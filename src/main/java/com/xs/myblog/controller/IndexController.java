package com.xs.myblog.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.BlogViews;
import com.xs.myblog.pojo.Comment;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public String index(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model, HttpSession session) {
        List<Blog> allBlog = blogService.getAllBlogAndUser();
        int size = allBlog.size();
        User user = (User) session.getAttribute("user");
        session.setAttribute("allBlogs", allBlog);
        if (user == null) {
            for (Blog blog : allBlog) {
                blog.setScore(getScoreDefault(blog));
            }
        } else {
            List<Long> friendIds = userService.getAllFriendIdByUserId(user.getId());//用户关注的博主id
            List<BlogViews> blogViews = blogService.getBlogViews(user.getId() + "");//用户浏览过的博文
            List<Long> blogViewIds = new ArrayList<>();
            for (BlogViews blogView : blogViews) {
                blogViewIds.add(Long.valueOf(blogView.getId()));
            }
            for (Blog blog : allBlog) {
                blog.setScore(getScoreByUser(user, blog, friendIds, blogViewIds));
            }
        }
        allBlog.sort((o1, o2) -> o2.getScore() - o1.getScore());
        PageInfo<Blog> pageInfo = new PageInfo<>(allBlog);
        model.addAttribute("page", pageInfo);
        model.addAttribute("types", typeService.listTypeTop(5));
        model.addAttribute("tags", tagService.listTagTop(8));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(6));
        model.addAttribute("size", size);
        return "index";
    }

    @RequestMapping("/search/{pageNum}")
    public String search(@PathVariable(required = false) String pageNum,
                         @RequestParam(required = false) String query,
                         Model model,HttpSession session) {
        Integer x = Integer.parseInt(pageNum);
        if (pageNum == null)
            x = 1;
        String q1 = (String) session.getAttribute("query");
        String q2 = (String) model.getAttribute("query");
        if (q1 != null)
            query = q1;
        if (q2 != null)
            query = q2;
        PageHelper.startPage(x, 5, "update_time desc");
        List<Blog> blogList = blogService.searchBlogByQuery(query);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
        model.addAttribute("query", query);
        model.addAttribute("page", pageInfo);
        session.setAttribute("query", query);

        return "search";
    }

    @RequestMapping("/search")
    public String search2(@RequestParam String query,
                          Model model,HttpSession session ) {
        PageHelper.startPage(1, 100, "update_time desc");
        List<Blog> blogList = blogService.searchBlogByQuery(query);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
        model.addAttribute("query", query);
        session.setAttribute("query", query);
        model.addAttribute("page", pageInfo);

        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model, HttpSession session) {
        model.addAttribute("blog", blogService.getAndConvert(id, session));
//        model.addAttribute("comments", commentService.listCommentByBlogId(id));
        session.setAttribute("isFriend", 0);
        User user = (User) session.getAttribute("user");
        session.setAttribute("isFriend", 0);
        session.setAttribute("isAdmin", 0);
        session.setAttribute("message", null);
        if (user != null) {
            Blog blog = blogService.getBlogById(id);
            Long adminId = blog.getUser().getId();//博主id
            List<Long> friendIds = userService.getAllFriendIdByUserId(user.getId());
            List<Integer> authority = userService.getAuthority(user.getId());
            if (user.getId() == adminId || friendIds.contains(adminId))
                session.setAttribute("isFriend", 1);
            if (user.getId() == -1)
                session.setAttribute("isAdmin", 1);
            if (adminId == user.getId() && authority.contains(4))
                session.setAttribute("isAdmin", 1);
            blogService.updateBlogViewsUsers(blog.getId(), user.getId());
        }
        return "blog";
    }

    @GetMapping("/footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }

    @GetMapping("/friend/{id}")
    public String follow(@PathVariable("id") Long id, HttpSession session, RedirectAttributes attributes, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            attributes.addFlashAttribute("message", "请先登录,才能关注博主");
            return "redirect:/admin/l";
        }
        Long userId = user.getId();//游客id
        Blog blog = blogService.getBlogById(id);
        Long adminId = blog.getUser().getId();//博主id
        //游客关注博主
        userService.addFriend(userId, adminId);
        model.addAttribute("blog", blogService.getAndConvert(id, session));
        session.setAttribute("isFriend", 1);

        return "redirect:/blog/" + blog.getId();
    }

    int getScoreDefault(Blog blog) {
        int score = 0;
        if (blog.isRecommend())
            score += 100;
        if (blog.getUser().getId() == -1)//管理员发布的博文
            score += 100;
        score += (blog.getViews() / 10);
        score += commentService.getCommentsCountById(blog.getId()) * 5;
        long creatTime = blog.getCreateTime().getTime();
        long time = (System.currentTimeMillis() - creatTime);
        long day = time / 1000 / 60 / 60 / 24;
        if (day == 0)
            score += 100;
        else if (day < 3)
            score += 30;
        else if (day < 7)
            score += 10;
        else if (day < 30)
            score += 5;
        return score;
    }

    int getScoreByUser(User user, Blog blog, List<Long> friendIds, List<Long> blogViewIds) {
        int score = getScoreDefault(blog);
        if (blog.getUser().getId() == user.getId())
            score -= 100;
        //该用户没看过管理员发布的博文
        if (blog.getUser().getId() == -1 && !blogViewIds.contains(blog.getId()))
            score += 5000;
        //1:本博文是用户关注的博主发布的博文且该用户没看过这篇博文
        if (friendIds.contains(blog.getUser().getId()) && !blogViewIds.contains(blog.getId()))
            score += 5000;
        //2:本博文是用户关注的博主发布的博文，但是该用户看过了这篇博文
        else if (friendIds.contains(blog.getUser().getId()) && blogViewIds.contains(blog.getId()))
            score += 10;
        //3:本博文不是用户关注的博主发布的博文，但是该用户没看过这篇博文
        else if (!friendIds.contains(blog.getUser().getId()) && !blogViewIds.contains(blog.getId()))
            score += 50;
        //4:本博文不是用户关注的博主发布的博文，该用户也看过这篇博文
        else if (!friendIds.contains(blog.getUser().getId()) && blogViewIds.contains(blog.getId()))
            score -= 100;

        if (user.getProvince() != null && blog.getUser().getProvince() != null) {
            if (blog.getUser().getProvince().equals(user.getProvince())) {
                score += 5;
                if (user.getCity() != null && blog.getUser().getCity() != null) {
                    if (blog.getUser().getCity().equals(user.getCity()))
                        score += 5;
                }
            }
        }
        return score;
    }
}

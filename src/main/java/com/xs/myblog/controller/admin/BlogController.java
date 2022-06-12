package com.xs.myblog.controller.admin;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.Tag;
import com.xs.myblog.pojo.Type;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.BlogService;
import com.xs.myblog.service.TagService;
import com.xs.myblog.service.TypeService;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/blogs")
public class BlogController {

    private final String UPDATE_BLOG = "admin/blogs-input";
    private final String LIST = "admin/blogs";
    private final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String blogs(Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String orderBy = "update_time desc";
        PageHelper.startPage(pageNum, 5, orderBy);
        Long id = user.getId();
        int isRoot = (Integer) session.getAttribute("root");
        List<Blog> list;
        if (id == -1 || isRoot == 1)
            list = blogService.getAllBlog();
        else
            list = blogService.getAllBlogAndUserById(Math.toIntExact(user.getId()));
        PageInfo<Blog> pageInfo = new PageInfo<>(list);
        model.addAttribute("user", user);
        model.addAttribute("id", user.getId());
        model.addAttribute("types", typeService.getAllType());
        model.addAttribute("page", pageInfo);
        return LIST;
    }

    @PostMapping("/search")
    public String search(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,@RequestParam(value = "username",required = false) String username,
                         @RequestParam(value = "typeId") Long typeId, Blog blog, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User thisUser = new User();
        thisUser.setId(user.getId());
        if (user.getId() < 0)
            thisUser.setId(null);
        if (username == null || username.equals(""))
            username = "";
        thisUser.setUsername(username);
        Type type = new Type();
        type.setId(typeId);
        blog.setType(type);
        blog.setUser(thisUser);

        PageHelper.startPage(pageNum, 5);
        List<Blog> blogList = blogService.searchBlog(blog);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
        model.addAttribute("page", pageInfo);
        return "admin/blogs :: blogList";//部分更新
    }

    @PostMapping("")
    public String addBlog(Blog blog, HttpSession session, RedirectAttributes attributes) {
        User user = (User) session.getAttribute("user");
        blog.setUser(user);
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        int count = 0;

        if (blog.getId() == null) {
            count =  blogService.saveBlog(blog);
            tagService.insertBlogTag(blog.getId(), blog.getTagIds());
        } else {
            count = blogService.updateBlog(blog.getId(), blog);
        }
        if (count == 0) {
            //新增失败
            attributes.addFlashAttribute("message", "操作失败");
        }else {
            Long typeId = blog.getType().getId();
            Long blogId = blog.getId();
            if (blog.getId() == null)
                blogService.insertBlogViews(blogId, typeId, "0");
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    @GetMapping("/{id}/input")
    public String editInput(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes attributes) {
        User user = (User) session.getAttribute("user");
        List<Integer> authority = userService.getAuthority(user.getId());
        if (!authority.contains(1)) {
            attributes.addFlashAttribute("message", "不具备编辑博客的权限");
            return REDIRECT_LIST;
        }
        setTypeAndTag(model);
        Blog blog = blogService.getBlogById(id);

        blog.init();
        model.addAttribute("blog", blog);
        return UPDATE_BLOG;
    }

    @GetMapping("/input")
    public String input(Model model, HttpSession session, RedirectAttributes attributes) {
        User user = (User) session.getAttribute("user");
        List<Integer> authority = userService.getAuthority(user.getId());
        if (!authority.contains(1)) {
            attributes.addFlashAttribute("message", "不具备编辑博客的权限");
            return REDIRECT_LIST;
        }
        model.addAttribute("blog", new Blog());
        setTypeAndTag(model);
        return UPDATE_BLOG;
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message");
        return REDIRECT_LIST;
    }

    @GetMapping("/{id}/editRecommend")
    public String editRecommend(@PathVariable Long id, RedirectAttributes attributes,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.getId() != -1)
            return REDIRECT_LIST;
        blogService.setRecommend(id);
        attributes.addFlashAttribute("message","操作成功");
        return REDIRECT_LIST;
    }

    @PostMapping("/types/{typeId}")
    public String getTags1(@PathVariable(value = "typeId") Long typeId,Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Tag> tags = typeService.getTagsByUserId(typeId, user.getId());
        model.addAttribute("tags", tags);
        session.setAttribute("tags", tags);
        return "admin/blogs-input :: tagUpdate";
    }

    @GetMapping("/types/{typeId}")
    public String getTags2(@PathVariable(value = "typeId") Long typeId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Tag> tags = typeService.getTagsByUserId(typeId, user.getId());
        model.addAttribute("tags", tags);
        session.setAttribute("tags", tags);
        return "admin/blogs-input :: tagUpdate";
    }


}

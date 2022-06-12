package com.xs.myblog.controller.admin;

import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.BlogViews;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.BlogService;
import com.xs.myblog.service.CommentService;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/rootData")
public class RootDataController {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;

    @GetMapping("")
    public String data(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        List<User> allUsers = userService.getAllUsers();
        session.setAttribute("userNumber", allUsers.size());

        List<Blog> allBlogAndUser = blogService.getAllBlogAndUser();
        session.setAttribute("blogNumber", allBlogAndUser.size());

        int allViews = 0;
        for (Blog blog : allBlogAndUser) {
            allViews += blog.getViews();
        }
        session.setAttribute("allViews", allViews);

        int commentsCount = commentService.getCommentsCount();
        session.setAttribute("commentsCount", commentsCount);

        return "admin/rootData";
    }

    @GetMapping("/userInfo")
    public String userInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        List<User> allUsers = userService.getAllUsers();
        HashMap<String, Integer> areas = new HashMap<>();
        for (User temp : allUsers) {
            String province = temp.getProvince();
            areas.put(province, areas.getOrDefault(province, 0) + 1);
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(areas.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        int allNumber = (Integer) session.getAttribute("userNumber");

        List<Integer> data = new ArrayList<>();
        List<String> place = new ArrayList<>();
        List<Double> nums = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            place.add(list.get(i).getKey());
            data.add(list.get(i).getValue());
            nums.add(list.get(i).getValue() * 1.0 / allNumber);
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", place);
        session.setAttribute("x", "用户来源地区");
        session.setAttribute("y", "人数");
        session.setAttribute("text_arr", place);
        session.setAttribute("data_arr", nums);
        session.setAttribute("text", "用户来源地区");
        return "admin/userData";
    }

    @GetMapping("/blogInfo")
    public String blogInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        Integer blogNumber = (Integer) session.getAttribute("blogNumber");
        List<Blog> blogs = blogService.getAllBlogAndUser();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < blogs.size(); i++) {
            String username = userService.getUserByIdOrName(blogs.get(i).getUser().getId(), null).getUsername();
            map.put(username, map.getOrDefault(username,0) + 1);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> data = new ArrayList<>();
        List<String> blogTitle = new ArrayList<>();
        List<Double> blogViews = new ArrayList<>();
        double otherPercent = 0;
        int otherNumber = 0;
        if (map.size() > 5) {
            for (int i = 0; i < 5 && list.get(i).getValue() > 0; i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                otherNumber += list.get(i).getValue();
                double percent = list.get(i).getValue() * 1.0 / blogNumber;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
            blogTitle.add("其它");
            data.add(blogNumber - otherNumber);
            otherPercent = otherPercent > 1 ? 1 : otherPercent;
            blogViews.add(Double.parseDouble(String.format("%.3f", 1 - otherPercent)));
        } else {
            for (int i = 0; i < map.size(); i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                double percent = list.get(i).getValue() * 1.0 / blogNumber;
                percent = Double.parseDouble(String.format("%.3f", percent));
                blogViews.add(percent);
            }
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", blogTitle);
        session.setAttribute("x", "用户名");
        session.setAttribute("y", "博文数");
        session.setAttribute("text_arr", blogTitle);
        session.setAttribute("data_arr", blogViews);
        session.setAttribute("text", "博主发布博文百分比");
        return "admin/userData";
    }

    @GetMapping("/blogViews")
    public String blogViews(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        Integer allViews = (Integer) session.getAttribute("allViews");
        List<Blog> blogs = blogService.getAllBlogAndUser();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < blogs.size(); i++) {
            String blogTitle = blogs.get(i).getTitle();
            map.put(blogTitle, blogs.get(i).getViews());
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> data = new ArrayList<>();
        List<String> blogTitle = new ArrayList<>();
        List<Double> blogViews = new ArrayList<>();
        double otherPercent = 0;
        int otherNumber = 0;
        if (map.size() > 5) {
            for (int i = 0; i < 5 && list.get(i).getValue() > 0; i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                otherNumber += list.get(i).getValue();
                double percent = list.get(i).getValue() * 1.0 / allViews;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
            blogTitle.add("其它");
            data.add(allViews - otherNumber);
            otherPercent = otherPercent > 1 ? 1 : otherPercent;
            blogViews.add(Double.parseDouble(String.format("%.3f", 1 - otherPercent)));
        } else {
            for (int i = 0; i < map.size(); i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                double percent = list.get(i).getValue() * 1.0 / allViews;
                percent = Double.parseDouble(String.format("%.3f", percent));
                blogViews.add(percent);
            }
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", blogTitle);
        session.setAttribute("x", "博文标题");
        session.setAttribute("y", "被浏览次数");
        session.setAttribute("text_arr", blogTitle);
        session.setAttribute("data_arr", blogViews);
        session.setAttribute("text", "博客被浏览百分比");
        return "admin/userData";
    }

    @GetMapping("/commentInfo")
    public String commentInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        Integer commentsCount = (Integer) session.getAttribute("commentsCount");
        List<Blog> blogs = blogService.getAllBlogAndUser();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < blogs.size(); i++) {
            String blogTitle = blogs.get(i).getTitle();
            map.put(blogTitle, commentService.getCommentsCountById(blogs.get(i).getId()));
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> data = new ArrayList<>();
        List<String> blogTitle = new ArrayList<>();
        List<Double> blogViews = new ArrayList<>();
        double otherPercent = 0;
        int otherNumber = 0;
        if (map.size() > 5) {
            for (int i = 0; i < 5 && list.get(i).getValue() > 0; i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                otherNumber += list.get(i).getValue();
                double percent = list.get(i).getValue() * 1.0 / commentsCount;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
            blogTitle.add("其它");
            data.add(commentsCount - otherNumber);
            otherPercent = otherPercent > 1 ? 1 : otherPercent;
            blogViews.add(Double.parseDouble(String.format("%.3f", 1 - otherPercent)));
        } else {
            for (int i = 0; i < map.size(); i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                double percent = list.get(i).getValue() * 1.0 / commentsCount;
                percent = Double.parseDouble(String.format("%.3f", percent));
                blogViews.add(percent);
            }
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", blogTitle);
        session.setAttribute("x", "博文标题");
        session.setAttribute("y", "评论量");
        session.setAttribute("text_arr", blogTitle);
        session.setAttribute("data_arr", blogViews);
        session.setAttribute("text", "博客评论数量百分比");
        return "admin/userData";
    }

    @GetMapping("/blogScore")
    public String blogScore(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        if (userId != -1)
            return "admin/data";
        Integer allScore = 0;
        List<Blog> blogs = blogService.getAllBlogAndUser();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < blogs.size(); i++) {
            String blogTitle = blogs.get(i).getTitle();
            int score = getScoreDefault(blogs.get(i));
            map.put(blogTitle, score);
            allScore += score;
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> data = new ArrayList<>();
        List<String> blogTitle = new ArrayList<>();
        List<Double> blogViews = new ArrayList<>();
        double otherPercent = 0;
        int otherNumber = 0;
        if (map.size() > 5) {
            for (int i = 0; i < 5 && list.get(i).getValue() > 0; i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                otherNumber += list.get(i).getValue();
                double percent = list.get(i).getValue() * 1.0 / allScore;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
            blogTitle.add("其它");
            data.add(allScore - otherNumber);
            otherPercent = otherPercent > 1 ? 1 : otherPercent;
            blogViews.add(Double.parseDouble(String.format("%.3f", 1 - otherPercent)));
        } else {
            for (int i = 0; i < map.size(); i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                double percent = list.get(i).getValue() * 1.0 / allScore;
                percent = Double.parseDouble(String.format("%.3f", percent));
                blogViews.add(percent);
            }
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", blogTitle);
        session.setAttribute("x", "博文标题");
        session.setAttribute("y", "综合分数");
        session.setAttribute("text_arr", blogTitle);
        session.setAttribute("data_arr", blogViews);
        session.setAttribute("text", "博客综合分数百分比");
        return "admin/userData";
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
}

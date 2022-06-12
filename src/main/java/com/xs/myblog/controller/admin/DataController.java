package com.xs.myblog.controller.admin;

import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.BlogViews;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.BlogService;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/admin/data")
public class DataController {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @GetMapping("")
    public String data(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        int fansNumber = userService.getFansNumber(userId);
        List<Blog> blogs = userService.getAllBlogsByUserId(userId);
        session.setAttribute("fansNumber", fansNumber);
        int views = 0;
        for (Blog blog : blogs) {
            views += blog.getViews();
        }
        List<BlogViews> blogViews = blogService.getBlogViews(userId + "");
        int seeViews = 0;
        for (int i = 0; i < blogViews.size(); i++) {
            String s = blogViews.get(i).getUserIds();
            String[] ids = s.split(",");
            for (int j = 0; j < ids.length; j++) {
                if (userId == Long.parseLong(ids[j]))
                    seeViews++;
            }
        }
        session.setAttribute("seeViews", seeViews);
        session.setAttribute("allViews", views);
        blogs.sort((o1, o2) -> o2.getViews() - o1.getViews());
        if (blogs.size() == 0) {
            session.setAttribute("bestBlog", "");
            model.addAttribute("blog", null);
        } else {
            session.setAttribute("bestBlog", blogs.get(0).getTitle());
            model.addAttribute("blog", blogs.get(0));
        }
        session.setAttribute("blogSize", blogs.size());
        return "admin/data";
    }

    @GetMapping("/fans")
    public String fans(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Long> allFriendIds = userService.getAllFanIds(user.getId());
        HashMap<String, Integer> areas = new HashMap<>();
        for (Long friendId : allFriendIds) {
            String province = userService.getUserByIdOrName(friendId,null).getProvince();
            areas.put(province, areas.getOrDefault(province, 0) + 1);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(areas.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        int allNumber = 0;
        for (int i = 0; i < list.size(); i++) {
            allNumber += list.get(i).getValue();
        }
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
        session.setAttribute("x", "粉丝来源地区");
        session.setAttribute("y", "人数");
        session.setAttribute("text_arr", place);
        session.setAttribute("data_arr", nums);
        session.setAttribute("text", "粉丝来源地区");
        return "admin/myData";
    }

    @GetMapping("/blogs")
    public String blogs(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Blog> blogs = blogService.getAllBlogAndUserById(Math.toIntExact(user.getId()));
        Integer allViews = (Integer) session.getAttribute("allViews");
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
            for (int i = 0; i < 5; i++) {
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
        return "admin/myData";
    }

    @GetMapping("/seeBlogs")
    public String seeBlogs(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Integer seeViews = (Integer) session.getAttribute("seeViews");
        Long userId = user.getId();
        List<BlogViews> blogViewsOne = blogService.getBlogViews(userId + "");

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0; i < blogViewsOne.size(); i++) {
            BlogViews temp = blogViewsOne.get(i);
            String title = blogService.getBlogById(temp.getBlogId()).getTitle();
            String s = blogViewsOne.get(i).getUserIds();
            int number = 0;
            String[] ids = s.split(",");
            for (int j = 0; j < ids.length; j++) {
                if (userId == Long.parseLong(ids[j]))
                    number++;
            }
            map.put(title, number);
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> data = new ArrayList<>();
        List<String> blogTitle = new ArrayList<>();
        List<Double> blogViews = new ArrayList<>();
        double otherPercent = 0;
        int otherNumber = 0;
        if (map.size() > 5) {
            for (int i = 0; i < 5; i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                otherNumber += list.get(i).getValue();
                double percent = list.get(i).getValue() * 1.0 / seeViews;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
            data.add(seeViews - otherNumber);
            blogTitle.add("其它");
            otherPercent = otherPercent > 1 ? 1 : otherPercent;
            blogViews.add(Double.parseDouble(String.format("%.3f", 1 - otherPercent)));
        } else {
            for (int i = 0; i < map.size(); i++) {
                blogTitle.add(list.get(i).getKey());
                data.add(list.get(i).getValue());
                double percent = list.get(i).getValue() * 1.0 / seeViews;
                percent = Double.parseDouble(String.format("%.3f", percent));
                otherPercent += percent;
                blogViews.add(percent);
            }
        }
        session.setAttribute("data", data);
        session.setAttribute("lateral", blogTitle);
        session.setAttribute("x", "博文标题");
        session.setAttribute("y", "浏览次数");
        session.setAttribute("text_arr", blogTitle);
        session.setAttribute("data_arr", blogViews);
        session.setAttribute("text", "浏览历史中各博客浏览次数百分比");
        return "admin/myData";
    }
}

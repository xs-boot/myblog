package com.xs.myblog.controller.admin;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.Type;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.BlogService;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @GetMapping("")
    public String users(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.getId() != -1)
            return "admin/index";
        List<User> allUsers = userService.getAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            Long id = allUsers.get(i).getId();
            List<Integer> authority = userService.getAuthority(id);
            if (authority.contains(1))
                allUsers.get(i).setWrite(1);
            else
                allUsers.get(i).setWrite(0);
            if (authority.contains(2))
                allUsers.get(i).setSpeak(1);
            else
                allUsers.get(i).setSpeak(0);
            if (authority.contains(4))
                allUsers.get(i).setComment(1);
            else
                allUsers.get(i).setComment(0);
        }
        PageHelper.startPage(pageNum, 5);
        List<User> users = allUsers;
        PageInfo<User> pageInfo = new PageInfo<>(users);
        model.addAttribute("users", users);
        model.addAttribute("page", pageInfo);
        return "admin/authorityController";
    }

    @PostMapping("/search")
    public String search(@RequestParam(value = "username",required = false) String username,
                         @RequestParam(value = "edit",required = false) boolean edit,
                         @RequestParam(value = "speak",required = false) boolean speak,
                         @RequestParam(value = "comment",required = false) boolean comment,
                         Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<User> allUsers = userService.getUsersLikeName(username);
        for (int i = 0; i < allUsers.size(); i++) {
            Long id = allUsers.get(i).getId();
            List<Integer> authority = userService.getAuthority(id);
            if (authority.contains(1))
                allUsers.get(i).setWrite(1);
            else
                allUsers.get(i).setWrite(0);
            if (authority.contains(2))
                allUsers.get(i).setSpeak(1);
            else
                allUsers.get(i).setSpeak(0);
            if (authority.contains(4))
                allUsers.get(i).setComment(1);
            else
                allUsers.get(i).setComment(0);
        }
        for (int i = 0; i < allUsers.size(); i++) {
            if (edit && allUsers.get(i).getWrite() == 0) {
                allUsers.remove(i);
                i--;
                continue;
            }
            if (speak && allUsers.get(i).getSpeak() == 0) {
                allUsers.remove(i);
                i--;
                continue;
            }
            if (comment && allUsers.get(i).getComment() == 0) {
                allUsers.remove(i);
                i--;
            }
        }
        model.addAttribute("users", allUsers);
//        model.addAttribute("page", pageInfo);
        return "admin/authorityController :: authorityList";
    }

    @GetMapping("/listInformation")
    public String listInformation(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<String> authorityName = userService.getAuthorityName(user.getId());
        StringBuilder authority = new StringBuilder();
        for (int i = 0; i < authorityName.size(); i++) {
            authority.append(authorityName.get(i));
            if (i != authorityName.size() - 1)
                authority.append(",  ");
        }
        User userByIdOrName = userService.getUserByIdOrName(user.getId(), null);
        session.setAttribute("user", userByIdOrName);
        session.setAttribute("authority", authority.toString());
        return "admin/information";
    }

    @GetMapping("/getHomePage/{id}")
    public String getHomePage(HttpSession session, @PathVariable(value = "id") Long id, Model model) {

        User userByIdOrName = userService.getUserByIdOrName(id, null);
        session.setAttribute("otherUser", userByIdOrName);

        List<Blog> blogs = blogService.findAllByUserAndPublish(Math.toIntExact(id));

        int size = blogs.size();
        User user = (User) session.getAttribute("user");
        session.setAttribute("allBlogs", blogs);

        PageInfo<Blog> pageInfo = new PageInfo<>(blogs);
        model.addAttribute("page", pageInfo);
        model.addAttribute("size", size);

        return "admin/homePage";
    }


    @GetMapping("/{uId}/{aId}/delete")
    public String delete(@PathVariable(value = "uId") Long uId, @PathVariable(value = "aId") Long aId, RedirectAttributes attributes) {
        userService.deleteAuthority(uId, Math.toIntExact(aId));
        return "redirect:/admin/users";
    }

    @GetMapping("/{uId}/{aId}/add")
    public String add(@PathVariable(value = "uId") Long uId, @PathVariable(value = "aId") Long aId, RedirectAttributes attributes) {
        userService.addAuthority(uId, Math.toIntExact(aId));
        return "redirect:/admin/users";
    }



}

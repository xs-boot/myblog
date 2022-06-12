package com.xs.myblog.controller.admin;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String friends(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        List<Long> allFriendIdByUserId = userService.getAllFriendIdByUserId(userId);
        List<User> friends = new ArrayList<>();
        for (int i = 0; i < allFriendIdByUserId.size(); i++) {
            User temp = userService.getUserByIdOrName(allFriendIdByUserId.get(i), null);
            friends.add(temp);
        }
        PageHelper.startPage(pageNum, 5);
        List<User> users = friends;
        PageInfo<User> pageInfo = new PageInfo<>(users);
        model.addAttribute("page", pageInfo);
        return "admin/friends";
    }


    @GetMapping("/delete/{id}")
    public String delete1(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        userService.deleteFriend(userId, id);
        return "redirect:/admin/friends";
    }

    @PostMapping("/search")
    public String search(@RequestParam(value = "nickname", required = false) String nickname,
                         @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getId();
        List<Long> allFriendIdByUserId = userService.getAllFriendIdByUserId(userId);
        List<User> friends = new ArrayList<>();
        for (int i = 0; i < allFriendIdByUserId.size(); i++) {
            User temp = userService.getUserByIdOrName(allFriendIdByUserId.get(i), null);
            if (temp.getNickname().contains(nickname))
                friends.add(temp);
        }
        PageHelper.startPage(pageNum, 5);
        List<User> users = friends;
        PageInfo<User> pageInfo = new PageInfo<>(users);
        model.addAttribute("page", pageInfo);
        return "admin/friends :: friendList";
    }



}

package com.xs.myblog.controller.admin;


import com.xs.myblog.pojo.User;
import com.xs.myblog.service.UserService;
import com.xs.myblog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/l")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/r")
    public String registerPage() {
        return "admin/register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes attributes) {
        User user = userService.checkUser(username, password);
        if (user == null) {
            user = userService.getUserByEmailAndPassword(username, password);
        }
        session.setAttribute("write", 0);
        session.setAttribute("speak", 0);
        session.setAttribute("root", 0);
        session.setAttribute("comment", 0);
        if (user != null) {
            user.setPassword(null);//不保存密码
            List<Integer> authority = userService.getAuthority(user.getId());
            if (authority.contains(1))
                session.setAttribute("write", 1);
            if (authority.contains(2))
                session.setAttribute("speak", 1);
            if (authority.contains(3))
                session.setAttribute("root", 1);
            if (authority.contains(4))
                session.setAttribute("comment", 1);
            session.setAttribute("user", user);
            return "/admin/index";
        } else {
            attributes.addFlashAttribute("message", "用户名或密码错误");
            return "redirect:/admin/l";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password1,@RequestParam String password2,
                           @RequestParam String email, HttpSession session, RedirectAttributes attributes) {
        if (isExitUserName(username)) {
            attributes.addFlashAttribute("message", "用户名已存在！");
            return "redirect:/admin/r";
        }
        if (isExitEmail(email)) {
            attributes.addFlashAttribute("message", "邮箱已被别人注册！");
            return "redirect:/admin/r";
        }
        if (password1.contains(" ")) {
            attributes.addFlashAttribute("message", "密码不能含有空格！");
            return "redirect:/admin/r";
        }
        if (password1.length() < 6) {
            attributes.addFlashAttribute("message", "密码长度需在6位以上");
            return "redirect:/admin/r";
        }
        if (!password1.equals(password2)) {
            attributes.addFlashAttribute("message", "两次密码不一致！");
            return "redirect:/admin/r";
        }

        User newUser = new User();
        newUser.setUsername(username);
        userService.registerUser(username, password1, email);
        User userByIdOrName = userService.getUserByIdOrName(null, username);
        userService.addAuthority(userByIdOrName.getId(), 1);
        userService.addAuthority(userByIdOrName.getId(), 2);
        session.setAttribute("user", newUser);
        return "/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(String password0, String password1, String password2,
                                 HttpSession session, RedirectAttributes attributes) {
        User tempUser = (User) session.getAttribute("user");
        String passwordByUserId = userService.getPasswordByUserId(tempUser.getId());
        if (!MD5Utils.code(password0).equals(passwordByUserId)) {
            attributes.addFlashAttribute("message", "输入的旧密码不准确");
            return "redirect:/admin/updatePassword1";
        }
        if (password1 == null || password2 == null || password1.equals("") || password2.equals("")) {
            attributes.addFlashAttribute("message", "密码不能为空");
            return "redirect:/admin/updatePassword1";
        }
        if (password1.length() < 6) {
            attributes.addFlashAttribute("message", "密码长度不能小于6");
            return "redirect:/admin/updatePassword1";
        }
        if (!password1.equals(password2)) {
            attributes.addFlashAttribute("message", "两次密码不一致");
            return "redirect:/admin/updatePassword1";
        }
        attributes.addFlashAttribute("message", "密码更新成功，请重新登录");
        String code = MD5Utils.code(password1);
        User user = (User) session.getAttribute("user");
        userService.updatePassword(user.getId(), code);
        return "redirect:/admin/index";
    }

    @GetMapping("/updatePassword1")
    public String updatePassword1() {
        return "/admin/updatePassword";
    }

    @GetMapping("/selfUpdate")
    public String selfUpdate() {
        return "/admin/self-information";
    }

    @PostMapping("/updateInformation")
    public String updateInformation(String avatar, String email, String nickname, String province, String city,
                                    HttpSession session, Model model, RedirectAttributes attributes) {
        if (isExitEmail(email)) {
            attributes.addFlashAttribute("message", "邮箱已被别人注册！");
            return "redirect:/admin/index";
        }
        User user = (User) session.getAttribute("user");
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProvince(province);
        user.setCity(city);
        userService.updateInformation(user);
        session.setAttribute("user", user);
        model.addAttribute("user", user);
        return "/admin/index";
    }

    boolean isExitUserName(String username) {
        User user = userService.getUserByIdOrName(null, username);
        return user != null;
    }

    boolean isExitEmail(String email) {
        List<User> allUsers = userService.getAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getEmail().equals(email))
                return true;
        }
        return false;
    }
}

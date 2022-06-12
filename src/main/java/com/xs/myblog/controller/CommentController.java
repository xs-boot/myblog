package com.xs.myblog.controller;


import com.xs.myblog.pojo.Blog;
import com.xs.myblog.pojo.Comment;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.BlogService;
import com.xs.myblog.service.CommentService;
import com.xs.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private UserService userService;

//    @Value("${comment.avatar}")
    private final String avatar = "/images/avatar.png";

    @GetMapping("/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }

    @PostMapping("")
    public String post(Comment comment, HttpSession session, RedirectAttributes attributes) {
        System.out.println("====================================================================");
        Long blogId = comment.getBlog().getId();
        Blog blog = blogService.getBlogById(blogId);
        comment.setBlog(blogService.getBlogById(blogId));
        User user = (User) session.getAttribute("user");
        session.setAttribute("message", null);
        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(blog.getUser().getId() == user.getId());
            List<Integer> authority = userService.getAuthority(user.getId());
            if (!authority.contains(2)) {
                attributes.addFlashAttribute("message", "不具备发表评论的权限");
                session.setAttribute("message", "不具备发表评论的权限");
                return "redirect:/comments/" + blogId;
            }
        } else {
//            attributes.addFlashAttribute("message", "请先登录再发表评论");
            session.setAttribute("message", "请先登录再发表评论");
            return "redirect:/comments/" + blogId;
        }

        commentService.saveComment(comment);
        return "redirect:/comments/" + blogId;
    }

    @RequestMapping("/updateTop/{top}/{commentId}/{blogId}")
    public String updateTop(@PathVariable Integer top, @PathVariable Long commentId,
                            @PathVariable Long blogId,Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        commentService.updateCommentTop(top,commentId);
        Blog blog = blogService.getAllBlogAndUserById(Math.toIntExact(user.getId()), blogId);

        List<Comment> comments = commentService.listCommentByBlogId(blogId);
        model.addAttribute("comments", comments);
        model.addAttribute("blog", blog);
        return "redirect:/blog/" + blogId;
    }

    //删除评论
    @RequestMapping("/{blogId}/{id}/delete")
    public String delete(@PathVariable Long blogId, @PathVariable Long id, Comment comment, Model model) {
        System.out.println(blogId + " " + id);
        commentService.deleteComment(comment, id);

        Blog blog = blogService.getAllBlogAndUserById(1, blogId);
        List<Comment> comments = commentService.listCommentByBlogId(blogId);
        model.addAttribute("comments", comments);
        model.addAttribute("blog", blog);

        return "redirect:/blog/" + blogId;
    }
}

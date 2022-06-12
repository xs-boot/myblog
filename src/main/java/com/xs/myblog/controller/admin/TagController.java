package com.xs.myblog.controller.admin;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.Tag;
import com.xs.myblog.pojo.Type;
import com.xs.myblog.pojo.User;
import com.xs.myblog.service.TagService;
import com.xs.myblog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/tags")
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private TypeService typeService;

    int pageSize = 8;

    @GetMapping("")
    public String tags(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> tags = tagService.findAllTagAndType(user.getId() == -1 ? null : user.getId());
        PageInfo<Tag> pageInfo = new PageInfo<>(tags);
        model.addAttribute("page", pageInfo);
        return "admin/tags";
    }

    @PostMapping("/search")
    public String search(@RequestParam(value = "tagname", required = false) String tagname,
                         @RequestParam(value = "isMine", required = false) boolean isMine,
                         @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> tags = tagService.searchTag(tagname, isMine, user.getId());
        PageInfo<Tag> pageInfo = new PageInfo<>(tags);
        model.addAttribute("page", pageInfo);
        return "admin/tags :: tagList";
    }

    @GetMapping("/input")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        model.addAttribute("types", typeService.listType());
        return "admin/tags-input";
    }

    @GetMapping("/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        model.addAttribute("types", typeService.listType());
        return "admin/tags-input";
    }


    @PostMapping()
    public String post(@Valid Tag tag, Long typeId, BindingResult result, RedirectAttributes attributes,HttpSession session) {
        if (typeId == null) {
            attributes.addFlashAttribute("message", "分类不能为空");
            return "redirect:/admin/tags";
        }
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的标签");
        }
        if (result.hasErrors()) {
            return "admin/tags-input";
        }
        User user = (User) session.getAttribute("user");
        tag.setUserId(user.getId());
        int t = tagService.saveTag(tag);
        Tag tagByName = tagService.getTagByName(tag.getName());
        tagService.addTypeTag(typeId, tagByName.getId());
        if (t == 0) {
            attributes.addFlashAttribute("message", "新增失败");
        } else {
            attributes.addFlashAttribute("message", "新增成功");
        }
        return "redirect:/admin/tags";
    }


    @PostMapping("/{id}")
    public String editPost(@Valid Tag tag, @RequestParam(required = false) Long typeId, BindingResult result, @PathVariable Long id, RedirectAttributes attributes, Model model) {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "不能添加重复的标签");
        }
        if (result.hasErrors()) {
            return "admin/tags-input";
        }
        int t = tagService.updateTag(id, tag);

        if (t == 0) {
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            attributes.addFlashAttribute("message", "更新成功");
        }

        return "redirect:/admin/tags";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        int count = tagService.deleteTag(id);
        if (count == -1) {
            attributes.addFlashAttribute("message", "删除失败,已经有博客使用了该标签");
        }else
            attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/tags";
    }


}

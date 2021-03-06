package com.xs.myblog.controller.admin;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.myblog.pojo.Tag;
import com.xs.myblog.pojo.Type;
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
@RequestMapping("/admin/types")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("")
    public String list(Model model,
                       @RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum) {
        PageHelper.startPage(pageNum, 5);
        List<Type> types = typeService.listType();
        PageInfo<Type> pageInfo = new PageInfo<>(types);
        model.addAttribute("page", pageInfo);
        return "admin/types";
    }

    @PostMapping("/search")
    public String search(@RequestParam(value = "typename", required = false) String typename,
                         @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, HttpSession session, Model model) {
        PageHelper.startPage(pageNum, 5);
        List<Type> types = typeService.findAllByName(typename);
        PageInfo<Type> pageInfo = new PageInfo<>(types);
        model.addAttribute("page", pageInfo);
        return "admin/types :: typeList";
    }

//    @GetMapping("/{typeId}")
//    public String getTags(@PathVariable(value = "typeId") Long typeId,Model model) {
//        List<Tag> tags = typeService.getTags(typeId);
//        model.addAttribute("tags", tags);
//        return "admin/types-input";
//    }

    @GetMapping("/input")
    public String input(Type type ,Model model) {
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    @GetMapping("/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.getType(id));
        return "admin/types-input";
    }

    @PostMapping("")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes){
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name", "nameError", "???????????????????????????");
        }
        if (result.hasErrors()) {
            return "admin/types-input";
        }
        int t = typeService.saveType(type);
        if (t == 0) {
            //????????????
            attributes.addFlashAttribute("message", "????????????");
        }else {
            attributes.addFlashAttribute("message", "????????????");
        }
        return "redirect:/admin/types";
    }

    @PostMapping("/{id}")
    public String editPost(@Valid Type type,@PathVariable Long id, BindingResult result, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name", "nameError", "???????????????????????????");
        }
        if (result.hasErrors()) {
            return "admin/types-input";
        }

        int t = typeService.updateType(id,type);

        if (t == 0) {
            attributes.addFlashAttribute("message", "????????????");
        } else {
            attributes.addFlashAttribute("message", "????????????");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/{id}/delete")
    public String deleteType(@PathVariable Long id,RedirectAttributes attributes) {
        int count = typeService.deleteType(id);
        if (count != -1)
            attributes.addFlashAttribute("message", "????????????");
        else
            attributes.addFlashAttribute("message", "????????????,???????????????????????????");

        return "redirect:/admin/types";
    }
}

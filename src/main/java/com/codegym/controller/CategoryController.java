package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Note;
import com.codegym.service.CategoryService;
import com.codegym.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ModelAndView listCategories() {
        Iterable<Category> categories = categoryService.findAll();
        ModelAndView modelAndView = new ModelAndView("/categories/list");
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/category-create")
    public ModelAndView createCategory() {
        ModelAndView modelAndView = new ModelAndView("/categories/create");
        modelAndView.addObject("category", new Category());
        return modelAndView;
    }

    @PostMapping("/category-create")
    public ModelAndView createCategory(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        ModelAndView modelAndView = new ModelAndView("/categories/create");
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("message", "New category was added!");
        return modelAndView;
    }

    @GetMapping("/category-edit/{id}")
    public ModelAndView editCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/categories/edit");
        modelAndView.addObject("category", category);
        return modelAndView;
    }

    @PostMapping("/category-edit")
    public ModelAndView editCategory(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        ModelAndView modelAndView = new ModelAndView("/categories/edit");
        modelAndView.addObject("message", "Category was edited");
        return modelAndView;
    }

    @GetMapping("/category-delete/{id}")
    public ModelAndView deleteCategory(@PathVariable Long id, Pageable pageable, RedirectAttributes redirectAttributes) {
        Category category = categoryService.findById(id);
        Page<Note> notes = noteService.findAllByCategory(category,pageable);
        List<Note> notesList = notes.getContent();
        ModelAndView modelAndView = new ModelAndView("redirect:/categories");
        boolean checkIsEmptyNoteInCategory = notesList.isEmpty();
        if (!checkIsEmptyNoteInCategory) {
            redirectAttributes.addFlashAttribute("message", "Can't delete! Please delete all notes of this category before.");
        } else {
            categoryService.remove(id);
            redirectAttributes.addFlashAttribute("message", "Category was deleted");
        }
        return modelAndView;
    }

    @Autowired
    public NoteService noteService;

    @GetMapping("/category-view/{id}")
    public ModelAndView viewCategory(@PathVariable Long id, Pageable pageable) {
        Category category = categoryService.findById(id);
        Page<Note> notes = noteService.findAllByCategory(category,pageable);
        List<Note> notesList = notes.getContent();
        ModelAndView modelAndView;
        boolean checkIsEmptyNoteInCategory = notesList.isEmpty();
        if (checkIsEmptyNoteInCategory) {
            modelAndView = new ModelAndView("/categories/view-404");
            modelAndView.addObject("category", category);
            modelAndView.addObject("message", "No notes found!");
        } else {
            modelAndView = new ModelAndView("/categories/view");
            modelAndView.addObject("category", category);
            modelAndView.addObject("notes", notes);
        }
        return modelAndView;
    }
}

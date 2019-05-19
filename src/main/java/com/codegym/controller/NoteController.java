package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Note;
import com.codegym.service.CategoryService;
import com.codegym.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class NoteController {
    @Autowired
    public NoteService noteService;

    @Autowired
    public CategoryService categoryService;

    @ModelAttribute("categories")
    public Iterable<Category> categories() {
        return categoryService.findAll();
    }

    @GetMapping("/notes")
    public ModelAndView listNote(@RequestParam("categories") Optional <Long> category_id,
                                 @RequestParam("findByTitle") Optional <String> findByTitle,
                                 @PageableDefault(value = 5) Pageable pageable) {
        Page<Note> notes;
        ModelAndView modelAndView = new ModelAndView("notes/list");

        if (findByTitle.isPresent()) {
            notes = noteService.findAllByTitleContaining(findByTitle.get(), pageable);
        } else {
            if (category_id.isPresent()) {
                Category category = categoryService.findById(category_id.get());
                notes = noteService.findAllByCategory(category, pageable);
                modelAndView.addObject("category_id", category_id.get());
            } else {
                notes = noteService.findAll(pageable);
            }
        }
        modelAndView.addObject("notes", notes);

        return modelAndView;
    }

    @GetMapping("/create-note")
    public ModelAndView createNote() {
        ModelAndView modelAndView = new ModelAndView("notes/create");
        modelAndView.addObject("note", new Note());
        return modelAndView;
    }

    @PostMapping("/create-note")
    public ModelAndView createNote(@ModelAttribute("note") Note note) {
        noteService.save(note);
        ModelAndView modelAndView = new ModelAndView("notes/create");
        modelAndView.addObject("note", new Note());
        modelAndView.addObject("message", "New note was added.");
        return modelAndView;
    }

    @GetMapping("/note-edit/{id}")
    public ModelAndView editNote(@PathVariable Long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("notes/edit");
        modelAndView.addObject("note", note);
        return modelAndView;
    }

    @PostMapping("/note-edit")
    public ModelAndView editNote(@ModelAttribute("note") Note note) {
        noteService.save(note);
        ModelAndView modelAndView = new ModelAndView("notes/edit");
        modelAndView.addObject("message", "This note was updated.");
        return modelAndView;
    }

    @GetMapping("/note-delete/{id}")
    public ModelAndView deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        noteService.remove(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        redirectAttributes.addFlashAttribute("message", "Note was deleted.");
        return modelAndView;
    }

    @GetMapping("/note-view/{id}")
    public ModelAndView viewNote(@PathVariable Long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("notes/view");
        modelAndView.addObject("note", note);
        return modelAndView;
    }
}

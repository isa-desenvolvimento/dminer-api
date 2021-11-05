package com.dminer.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search-all")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SearchAll {
    

    @GetMapping
    public String getAll() {
        return "aqui vai uma pesquisa";
    }
}

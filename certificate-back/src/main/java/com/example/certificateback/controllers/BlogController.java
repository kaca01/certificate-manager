package com.example.certificateback.controllers;

import com.example.certificateback.domains.Blog;
import com.example.certificateback.repositories.IBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlogController {

    @Autowired
    IBlogRepository iBlogRepository;

    @GetMapping("/blog")
    public List<Blog> get() {
        System.out.println("BLOGS");
        for (Blog blog : iBlogRepository.findAll()) {
            System.out.println(blog.toString());
        }
        return iBlogRepository.findAll();
    }
}

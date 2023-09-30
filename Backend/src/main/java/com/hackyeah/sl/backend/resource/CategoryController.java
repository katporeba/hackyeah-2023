package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.Category;
import com.hackyeah.sl.backend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping(path = {"/category"})
public class CategoryController {

    private CategoryRepository categoryRepository;
    @GetMapping("/list")
    public ResponseEntity<List<Category>> userList() {
        List<Category> categories = categoryRepository.findAll();
        return new ResponseEntity<>(categories, OK);
    }
}

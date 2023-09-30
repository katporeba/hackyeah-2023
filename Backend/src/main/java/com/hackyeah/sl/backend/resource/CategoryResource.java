package com.hackyeah.sl.backend.resource;

import com.hackyeah.sl.backend.domain.Category;
import com.hackyeah.sl.backend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping(path = {"/category"})
public class CategoryResource {

    private CategoryRepository categoryRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> userList() {
        List<Category> categories = categoryRepository.findAll();
        return new ResponseEntity<>(categories, OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    @DeleteMapping("/delete/{name}")
    public ResponseEntity deleteCategory(@PathVariable(name = "name") String name) {
        categoryRepository.removeByName(name);
        return new ResponseEntity<>(OK);
    }

    @PutMapping("/edit/{name}")
    public ResponseEntity<Category> editCategory(
            @PathVariable String name,
            @RequestBody Category updatedCategory) {

        Optional<Category> existingCategory = categoryRepository.findByName(name);

        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());

            Category savedCategory = categoryRepository.save(category);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}

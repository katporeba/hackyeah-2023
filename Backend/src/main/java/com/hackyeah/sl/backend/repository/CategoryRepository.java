package com.hackyeah.sl.backend.repository;

import com.hackyeah.sl.backend.domain.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {
    void removeByName(String name);
    Optional<Category> findByName(String name);
}
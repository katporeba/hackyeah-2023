package com.hackyeah.sl.backend.repository;

import com.hackyeah.sl.backend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
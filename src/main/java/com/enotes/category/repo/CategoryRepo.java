package com.enotes.category.repo;

import com.enotes.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {
    // 1. For user dropdowns
    List<Category> findByIsActiveTrue();

    // 2. For admin listing inactive categories
    List<Category> findByIsActiveFalse();

    // 3. For duplicate check
    Optional<Category> findByName(String name);

    // 4. For validating category is ACTIVE (specific use case)
    Optional<Category> findByIdAndIsActiveTrue(Integer categoryId);
}

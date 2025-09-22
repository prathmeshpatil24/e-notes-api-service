package com.enotes.repo;

import com.enotes.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

    List<Category>findByIsActiveTrueAndIsDeletedFalse();

    Optional<Category>findByIdAndIsDeletedFalse(Integer categoryId);

    Optional<Category>findByIdAndIsActiveTrueAndIsDeletedFalse(Integer categoryId);

    Optional<Category>findByName(String name);


}

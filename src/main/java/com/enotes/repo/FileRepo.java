package com.enotes.repo;

import com.enotes.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<Integer, FileEntity> {
}

package com.enotes.repo;

import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileEntity, Integer> {

//    Optional<FileEntity> findByFileNameAndIsDeletedFalse(String fileName);

    Optional<FileEntity> findByNotesIdAndFileNameAndIsDeletedFalse(Integer notesId, String fileName);

    Optional<FileEntity>findByIdAndNotesIdAndIsDeletedFalse(Integer fileId, Integer notesId);

    Optional<FileEntity>findByIdAndNotesIdAndIsDeletedTrue(Integer fileId, Integer notesId);

    List<FileEntity> findAllByDeletedTrue();
}

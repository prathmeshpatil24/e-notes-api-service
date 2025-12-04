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

    Optional<FileEntity> findByFileIdAndNotesIdAndIsDeletedFalse(Integer fileId, Integer notesId);

    Optional<FileEntity> findByFileIdAndNotesIdAndIsDeletedTrue(Integer fileId, Integer notesId);

    List<FileEntity> findAllByIsDeletedTrue();

    List<FileEntity> findByNotesIdAndIsDeletedFalse(Integer notesId);

    //    SELECT f.*
//    FROM file_entity f
//    JOIN notes n ON f.notes_id = n.id
//    WHERE f.is_deleted = true
//    AND n.is_deleted = false
//    AND n.created_by = :userId;
    List<FileEntity> findByIsDeletedTrueAndNotes_IsDeletedFalseAndNotes_CreatedBy(Integer userId);

    /*SELECT f.*
     FROM file_details f
     JOIN notes n ON f.notes_id = n.id
     WHERE f.file_id = :fileId
     AND f.notes_id = :noteId
     AND f.created_by = :createdBy
     AND f.is_deleted = TRUE;
     */
    Optional<FileEntity> findByFileIdAndNotes_IdAndCreatedByAndIsDeletedTrue(
            Integer fileId,
            Integer noteId,
            Integer createdBy
    );


}

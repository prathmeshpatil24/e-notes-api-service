package com.enotes.repo;

import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileEntity, Integer> {

//    Optional<FileEntity> findByFileNameAndIsDeletedFalse(String fileName);

    Optional<FileEntity> findByNotesIdAndFileNameAndIsDeletedFalse(Integer notesId, String fileName);

    Optional<FileEntity> findByFileIdAndNotesIdAndIsDeletedFalse(Integer fileId, Integer notesId);

    Optional<FileEntity> findByFileIdAndNotesIdAndIsDeletedTrue(Integer fileId, Integer notesId);

    List<FileEntity>findByCreatedByAndIsDeletedTrue(Integer userId);

    List<FileEntity> findByNotesIdAndIsDeletedFalse(Integer notesId);


    //for recycle bin
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


    /*
✅ Meaning of deleted_at < :cutoff
👉 If deleted_at is older than cutoff

➡️ The item WILL be auto deleted by the scheduler.

👉 If deleted_at is NOT older than cutoff

➡️ The item WILL NOT be auto deleted and will stay in recycle bin.
* Today = Feb 1
Cutoff = today − 28 days = Jan 4

deleted_at_date	  Check	                    Auto Delete?
Dec 20	          Dec 20 < Jan 4	          ✔ YES
Jan 1	          Jan 1 < Jan 4	              ✔ YES
Jan 4	          Jan 4 < Jan 4 → false	      ❌ NO
Jan 20	          Jan 20 < Jan 4 → false      ❌ NO
Jan 30	          Jan 30 < Jan 4 → false      ❌ NO
*/
    @Query("SELECT f FROM FileEntity f WHERE f.isDeleted = TRUE AND f.deletedAt < :cutoff")
    List<FileEntity> findExpiredFiles(@Param("cutoff") LocalDateTime cutoff);

}

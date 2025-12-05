package com.enotes.repo;

import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepo extends JpaRepository<Notes, Integer> {

    Optional<Notes>findByIdAndCreatedBy(Integer noteId, Integer userId);

    Optional<Notes> findByTitle(String notesTitle);

    Optional<Notes> findByIdAndIsDeletedFalse(Integer notesId);

    Page<Notes> findByCreatedByAndIsDeletedFalse(Integer userId, Pageable pageable);

    //recycle bin list
    List<Notes> findByCreatedByAndIsDeletedTrue(Integer userId);
    //Page<Notes>findByCreatedByAndIsDeletedTrue(Integer userId ,Pageable pageable);

    // restoring/deleting the deleted notes with file
    Optional<Notes> findByIdAndCreatedByAndIsDeletedTrue(Integer notesId, Integer userId);

    @Query("SELECT n FROM Notes n WHERE n.isDeleted = TRUE AND n.deletedAt < :cutoff")
    List<Notes> findExpiredFiles(@Param("cutoff") LocalDateTime cutoff);

    //list of all fav notes as per userID
    @Query("SELECT n FROM Notes n WHERE n.createdBy = :userId AND n.isFavorite = TRUE AND n.isDeleted = FALSE")
    Page<Notes> findAllFavoriteNotes(@Param("userId") Integer userId, Pageable pageable);
}

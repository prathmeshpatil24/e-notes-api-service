package com.enotes.repo;

import com.enotes.entity.Priority;
import com.enotes.entity.ToDo;
import com.enotes.entity.TodoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToDoRepo extends JpaRepository<ToDoRepo, Integer> {

    // 1. Get all active todos for user
    Page<ToDo> findByCreatedByAndIsDeletedFalse(Integer createdBy, Pageable pageable);

    // 2. Get all todos by status
    Page<ToDo> findByCreatedByAndStatusAndIsDeletedFalse(Integer createdBy, TodoStatus status, Pageable pageable);

    // 3. Get all todos by priority
    Page<ToDo> findByCreatedByAndPriorityAndIsDeletedFalse(Integer createdBy, Priority priority, Pageable pageable);

    // 4. Single todo (active only)
    Optional<ToDo> findByIdAndCreatedByAndIsDeletedFalse(Integer id, Integer createdBy);

    // 5. Soft delete → find only active
    Optional<ToDo> findByIdAndIsDeletedFalse(Integer id);

    // 6. Get all deleted (bin)
    Page<ToDo> findByCreatedByAndIsDeletedTrue(Integer createdBy, Pageable pageable);

    // 7. Restore → find deleted
    Optional<ToDo> findByIdAndIsDeletedTrue(Integer id);

    // 8. Hard delete → find deleted only
    Optional<ToDo> findByIdAndCreatedByAndIsDeletedTrue(Integer id, Integer createdBy);

    // 9. Clear bin
    @Modifying
    @Query("DELETE FROM ToDo t WHERE t.createdBy = :createdBy AND t.isDeleted = TRUE")
    void deleteAllDeletedByUser(@Param("createdBy") Integer createdBy);

    // 10. Summary counts
    Long countByCreatedBy(Integer createdBy);

    Long countByCreatedByAndStatus(Integer createdBy, TodoStatus status);

    Long countByCreatedByAndPriority(Integer createdBy, Priority priority);
}

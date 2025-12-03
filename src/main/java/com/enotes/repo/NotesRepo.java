package com.enotes.repo;

import com.enotes.entity.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepo extends JpaRepository<Notes, Integer> {

  Optional<Notes>findByTitle(String notesTitle);

  Optional<Notes>findByIdAndIsDeletedFalse(Integer notesId);

  Page<Notes>findByCreatedByAndIsDeletedFalse(Integer userId ,Pageable pageable);

  Optional<Notes>findByIdAndIsDeletedTrue(Integer notesId);

  List<Notes>findAllByIsDeletedTrue();

  //recycle bin list
  List<Notes>findByCreatedByAndIsDeletedTrue(Integer userId);
//  Page<Notes>findByCreatedByAndIsDeletedTrue(Integer userId ,Pageable pageable);

}

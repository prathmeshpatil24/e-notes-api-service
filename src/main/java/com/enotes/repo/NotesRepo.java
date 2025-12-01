package com.enotes.repo;

import com.enotes.entity.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesRepo extends JpaRepository<Notes, Integer> {

  Optional<Notes>findByTitle(String notesTitle);

  Page<Notes>findByCreatedBy(Integer userId ,Pageable pageable);
}

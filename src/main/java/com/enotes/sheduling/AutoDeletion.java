package com.enotes.sheduling;

import com.enotes.notes.entity.FileEntity;
import com.enotes.notes.entity.Notes;
import com.enotes.todo.entity.ToDo;
import com.enotes.notes.repo.FileRepo;
import com.enotes.notes.repo.NotesRepo;
import com.enotes.todo.repo.ToDoRepo;
import com.enotes.notes.service.FileServiceImpl;
import com.enotes.notes.service.NotesServiceImpl;
import com.enotes.todo.service.ToDoServiceImpl;
import com.enotes.utils.service.impl.TokenBlockServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AutoDeletion {

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private ToDoRepo toDoRepo;

    @Autowired
    private NotesServiceImpl notesService;

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private ToDoServiceImpl toDoService;

    @Autowired
    private TokenBlockServiceImpl tokenBlockService;

    // testing remaining
    /*
 ┌────────-------------- second  = 0
 │  ┌──────-------------- minute = 0
 │  │   ┌────-------------- hour = 3
 │  │   │   ┌──---- day of month = *
 │  │   │   │   ┌--------- month = *
 │  │   │   │   │   ┌day of week = *
 0  0   3   *   *   *
      */

    //Once per day at low-traffic time
    //Low DB load, Predictable, Safe, Industry standard
    @Scheduled(cron = "0 0 3 * * *")
    public void autoHardDelete(){

        LocalDateTime cutoff = LocalDateTime.now().minusDays(28);

        //1 expired Notes
        List<Notes> expiredNotes = notesRepo.findExpiredFiles(cutoff);

        expiredNotes.forEach(
                n->{
                    notesService.hardDeleteNotesById(n.getId(), n.getCreatedBy());
                }
        );


        //2 expired Files
        List<FileEntity> expiredFiles = fileRepo.findExpiredFiles(cutoff);

        expiredFiles.forEach(
                f->{
                    fileService.hardDeleteFile(f.getFileId(), f.getNotes().getId(), f.getCreatedBy());
                }
        );

        //3 expired toDo
        List<ToDo> expiredToDo = toDoRepo.findExpiredToDo(cutoff);

        expiredToDo.forEach(
                toDo -> {
                    toDoService.hardDeleteTodo(toDo.getId(), toDo.getCreatedBy());
                }
        );

    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUpExpiredToken(){
      tokenBlockService.deleteExpiredToken();
    }

}

/*
✅ Meaning of deleted_at < :cutoff
👉 If deleted_at is older than cutoff

➡️ The item WILL be auto deleted by the scheduler.

👉 If deleted_at is NOT older than cutoff

➡️ The item WILL NOT be auto deleted and will stay in recycle bin.
* Today = Feb 1
Cutoff = today − 28 days = Jan 4

deleted_at_date	  Check	                Auto Delete?
Dec 20	          Dec 20 < Jan 4	      ✔ YES
Jan 1	          Jan 1 < Jan 4	          ✔ YES
Jan 4	          Jan 4 < Jan 4 → false	  ❌ NO
Jan 20	          Jan 20 < Jan 4 → false  ❌ NO
Jan 30	          Jan 30 < Jan 4 → false  ❌ NO
*/
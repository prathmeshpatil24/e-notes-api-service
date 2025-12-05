package com.enotes.service.sheduling;

import com.enotes.entity.FileEntity;
import com.enotes.entity.Notes;
import com.enotes.repo.FileRepo;
import com.enotes.repo.NotesRepo;
import com.enotes.service.impl.FileServiceImpl;
import com.enotes.service.impl.NotesServiceImpl;
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
    private NotesServiceImpl notesService;

    @Autowired
    private FileServiceImpl fileService;

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
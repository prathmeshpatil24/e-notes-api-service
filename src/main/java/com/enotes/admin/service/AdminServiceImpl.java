package com.enotes.admin.service;

import com.enotes.admin.dto.UserData;
import com.enotes.dto.NotesListResponseModel;
import com.enotes.dto.PaginationResponse;
import com.enotes.exceptions.InvalidPaginationParameterException;
import com.enotes.utils.entity.UserEntity;
import com.enotes.utils.repository.UserDetailRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserDetailRepo userDetailRepo;

    @Override
    public PaginationResponse<UserData> listOfUsers(Integer pageNo,
                                                    Integer pageSize,
                                                    String sortBy,
                                                    String sortDir) {

        // Validate pagination params
        if (pageNo < 0) {
            throw new InvalidPaginationParameterException("Page index must not be negative");
        }
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("Page size must be greater than zero");
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidPaginationParameterException("Sort direction must be 'asc' or 'desc'");
        }
        try {
            //created sorting object
            Sort sort = sortDir.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            //created pageable object
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


            Page<UserEntity> page = userDetailRepo.findByRoles_RoleName("ROLE_USER", pageable);

            //validate page number does not exceed total pages
            int totalPages = page.getTotalPages();
            if (totalPages > 0 && pageNo >= totalPages) {
                throw new InvalidPaginationParameterException(
                        "Page number " + pageNo + " exceeds the maximum available pages: " + (totalPages - 1)
                );
            }

            //convert entity page to dto page
            Page<UserData> userData = page
                    .map(user ->
                    {
                        UserData dto = new UserData();
                        dto.setId(user.getId());
                        dto.setFirstName(user.getFirstName());
                        dto.setLastName(user.getLastName());
                        dto.setEmail(user.getEmail());
                        dto.setMobileNo(user.getMobileNo());
                        dto.setIsActive(user.getIsActive());
                        dto.setRoles(user.getRoles());
                        return dto;
                    });
            return new PaginationResponse<>(userData);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

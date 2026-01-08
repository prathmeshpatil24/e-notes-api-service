package com.enotes.admin.service;


import com.enotes.admin.dto.UserData;
import com.enotes.dto.PaginationResponse;

import java.util.List;

public interface AdminService {

    PaginationResponse<UserData> listOfUsers(Integer pageNo,
                                             Integer pageSize,
                                             String sortBy,
                                             String sortDir);
}

package com.enotes.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginationResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;


    //for empty bin custom message
    private List<T> items;
    private int totalItems;
    private String message;

    public PaginationResponse(Page<T> pageData) {
        this.content = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
    }

    // NEW constructor for custom empty response
    public PaginationResponse(List<T> items, int totalItems, int totalPages, String message) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.message = message;
    }
}

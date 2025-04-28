package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Lớp mô hình cho phản hồi phân trang từ API
 */
public class PagedResponse<T> {
    @SerializedName("content")
    private List<T> content;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("last")
    private boolean last;

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }
}
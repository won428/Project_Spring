package com.secondproject.secondproject.dto;

public record PageDto (
        int pageNumber,
        int pageSize,
        int totalPages,
        long totalElements
) {
}

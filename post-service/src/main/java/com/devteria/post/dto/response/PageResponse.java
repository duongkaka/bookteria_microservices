package com.devteria.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {

    int totalPages;
    int pageSize;
    long totalElements;
    int currentPage;

    @Builder.Default
    private List<T> data = Collections.emptyList();

}

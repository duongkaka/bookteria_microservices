package com.devteria.post.controller;

import com.devteria.post.dto.ApiResponse;
import com.devteria.post.dto.request.PostRequest;
import com.devteria.post.dto.response.PageResponse;
import com.devteria.post.dto.response.PostResponse;
import com.devteria.post.service.PostService;
import feign.Body;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
        PostService postService;
    @PostMapping("/create")
    public ApiResponse<PostResponse>createPost(@RequestBody PostRequest request){
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/my-posts")
    public ApiResponse<PageResponse<PostResponse>>getMyPosts(
            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
            @RequestParam(value = "size",required = false,defaultValue = "10") int size
            ){
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.getMyPost(page,size))
                .build();
    }


}
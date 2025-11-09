package com.devteria.profile.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.devteria.profile.dto.ApiResponse;
import com.devteria.profile.dto.request.ProfileCreationRequest;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/")
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {

        return userProfileService.createProfile(request);
    }

    @GetMapping("/internal/")
    List<UserProfileResponse> getProfileUsers(@PathVariable String profileId) {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/internal/{profileId}")
    UserProfileResponse getProfile(@PathVariable String profileId) {
        return userProfileService.getProfile(profileId);
    }

    @GetMapping("/internal/user/{userId}")
    ApiResponse<UserProfileResponse> getProfileByUserId(@PathVariable String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getByUserId(userId))
                .build();
    }
}

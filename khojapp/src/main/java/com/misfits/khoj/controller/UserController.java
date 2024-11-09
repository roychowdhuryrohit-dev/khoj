package com.misfits.khoj.controller;

import com.misfits.khoj.model.user.UserProfile;
import com.misfits.khoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

  @Autowired UserService userService;

  @GetMapping("/user")
  public UserProfile getUserProfileFromAuth(@AuthenticationPrincipal OAuth2User principal) {
    return userService.getUserProfile(principal);
  }
}

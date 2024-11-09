package com.misfits.khoj.service;

import com.misfits.khoj.model.persistence.UserProfileDetails;
import com.misfits.khoj.model.user.UserProfile;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  UserProfile getUserProfile(OAuth2User principal);

  String getUserId(OAuth2User principal);

  UserProfileDetails fetchUserDetails(String userPoolId, String userId);
}

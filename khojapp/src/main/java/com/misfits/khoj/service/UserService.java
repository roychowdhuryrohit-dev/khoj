package com.misfits.khoj.service;

import com.misfits.khoj.exceptions.userexceptions.UserProfileException;
import com.misfits.khoj.model.UserProfile;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  UserProfile getUserProfile(OAuth2User principal) throws UserProfileException;
}

package com.puppynoteserver.user.user.application.port.in;

import com.puppynoteserver.user.user.application.port.in.request.UserProfileUpdateServiceRequest;

public interface UserUpdater {
    void updateProfile(UserProfileUpdateServiceRequest request);
}

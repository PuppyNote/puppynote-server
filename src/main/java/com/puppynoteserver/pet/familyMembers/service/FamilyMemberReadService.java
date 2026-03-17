package com.puppynoteserver.pet.familyMembers.service;

import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import com.puppynoteserver.user.users.entity.User;

import java.util.List;

public interface FamilyMemberReadService {

    List<FamilyMemberResponse> getFamilyMembers(Long petId);

    List<UserSearchResponse> searchUsersByEmail(String email);

    List<User> findFamilyUsers(Long userId);
}

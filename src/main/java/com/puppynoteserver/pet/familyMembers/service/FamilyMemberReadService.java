package com.puppynoteserver.pet.familyMembers.service;

import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;

import java.util.List;

public interface FamilyMemberReadService {

    List<FamilyMemberResponse> getFamilyMembers();

    List<UserSearchResponse> searchUsersByEmail(String email);
}

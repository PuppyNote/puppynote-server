package com.puppynoteserver.pet.familyMembers.service;

import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;

public interface FamilyMemberWriteService {

    void invite(FamilyMemberInviteServiceRequest request);

    void register(FamilyMemberRegisterServiceRequest request);

    void deleteAllByPetId(Long petId);
}

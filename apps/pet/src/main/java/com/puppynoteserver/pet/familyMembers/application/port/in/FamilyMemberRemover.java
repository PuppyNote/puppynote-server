package com.puppynoteserver.pet.familyMembers.application.port.in;

public interface FamilyMemberRemover {

    void deleteFamilyRelation(Long targetUserId, Long petId);

    void deleteAllByPetId(Long petId);
}

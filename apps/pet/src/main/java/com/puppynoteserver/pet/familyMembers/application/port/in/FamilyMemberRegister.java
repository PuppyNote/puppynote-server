package com.puppynoteserver.pet.familyMembers.application.port.in;

import com.puppynoteserver.pet.familyMembers.application.port.in.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.application.port.in.request.FamilyMemberRegisterServiceRequest;
import com.puppynoteserver.pet.pets.domain.entity.Pet;

import java.util.List;

public interface FamilyMemberRegister {

    void invite(FamilyMemberInviteServiceRequest request);

    void register(FamilyMemberRegisterServiceRequest request);

    /** pets 서브도메인이 펫 생성 시 호출 — 현재 유저를 OWNER로 등록한다. */
    void registerOwner(Long userId, Pet pet);

    /** pets 서브도메인이 펫 생성 시 호출 — 기존 가족 구성원들을 FAMILY로 등록한다. */
    void registerFamilyMembers(List<Long> userIds, Pet pet);
}

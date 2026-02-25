package com.puppynoteserver.pet.familyMembers.repository.impl;

import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FamilyMemberRepositoryImpl implements FamilyMemberRepository {

    private final FamilyMemberJpaRepository familyMemberJpaRepository;

    @Override
    public FamilyMember save(FamilyMember familyMember) {
        return familyMemberJpaRepository.save(familyMember);
    }
}

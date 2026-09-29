package com.puppynoteserver.pet.familyMembers.domain.entity;

import com.puppynoteserver.pet.familyMembers.domain.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.domain.entity.enums.RoleType;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * user 서비스가 분리되어 있으므로 User 엔티티를 참조하지 않고 userId(FamilyMemberId 안)만 갖는다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "family_members")
public class FamilyMember {

    @EmbeddedId
    private FamilyMemberId id;

    @MapsId("petId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FamilyMemberStatus status;

    public static FamilyMember of(Long userId, Pet pet, RoleType role, FamilyMemberStatus status) {
        FamilyMember familyMember = new FamilyMember();
        familyMember.id = FamilyMemberId.of(userId, pet.getId());
        familyMember.pet = pet;
        familyMember.role = role;
        familyMember.status = status;
        return familyMember;
    }

    public Long getUserId() {
        return id.getUserId();
    }

    public void updateRole(RoleType role) {
        this.role = role;
    }

    public void updateStatus(FamilyMemberStatus status) {
        this.status = status;
    }
}

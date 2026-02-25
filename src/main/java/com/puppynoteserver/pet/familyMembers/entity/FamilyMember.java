package com.puppynoteserver.pet.familyMembers.entity;

import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "family_members")
public class FamilyMember {

    @EmbeddedId
    private FamilyMemberId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("petId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RoleType role;

    public static FamilyMember of(User user, Pet pet, RoleType role) {
        FamilyMember familyMember = new FamilyMember();
        familyMember.id = FamilyMemberId.of(user.getId(), pet.getId());
        familyMember.user = user;
        familyMember.pet = pet;
        familyMember.role = role;
        return familyMember;
    }

    public void updateRole(RoleType role) {
        this.role = role;
    }
}

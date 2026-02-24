package com.puppynoteserver.pet.familyMembers.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class FamilyMemberId implements Serializable {

    private Long userId;
    private Long petId;

    public static FamilyMemberId of(Long userId, Long petId) {
        FamilyMemberId id = new FamilyMemberId();
        id.userId = userId;
        id.petId = petId;
        return id;
    }
}

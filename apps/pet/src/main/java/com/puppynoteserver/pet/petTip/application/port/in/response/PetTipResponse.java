package com.puppynoteserver.pet.petTip.application.port.in.response;

import com.puppynoteserver.pet.petTip.domain.entity.PetTip;
import lombok.Getter;

@Getter
public class PetTipResponse {

    private final Long id;
    private final String content;

    private PetTipResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public static PetTipResponse of(PetTip petTip) {
        return new PetTipResponse(petTip.getId(), petTip.getContent());
    }
}

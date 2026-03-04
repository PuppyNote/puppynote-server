package com.puppynoteserver.petTip.service.response;

import com.puppynoteserver.petTip.entity.PetTip;
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

package com.puppynoteserver.pet.petItems.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MajorCategory {

    FOOD_NUTRITION("식품/영양", "🍖"),
    HYGIENE("위생/그루밍", "🛁"),
    BATHROOM("배변용품", "🚽"),
    PLAY("장난감/훈련", "🎾"),
    WALK("산책/이동", "🦮"),
    FASHION("의류/패션", "👕"),
    DAILY("생활용품", "🏡"),
    SMART("스마트/헬스케어", "💡"),
    OTHER("기타", "📦");

    private final String description;
    private final String emoji;
}

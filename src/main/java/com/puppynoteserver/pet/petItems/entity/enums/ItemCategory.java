package com.puppynoteserver.pet.petItems.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {

    // 식품/영양
    FOOD("사료", "🍚", MajorCategory.FOOD_NUTRITION),
    SNACK("간식", "🦴", MajorCategory.FOOD_NUTRITION),
    SUPPLEMENT("영양제", "💊", MajorCategory.FOOD_NUTRITION),
    DEWORMING("구충제", "🔬", MajorCategory.FOOD_NUTRITION),
    FLEA_PREVENTION("벼룩/진드기 예방", "🛡️", MajorCategory.FOOD_NUTRITION),

    // 위생/그루밍
    SHAMPOO("샴푸", "🧴", MajorCategory.HYGIENE),
    CONDITIONER("컨디셔너", "💆", MajorCategory.HYGIENE),
    TOOTHBRUSH("칫솔", "🪥", MajorCategory.HYGIENE),
    TOOTHPASTE("치약", "🫧", MajorCategory.HYGIENE),
    DENTAL_CHEW("덴탈껌", "🦷", MajorCategory.HYGIENE),
    COMB("브러쉬/빗", "🪮", MajorCategory.HYGIENE),
    NAIL_CLIPPER("발톱깎이", "✂️", MajorCategory.HYGIENE),
    EAR_CLEANER("귀 세정제", "👂", MajorCategory.HYGIENE),
    EYE_DROPS("안약", "👁️", MajorCategory.HYGIENE),
    GROOMING("그루밍 용품", "✨", MajorCategory.HYGIENE),

    // 배변용품
    DIAPER("기저귀", "🧷", MajorCategory.BATHROOM),
    POOP_PAD("배변패드", "📰", MajorCategory.BATHROOM),
    POOP_BAG("배변봉투", "🛍️", MajorCategory.BATHROOM),

    // 장난감/훈련
    TOY("장난감", "🎾", MajorCategory.PLAY),
    TRAINING_TOOL("훈련도구", "🎯", MajorCategory.PLAY),

    // 산책/이동
    LEASH("목줄/리드줄", "🐕", MajorCategory.WALK),
    HARNESS("하네스", "🎒", MajorCategory.WALK),
    COLLAR("목걸이", "🏷️", MajorCategory.WALK),
    CARRIER("이동장", "🧳", MajorCategory.WALK),
    STROLLER("유모차", "🛒", MajorCategory.WALK),

    // 의류/패션
    CLOTHING("의류", "👕", MajorCategory.FASHION),
    SHOES("신발", "👟", MajorCategory.FASHION),

    // 생활용품
    BED("침대/방석", "🛏️", MajorCategory.DAILY),
    CAGE("케이지/울타리", "🏠", MajorCategory.DAILY),
    BOWL("밥그릇/물그릇", "🥣", MajorCategory.DAILY),
    PERFUME("탈취제/향수", "🌸", MajorCategory.DAILY),

    // 스마트/헬스케어
    CAMERA("반려동물 카메라", "📷", MajorCategory.SMART),
    FEEDER("자동급식기", "🤖", MajorCategory.SMART),
    WATER_DISPENSER("정수기/급수기", "💧", MajorCategory.SMART),
    HEALTH_CHECKER("건강체크 용품", "❤️‍🩹", MajorCategory.SMART),

    // 기타
    OTHER("기타", "📦", MajorCategory.OTHER);

    private final String description;
    private final String emoji;
    private final MajorCategory majorCategory;
}

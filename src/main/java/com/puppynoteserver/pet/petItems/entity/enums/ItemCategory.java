package com.puppynoteserver.pet.petItems.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {

    FOOD("사료"),
    SNACK("간식"),
    SHAMPOO("샴푸"),
    CONDITIONER("컨디셔너"),
    TOOTHBRUSH("칫솔"),
    TOOTHPASTE("치약"),
    DENTAL_CHEW("덴탈껌"),
    COMB("브러쉬/빗"),
    NAIL_CLIPPER("발톱깎이"),
    EAR_CLEANER("귀 세정제"),
    EYE_DROPS("안약"),
    DEWORMING("구충제"),
    FLEA_PREVENTION("벼룩/진드기 예방"),
    SUPPLEMENT("영양제"),
    DIAPER("기저귀"),
    POOP_PAD("배변패드"),
    POOP_BAG("배변봉투"),
    TOY("장난감"),
    LEASH("목줄/리드줄"),
    HARNESS("하네스"),
    COLLAR("목걸이"),
    CLOTHING("의류"),
    SHOES("신발"),
    BED("침대/방석"),
    CAGE("케이지/울타리"),
    BOWL("밥그릇/물그릇"),
    CARRIER("이동장"),
    STROLLER("유모차"),
    PERFUME("탈취제/향수"),
    TRAINING_TOOL("훈련도구"),
    CAMERA("반려동물 카메라"),
    FEEDER("자동급식기"),
    WATER_DISPENSER("정수기/급수기"),
    GROOMING("그루밍 용품"),
    HEALTH_CHECKER("건강체크 용품"),
    OTHER("기타");

    private final String description;
}

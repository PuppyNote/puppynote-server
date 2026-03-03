package com.puppynoteserver.home.service.impl;

import com.puppynoteserver.home.service.HomeReadService;
import com.puppynoteserver.home.service.response.HomeResponse;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petWalkAlarms.service.PetWalkAlarmReadService;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeReadServiceImpl implements HomeReadService {

    private final PetReadService petReadService;
    private final WalkReadService walkReadService;
    private final PetItemReadService petItemReadService;
    private final PetWalkAlarmReadService petWalkAlarmReadService;
    private final S3StorageService s3StorageService;

    @Override
    public HomeResponse getHomeInfo(Long petId) {
        Pet pet = petReadService.findById(petId);
        LocalDate today = LocalDate.now();

        // 펫 기본 정보
        String petProfileImageUrl = s3StorageService.createPresignedUrl(pet.getProfileImage(), BucketKind.PUPPY_PROFILE);
        String petAge = calculateAge(pet.getBirthDate(), today);
        Integer birthdayDday = calculateBirthdayDday(pet.getBirthDate(), today);

        // 산책 정보
        LocalDate sevenDaysAgo = today.minusDays(6);
        long recentWalkCount = walkReadService.countRecentWalks(petId, sevenDaysAgo, today);
        boolean walkedToday = walkReadService.walkedToday(petId);
        Integer daysSinceLastWalk = walkReadService.daysSinceLastWalk(petId);
        long monthlyWalkMinutes = walkReadService.monthlyWalkMinutes(petId);

        // 용품 정보
        long petItemCount = petItemReadService.countItemsByPetId(petId);

        // 오늘 산책 알람
        var todayWalkAlarmTimes = petWalkAlarmReadService.getTodayAlarmTimes(petId);

        return HomeResponse.builder()
                .petName(pet.getName())
                .petProfileImageUrl(petProfileImageUrl)
                .petAge(petAge)
                .birthdayDday(birthdayDday)
                .walkedToday(walkedToday)
                .daysSinceLastWalk(daysSinceLastWalk)
                .monthlyWalkMinutes(monthlyWalkMinutes)
                .recentWalkCount(recentWalkCount)
                .petItemCount(petItemCount)
                .todayWalkAlarmTimes(todayWalkAlarmTimes)
                .build();
    }

    private String calculateAge(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) return null;
        Period period = Period.between(birthDate, today);
        if (period.getYears() >= 1) {
            return period.getYears() + "살";
        }
        return period.getMonths() + "개월";
    }

    private Integer calculateBirthdayDday(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) return null;
        LocalDate nextBirthday = birthDate.withYear(today.getYear());
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return (int) ChronoUnit.DAYS.between(today, nextBirthday);
    }
}

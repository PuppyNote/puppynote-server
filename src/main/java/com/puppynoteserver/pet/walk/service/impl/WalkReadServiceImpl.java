package com.puppynoteserver.pet.walk.service.impl;

import com.puppynoteserver.pet.walk.entity.Walk;
import com.puppynoteserver.pet.walk.repository.WalkRepository;
import com.puppynoteserver.pet.walk.service.WalkReadService;
import com.puppynoteserver.pet.walk.service.response.WalkCalendarResponse;
import com.puppynoteserver.pet.walk.service.response.WalkDetailResponse;
import com.puppynoteserver.pet.walk.service.response.WalkResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkReadServiceImpl implements WalkReadService {

    private final WalkRepository walkRepository;
    private final S3StorageService s3StorageService;

    @Override
    public List<WalkResponse> getWalksByPetId(Long petId, LocalDate date) {
        return walkRepository.findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(
                        petId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay().minusNanos(1)
                ).stream()
                .map(walk -> {
                    String photoUrl = walk.getPhotos().stream()
                            .findFirst()
                            .map(photo -> s3StorageService.createPresignedUrl(photo.getImageKey(), BucketKind.WALK_PHOTO))
                            .orElse(null);
                    return WalkResponse.of(walk, photoUrl);
                })
                .toList();
    }

    @Override
    public List<WalkCalendarResponse> getWalkCalendar(Long petId, YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        Set<LocalDate> walkDates = walkRepository.findByPetIdAndStartTimeBetween(
                        petId,
                        firstDay.atStartOfDay(),
                        lastDay.plusDays(1).atStartOfDay().minusNanos(1)
                ).stream()
                .map(walk -> walk.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        return firstDay.datesUntil(lastDay.plusDays(1))
                .map(date -> WalkCalendarResponse.of(date, walkDates.contains(date)))
                .toList();
    }

    @Override
    public WalkDetailResponse getWalkDetail(Long walkId) {
        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new NotFoundException("산책 기록을 찾을 수 없습니다."));

        List<String> photoUrls = walk.getPhotos().stream()
                .map(photo -> s3StorageService.createPresignedUrl(photo.getImageKey(), BucketKind.WALK_PHOTO))
                .toList();

        return WalkDetailResponse.of(walk, photoUrls);
    }
}

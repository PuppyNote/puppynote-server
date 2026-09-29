package com.puppynoteserver.pet.walk.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.pets.application.port.in.PetFinder;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import com.puppynoteserver.pet.walk.application.port.in.WalkFinder;
import com.puppynoteserver.pet.walk.application.port.in.WalkRegister;
import com.puppynoteserver.pet.walk.application.port.in.WalkRemover;
import com.puppynoteserver.pet.walk.application.port.in.request.WalkCreateServiceRequest;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkCalendarResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkDetailResponse;
import com.puppynoteserver.pet.walk.application.port.in.response.WalkResponse;
import com.puppynoteserver.pet.walk.application.port.out.persistence.WalkRepository;
import com.puppynoteserver.pet.walk.domain.entity.Walk;
import com.puppynoteserver.pet.walk.domain.error.WalkErrorMessage;
import com.puppynoteserver.storage.application.port.out.FileStorage;
import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WalkService implements WalkFinder, WalkRegister, WalkRemover {

    private final WalkRepository walkRepository;
    private final PetFinder petFinder;
    private final FileStorage fileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<WalkResponse> getWalksByPetId(Long petId, LocalDate date) {
        return walkRepository.findByPetIdAndStartTimeBetweenOrderByEndTimeDesc(
                        petId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay().minusNanos(1)
                ).stream()
                .map(walk -> {
                    String photoUrl = walk.getPhotos().stream()
                            .findFirst()
                            .map(photo -> fileStorage.getCloudFrontUrl(photo.getImageKey(), BucketKind.WALK_PHOTO))
                            .orElse(null);
                    return WalkResponse.of(walk, photoUrl);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public long countRecentWalks(Long petId, LocalDate from, LocalDate to) {
        return walkRepository.countByPetIdAndStartTimeBetween(
                petId,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay().minusNanos(1)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean walkedToday(Long petId) {
        LocalDate today = LocalDate.now();
        return walkRepository.countByPetIdAndStartTimeBetween(
                petId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1)
        ) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer daysSinceLastWalk(Long petId) {
        return walkRepository.findTopByPetIdOrderByStartTimeDesc(petId)
                .map(walk -> (int) ChronoUnit.DAYS.between(walk.getStartTime().toLocalDate(), LocalDate.now()))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public long monthlyWalkMinutes(Long petId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        return walkRepository.findByPetIdAndStartTimeBetween(
                        petId,
                        firstDay.atStartOfDay(),
                        today.plusDays(1).atStartOfDay().minusNanos(1)
                ).stream()
                .mapToLong(walk -> Duration.between(walk.getStartTime(), walk.getEndTime()).toMinutes())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Walk findById(Long walkId) {
        return walkRepository.findById(walkId)
                .orElseThrow(() -> new NotFoundException(WalkErrorMessage.WALK_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public WalkDetailResponse getWalkDetail(Long walkId) {
        Walk walk = findById(walkId);

        List<String> photoUrls = walk.getPhotos().stream()
                .map(photo -> fileStorage.getCloudFrontUrl(photo.getImageKey(), BucketKind.WALK_PHOTO))
                .toList();

        return WalkDetailResponse.of(walk, photoUrls);
    }

    @Override
    public WalkResponse create(WalkCreateServiceRequest request) {
        Pet pet = petFinder.findById(request.getPetId());

        Walk walk = request.toEntity(pet);

        List<String> photoKeys = request.getPhotoKeys();
        if (photoKeys != null) {
            photoKeys.forEach(walk::addPhoto);
        }

        Walk savedWalk = walkRepository.save(walk);

        String photoUrl = (photoKeys == null || photoKeys.isEmpty()) ? null :
                fileStorage.getCloudFrontUrl(photoKeys.get(0), BucketKind.WALK_PHOTO);

        return WalkResponse.of(savedWalk, photoUrl);
    }

    @Override
    public void delete(Long walkId) {
        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new IllegalArgumentException(WalkErrorMessage.WALK_NOT_FOUND.getMessage()));

        List<String> photoKeys = walk.getPhotos().stream()
                .map(photo -> photo.getImageKey())
                .toList();
        fileStorage.deleteObjects(photoKeys, BucketKind.WALK_PHOTO);

        walkRepository.deleteById(walkId);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        List<String> photoKeys = walkRepository.findAllByPetId(petId).stream()
                .flatMap(walk -> walk.getPhotos().stream())
                .map(photo -> photo.getImageKey())
                .toList();
        fileStorage.deleteObjects(photoKeys, BucketKind.WALK_PHOTO);

        walkRepository.deleteAllByPetId(petId);
    }
}

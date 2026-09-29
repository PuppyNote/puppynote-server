package com.puppynoteserver.pet.petItems.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseFinder;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseRemover;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemFinder;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemRegister;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemRemover;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemUpdater;
import com.puppynoteserver.pet.petItems.application.port.in.request.PetItemCreateServiceRequest;
import com.puppynoteserver.pet.petItems.application.port.in.request.PetItemUpdateServiceRequest;
import com.puppynoteserver.pet.petItems.application.port.in.response.PetItemResponse;
import com.puppynoteserver.pet.petItems.application.port.out.persistence.PetItemRepository;
import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.domain.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.domain.error.PetItemErrorMessage;
import com.puppynoteserver.pet.pets.application.port.in.PetFinder;
import com.puppynoteserver.pet.pets.domain.entity.Pet;
import com.puppynoteserver.storage.application.port.out.FileStorage;
import com.puppynoteserver.storage.enums.BucketKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PetItemService implements PetItemFinder, PetItemRegister, PetItemUpdater, PetItemRemover {

    private final PetItemRepository petItemRepository;
    private final PetItemPurchaseFinder petItemPurchaseFinder;
    private final PetItemPurchaseRemover petItemPurchaseRemover;
    private final PetFinder petFinder;
    private final FileStorage fileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<PetItemResponse> getItemsByPetId(Long petId, ItemCategory category) {
        List<PetItem> items = (category != null)
                ? petItemRepository.findByPetIdAndCategory(petId, category)
                : petItemRepository.findByPetId(petId);

        if (items.isEmpty()) {
            return List.of();
        }

        // 최근 구매일 배치 조회 (N+1 방지)
        List<Long> petItemIds = items.stream().map(PetItem::getId).toList();
        Map<Long, LocalDate> latestPurchaseDateMap = petItemPurchaseFinder.findLatestPurchaseDatesByPetItemIds(petItemIds);

        LocalDate today = LocalDate.now();

        return items.stream()
                .map(item -> {
                    LocalDate lastPurchasedAt = latestPurchaseDateMap.get(item.getId());
                    String imageUrl = fileStorage.getCloudFrontUrl(item.getImageKey(), BucketKind.PET_ITEM_PHOTO);
                    return PetItemResponse.of(item, imageUrl, lastPurchasedAt);
                })
                // 구매주기가 다가오는 순 정렬: null(미구매)이 가장 앞, 이후 nextPurchaseAt - today ASC
                .sorted(Comparator.comparing(
                        response -> response.getNextPurchaseAt() != null
                                ? response.getNextPurchaseAt().toEpochDay() - today.toEpochDay()
                                : Long.MIN_VALUE
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countItemsByPetId(Long petId) {
        return petItemRepository.countByPetId(petId);
    }

    @Override
    @Transactional(readOnly = true)
    public PetItem findById(Long petItemId) {
        return petItemRepository.findById(petItemId)
                .orElseThrow(() -> new NotFoundException(PetItemErrorMessage.PET_ITEM_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public PetItemResponse getItemDetail(Long petItemId) {
        PetItem petItem = findById(petItemId);

        LocalDate lastPurchasedAt = petItemPurchaseFinder.findLatestPurchaseDateByPetItemId(petItemId)
                .orElse(null);

        String imageUrl = fileStorage.getCloudFrontUrl(petItem.getImageKey(), BucketKind.PET_ITEM_PHOTO);

        return PetItemResponse.of(petItem, imageUrl, lastPurchasedAt);
    }

    @Override
    public PetItemResponse create(PetItemCreateServiceRequest request) {
        Pet pet = petFinder.findById(request.getPetId());
        PetItem petItem = request.toEntity(pet);
        PetItem savedItem = petItemRepository.save(petItem);

        return PetItemResponse.of(savedItem, null, null);
    }

    @Override
    public PetItemResponse update(Long petItemId, PetItemUpdateServiceRequest request) {
        PetItem petItem = findById(petItemId);
        String oldImageKey = petItem.getImageKey();

        petItem.update(request.getName(), request.getCategory(),
                request.getPurchaseCycleDays(), request.getPurchaseUrl(), request.getImageKey());

        // 이미지가 변경된 경우 기존 이미지 S3에서 삭제
        if (oldImageKey != null && !Objects.equals(oldImageKey, request.getImageKey())) {
            fileStorage.deleteObject(oldImageKey, BucketKind.PET_ITEM_PHOTO);
        }

        return PetItemResponse.of(petItem, null, null);
    }

    @Override
    public void delete(Long petItemId) {
        PetItem petItem = findById(petItemId);
        petItemPurchaseRemover.deleteAllByPetItemId(petItemId);
        petItemRepository.deleteById(petItemId);

        fileStorage.deleteObject(petItem.getImageKey(), BucketKind.PET_ITEM_PHOTO);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        List<String> imageKeys = petItemRepository.findByPetId(petId).stream()
                .map(PetItem::getImageKey)
                .filter(Objects::nonNull)
                .toList();

        petItemPurchaseRemover.deleteAllByPetId(petId);
        petItemRepository.deleteAllByPetId(petId);

        fileStorage.deleteObjects(imageKeys, BucketKind.PET_ITEM_PHOTO);
    }
}

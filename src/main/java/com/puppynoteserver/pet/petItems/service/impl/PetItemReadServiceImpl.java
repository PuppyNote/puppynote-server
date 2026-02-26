package com.puppynoteserver.pet.petItems.service.impl;

import com.puppynoteserver.pet.petItems.entity.PetItem;
import com.puppynoteserver.pet.petItems.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.pet.petItems.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItems.repository.PetItemRepository;
import com.puppynoteserver.pet.petItems.service.PetItemReadService;
import com.puppynoteserver.pet.petItems.service.response.PetItemResponse;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetItemReadServiceImpl implements PetItemReadService {

    private final PetItemRepository petItemRepository;
    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final S3StorageService s3StorageService;

    @Override
    public List<PetItemResponse> getItemsByPetId(Long petId, ItemCategory category) {
        List<PetItem> items = (category != null)
                ? petItemRepository.findByPetIdAndCategory(petId, category)
                : petItemRepository.findByPetId(petId);

        if (items.isEmpty()) {
            return List.of();
        }

        // 최근 구매일 배치 조회 (N+1 방지)
        List<Long> petItemIds = items.stream().map(PetItem::getId).toList();
        Map<Long, LocalDate> latestPurchaseDateMap = petItemPurchaseRepository.findLatestByPetItemIds(petItemIds)
                .stream()
                .collect(Collectors.toMap(
                        purchase -> purchase.getPetItem().getId(),
                        PetItemPurchase::getPurchasedAt
                ));

        LocalDate today = LocalDate.now();

        return items.stream()
                .map(item -> {
                    LocalDate lastPurchasedAt = latestPurchaseDateMap.get(item.getId());
                    String imageUrl = s3StorageService.createPresignedUrl(item.getImageKey(), BucketKind.PET_ITEM_PHOTO);
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
    public PetItemResponse getItemDetail(Long petItemId) {
        PetItem petItem = petItemRepository.findById(petItemId)
                .orElseThrow(() -> new NotFoundException("용품 정보를 찾을 수 없습니다."));

        LocalDate lastPurchasedAt = petItemPurchaseRepository.findLatestByPetItemId(petItemId)
                .map(PetItemPurchase::getPurchasedAt)
                .orElse(null);

        String imageUrl = s3StorageService.createPresignedUrl(petItem.getImageKey(), BucketKind.PET_ITEM_PHOTO);

        return PetItemResponse.of(petItem, imageUrl, lastPurchasedAt);
    }
}

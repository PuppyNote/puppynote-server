package com.puppynoteserver.pet.petItemPurchase.service.impl;

import com.puppynoteserver.pet.petItemPurchase.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.repository.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.service.PetItemPurchaseReadService;
import com.puppynoteserver.pet.petItemPurchase.service.response.PetItemPurchaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetItemPurchaseReadServiceImpl implements PetItemPurchaseReadService {

    private final PetItemPurchaseRepository petItemPurchaseRepository;

    @Override
    public List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId) {
        return petItemPurchaseRepository.findAllByPetItemId(petItemId).stream()
                .map(PetItemPurchaseResponse::of)
                .toList();
    }

    @Override
    public Map<Long, LocalDate> findLatestPurchaseDatesByPetItemIds(List<Long> petItemIds) {
        return petItemPurchaseRepository.findLatestByPetItemIds(petItemIds).stream()
                .collect(Collectors.toMap(
                        purchase -> purchase.getPetItem().getId(),
                        PetItemPurchase::getPurchasedAt
                ));
    }

    @Override
    public Optional<LocalDate> findLatestPurchaseDateByPetItemId(Long petItemId) {
        return petItemPurchaseRepository.findLatestByPetItemId(petItemId)
                .map(PetItemPurchase::getPurchasedAt);
    }
}

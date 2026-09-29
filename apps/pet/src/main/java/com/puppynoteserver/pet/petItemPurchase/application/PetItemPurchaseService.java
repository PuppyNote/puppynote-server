package com.puppynoteserver.pet.petItemPurchase.application;

import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseFinder;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseRegister;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.PetItemPurchaseRemover;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.request.PetItemPurchaseCreateServiceRequest;
import com.puppynoteserver.pet.petItemPurchase.application.port.in.response.PetItemPurchaseResponse;
import com.puppynoteserver.pet.petItemPurchase.application.port.out.persistence.PetItemPurchaseRepository;
import com.puppynoteserver.pet.petItemPurchase.domain.entity.PetItemPurchase;
import com.puppynoteserver.pet.petItemPurchase.domain.error.PetItemPurchaseErrorMessage;
import com.puppynoteserver.pet.petItems.domain.entity.PetItem;
import com.puppynoteserver.pet.petItems.application.port.in.PetItemFinder;
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
@Transactional
public class PetItemPurchaseService implements PetItemPurchaseFinder, PetItemPurchaseRegister, PetItemPurchaseRemover {

    private final PetItemPurchaseRepository petItemPurchaseRepository;
    private final PetItemFinder petItemFinder;

    @Override
    @Transactional(readOnly = true)
    public List<PetItemPurchaseResponse> getPurchaseHistory(Long petItemId) {
        return petItemPurchaseRepository.findAllByPetItemId(petItemId).stream()
                .map(PetItemPurchaseResponse::of)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, LocalDate> findLatestPurchaseDatesByPetItemIds(List<Long> petItemIds) {
        return petItemPurchaseRepository.findLatestByPetItemIds(petItemIds).stream()
                .collect(Collectors.toMap(
                        purchase -> purchase.getPetItem().getId(),
                        PetItemPurchase::getPurchasedAt
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalDate> findLatestPurchaseDateByPetItemId(Long petItemId) {
        return petItemPurchaseRepository.findLatestByPetItemId(petItemId)
                .map(PetItemPurchase::getPurchasedAt);
    }

    @Override
    public PetItemPurchaseResponse recordPurchase(PetItemPurchaseCreateServiceRequest request) {
        PetItem petItem = petItemFinder.findById(request.getPetItemId());
        PetItemPurchase saved = petItemPurchaseRepository.save(request.toEntity(petItem));
        return PetItemPurchaseResponse.of(saved);
    }

    @Override
    public void deletePurchase(Long purchaseId) {
        petItemPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new NotFoundException(PetItemPurchaseErrorMessage.PURCHASE_NOT_FOUND.getMessage()));
        petItemPurchaseRepository.deleteById(purchaseId);
    }

    @Override
    public void deleteAllByPetItemId(Long petItemId) {
        petItemPurchaseRepository.deleteAllByPetItemId(petItemId);
    }

    @Override
    public void deleteAllByPetId(Long petId) {
        petItemPurchaseRepository.deleteAllByPetId(petId);
    }
}

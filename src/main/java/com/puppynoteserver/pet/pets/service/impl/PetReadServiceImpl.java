package com.puppynoteserver.pet.pets.service.impl;

import com.puppynoteserver.global.security.SecurityService;
import com.puppynoteserver.pet.pets.repository.PetRepository;
import com.puppynoteserver.pet.pets.service.PetReadService;
import com.puppynoteserver.pet.pets.service.response.PetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetReadServiceImpl implements PetReadService {

    private final PetRepository petRepository;
    private final SecurityService securityService;

    @Override
    public List<PetResponse> getMyPets() {
        Long userId = securityService.getCurrentLoginUserInfo().getUserId();
        return petRepository.findByUserId(userId).stream()
                .map(PetResponse::from)
                .toList();
    }
}

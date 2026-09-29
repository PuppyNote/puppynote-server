package com.puppynoteserver.pet.petWalkAlarms.application.port.in;

public interface PetWalkAlarmRemover {

    void deleteAllByPetId(Long petId);
}

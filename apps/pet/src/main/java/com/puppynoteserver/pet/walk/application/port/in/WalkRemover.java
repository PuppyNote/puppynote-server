package com.puppynoteserver.pet.walk.application.port.in;

public interface WalkRemover {

    void delete(Long walkId);

    void deleteAllByPetId(Long petId);
}

package com.puppynoteserver.pet.familyMembers.application.port.out;

public record UserProfile(Long userId, String email, String nickName, String profileUrl) {
}

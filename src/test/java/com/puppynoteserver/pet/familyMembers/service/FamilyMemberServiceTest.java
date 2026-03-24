package com.puppynoteserver.pet.familyMembers.service;

import com.puppynoteserver.IntegrationTestSupport;
import com.puppynoteserver.global.exception.NotFoundException;
import com.puppynoteserver.global.exception.PuppyNoteException;
import com.puppynoteserver.pet.familyMembers.entity.FamilyMember;
import com.puppynoteserver.pet.familyMembers.entity.enums.FamilyMemberStatus;
import com.puppynoteserver.pet.familyMembers.entity.enums.RoleType;
import com.puppynoteserver.pet.familyMembers.repository.FamilyMemberJpaRepository;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberInviteServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.request.FamilyMemberRegisterServiceRequest;
import com.puppynoteserver.pet.familyMembers.service.response.FamilyMemberResponse;
import com.puppynoteserver.pet.familyMembers.service.response.UserSearchResponse;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.pet.pets.repository.PetJpaRepository;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class FamilyMemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private FamilyMemberWriteService familyMemberWriteService;

    @Autowired
    private FamilyMemberReadService familyMemberReadService;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private PetJpaRepository petJpaRepository;

    @AfterEach
    @Override
    public void tearDown() {
        familyMemberJpaRepository.deleteAllInBatch();
        petJpaRepository.deleteAllInBatch();
        super.tearDown();
    }

    // ==================== FamilyMemberWriteService - invite ====================

    @DisplayName("OWNER가 다른 유저를 가족으로 초대하면 PENDING 상태로 저장된다.")
    @Test
    void invite_success() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User invitee = userRepository.save(createUser("invitee@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        FamilyMemberInviteServiceRequest request = FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(invitee.getId())
                .petId(pet.getId())
                .build();

        // when
        familyMemberWriteService.invite(request);

        // then
        Optional<FamilyMember> pendingRecord = familyMemberJpaRepository.findByIdUserIdAndIdPetId(invitee.getId(), pet.getId());
        assertThat(pendingRecord).isPresent();
        assertThat(pendingRecord.get().getRole()).isEqualTo(RoleType.FAMILY);
        assertThat(pendingRecord.get().getStatus()).isEqualTo(FamilyMemberStatus.PENDING);
    }

    @DisplayName("OWNER가 아닌 유저가 초대를 시도하면 PuppyNoteException이 발생한다.")
    @Test
    void invite_nonOwnerCannotInvite() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family = userRepository.save(createUser("family@test.com", "password", SnsType.NORMAL));
        User invitee = userRepository.save(createUser("invitee@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(family.getId()));

        FamilyMemberInviteServiceRequest request = FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(invitee.getId())
                .petId(pet.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.invite(request))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("펫의 OWNER만 가족을 초대할 수 있습니다.");
    }

    @DisplayName("이미 가족이거나 초대 대기 중인 유저를 초대하면 PuppyNoteException이 발생한다.")
    @Test
    void invite_alreadyInvited() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User alreadyInvited = userRepository.save(createUser("already@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(alreadyInvited, pet, RoleType.FAMILY, FamilyMemberStatus.PENDING));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        FamilyMemberInviteServiceRequest request = FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(alreadyInvited.getId())
                .petId(pet.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.invite(request))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("이미 가족으로 등록되어 있거나 초대 대기 중인 유저입니다.");
    }

    @DisplayName("초대자가 해당 펫의 멤버가 아닌 경우 NotFoundException이 발생한다.")
    @Test
    void invite_inviterNotFoundInPet() {
        // given
        User stranger = userRepository.save(createUser("stranger@test.com", "password", SnsType.NORMAL));
        User invitee = userRepository.save(createUser("invitee@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(stranger.getId()));

        FamilyMemberInviteServiceRequest request = FamilyMemberInviteServiceRequest.builder()
                .inviteeUserId(invitee.getId())
                .petId(pet.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.invite(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("해당 펫을 찾을 수 없습니다.");
    }

    // ==================== FamilyMemberWriteService - register ====================

    @DisplayName("PENDING 상태의 초대를 수락하면 DONE 상태로 변경된다.")
    @Test
    void register_success() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User invitee = userRepository.save(createUser("invitee@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(invitee, pet, RoleType.FAMILY, FamilyMemberStatus.PENDING));

        FamilyMemberRegisterServiceRequest request = FamilyMemberRegisterServiceRequest.builder()
                .userId(invitee.getId())
                .petId(pet.getId())
                .build();

        // when
        familyMemberWriteService.register(request);

        // then
        FamilyMember updated = familyMemberJpaRepository.findByIdUserIdAndIdPetId(invitee.getId(), pet.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(FamilyMemberStatus.DONE);
    }

    @DisplayName("초대 내역이 없는 경우 register 시 NotFoundException이 발생한다.")
    @Test
    void register_noPendingRecord() {
        // given
        User user = userRepository.save(createUser("user@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));

        FamilyMemberRegisterServiceRequest request = FamilyMemberRegisterServiceRequest.builder()
                .userId(user.getId())
                .petId(pet.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.register(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("초대받은 내역이 없습니다.");
    }

    @DisplayName("이미 DONE 상태인 레코드를 register하면 PuppyNoteException이 발생한다.")
    @Test
    void register_alreadyDone() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User member = userRepository.save(createUser("member@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(member, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        FamilyMemberRegisterServiceRequest request = FamilyMemberRegisterServiceRequest.builder()
                .userId(member.getId())
                .petId(pet.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.register(request))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("이미 가족으로 등록되어 있습니다.");
    }

    // ==================== FamilyMemberWriteService - deleteFamilyRelation ====================

    @DisplayName("OWNER가 FAMILY 멤버를 삭제하면 해당 가족 관계가 제거된다.")
    @Test
    void deleteFamilyRelation_ownerDeletesFamily() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family = userRepository.save(createUser("family@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        // when
        familyMemberWriteService.deleteFamilyRelation(family.getId(), pet.getId());

        // then
        assertThat(familyMemberJpaRepository.findByIdUserIdAndIdPetId(family.getId(), pet.getId())).isEmpty();
        assertThat(familyMemberJpaRepository.findByIdUserIdAndIdPetId(owner.getId(), pet.getId())).isPresent();
    }

    @DisplayName("FAMILY 멤버가 OWNER와의 관계를 스스로 탈퇴할 수 있다.")
    @Test
    void deleteFamilyRelation_familyLeavesPet() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family = userRepository.save(createUser("family@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(family.getId()));

        // when
        familyMemberWriteService.deleteFamilyRelation(owner.getId(), pet.getId());

        // then
        assertThat(familyMemberJpaRepository.findByIdUserIdAndIdPetId(family.getId(), pet.getId())).isEmpty();
        assertThat(familyMemberJpaRepository.findByIdUserIdAndIdPetId(owner.getId(), pet.getId())).isPresent();
    }

    @DisplayName("OWNER가 OWNER를 삭제 시도하면 PuppyNoteException이 발생한다.")
    @Test
    void deleteFamilyRelation_ownerCannotDeleteOwner() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User anotherOwner = userRepository.save(createUser("anotherowner@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(anotherOwner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.deleteFamilyRelation(anotherOwner.getId(), pet.getId()))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("FAMILY 멤버만 삭제할 수 있습니다.");
    }

    @DisplayName("FAMILY가 FAMILY를 삭제 시도하면 PuppyNoteException이 발생한다.")
    @Test
    void deleteFamilyRelation_familyCannotDeleteFamily() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family1 = userRepository.save(createUser("family1@test.com", "password", SnsType.NORMAL));
        User family2 = userRepository.save(createUser("family2@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family1, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family2, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(family1.getId()));

        // when & then
        assertThatThrownBy(() -> familyMemberWriteService.deleteFamilyRelation(family2.getId(), pet.getId()))
                .isInstanceOf(PuppyNoteException.class)
                .hasMessageContaining("FAMILY는 FAMILY를 삭제할 수 없습니다.");
    }

    // ==================== FamilyMemberReadService - getFamilyMembers ====================

    @DisplayName("펫의 DONE 상태 가족 목록 조회 시 현재 로그인 유저는 제외된다.")
    @Test
    void getFamilyMembers_excludesCurrentUser() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User family1 = userRepository.save(createUser("family1@test.com", "password", SnsType.NORMAL));
        User family2 = userRepository.save(createUser("family2@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family1, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(family2, pet, RoleType.FAMILY, FamilyMemberStatus.DONE));

        // owner로 로그인한 경우 owner 본인은 결과에서 제외됨
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        // when
        List<FamilyMemberResponse> result = familyMemberReadService.getFamilyMembers(pet.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("userId")
                .doesNotContain(owner.getId())
                .containsExactlyInAnyOrder(family1.getId(), family2.getId());
    }

    @DisplayName("PENDING 상태의 멤버는 가족 목록 조회에 포함되지 않는다.")
    @Test
    void getFamilyMembers_pendingExcluded() {
        // given
        User owner = userRepository.save(createUser("owner@test.com", "password", SnsType.NORMAL));
        User pendingUser = userRepository.save(createUser("pending@test.com", "password", SnsType.NORMAL));
        Pet pet = petJpaRepository.save(Pet.of("초코", LocalDate.of(2020, 1, 1), null, null));
        familyMemberJpaRepository.save(FamilyMember.of(owner, pet, RoleType.OWNER, FamilyMemberStatus.DONE));
        familyMemberJpaRepository.save(FamilyMember.of(pendingUser, pet, RoleType.FAMILY, FamilyMemberStatus.PENDING));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(owner.getId()));

        // when
        List<FamilyMemberResponse> result = familyMemberReadService.getFamilyMembers(pet.getId());

        // then
        assertThat(result).isEmpty();
    }

    // ==================== FamilyMemberReadService - searchUsersByEmail ====================

    @DisplayName("이메일로 유저 검색 시 현재 로그인 유저 본인은 제외된다.")
    @Test
    void searchUsersByEmail_excludesCurrentUser() {
        // given
        User currentUser = userRepository.save(createUser("search@test.com", "password", SnsType.NORMAL));
        User otherUser1 = userRepository.save(createUser("search2@test.com", "password", SnsType.NORMAL));
        User otherUser2 = userRepository.save(createUser("other@test.com", "password", SnsType.NORMAL));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(currentUser.getId()));

        // when
        List<UserSearchResponse> result = familyMemberReadService.searchUsersByEmail("search");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(otherUser1.getId());
        assertThat(result).extracting("userId").doesNotContain(currentUser.getId());
    }

    @DisplayName("일치하는 이메일이 없으면 빈 목록을 반환한다.")
    @Test
    void searchUsersByEmail_noMatch() {
        // given
        User user = userRepository.save(createUser("user@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        List<UserSearchResponse> result = familyMemberReadService.searchUsersByEmail("nonexistent");

        // then
        assertThat(result).isEmpty();
    }
}

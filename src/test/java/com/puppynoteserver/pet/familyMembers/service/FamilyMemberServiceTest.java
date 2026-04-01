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
    void OWNER가_다른_유저를_가족으로_초대하면_PENDING_상태로_저장된다() {
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
        assertThat(pendingRecord.get())
                .extracting("role", "status")
                .containsExactly(RoleType.FAMILY, FamilyMemberStatus.PENDING);
    }

    @DisplayName("OWNER가 아닌 유저가 초대를 시도하면 PuppyNoteException이 발생한다.")
    @Test
    void OWNER가_아닌_유저가_초대를_시도하면_예외가_발생한다() {
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
    void 이미_가족이거나_초대_대기_중인_유저를_초대하면_예외가_발생한다() {
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
    void 초대자가_해당_펫의_멤버가_아닌_경우_예외가_발생한다() {
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
    void PENDING_상태의_초대를_수락하면_DONE_상태로_변경된다() {
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
    void 초대_내역이_없는_경우_register_시_예외가_발생한다() {
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
    void 이미_DONE_상태인_레코드를_register하면_예외가_발생한다() {
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
    void OWNER가_FAMILY_멤버를_삭제하면_해당_가족_관계가_제거된다() {
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
    void FAMILY_멤버가_OWNER와의_관계를_스스로_탈퇴할_수_있다() {
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
    void OWNER가_OWNER를_삭제_시도하면_예외가_발생한다() {
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
    void FAMILY가_FAMILY를_삭제_시도하면_예외가_발생한다() {
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
    void 펫의_DONE_상태_가족_목록_조회_시_현재_로그인_유저는_제외된다() {
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
    void PENDING_상태의_멤버는_가족_목록_조회에_포함되지_않는다() {
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
    void 이메일로_유저_검색_시_현재_로그인_유저_본인은_제외된다() {
        // given
        User currentUser = userRepository.save(createUser("search@test.com", "password", SnsType.NORMAL));
        User otherUser1 = userRepository.save(createUser("search2@test.com", "password", SnsType.NORMAL));
        User otherUser2 = userRepository.save(createUser("other@test.com", "password", SnsType.NORMAL));

        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(currentUser.getId()));

        // when
        List<UserSearchResponse> result = familyMemberReadService.searchUsersByEmail("search");

        // then
        assertThat(result).hasSize(1);
        assertThat(result)
                .extracting("userId")
                .containsExactly(otherUser1.getId())
                .doesNotContain(currentUser.getId());
    }

    @DisplayName("일치하는 이메일이 없으면 빈 목록을 반환한다.")
    @Test
    void 일치하는_이메일이_없으면_빈_목록을_반환한다() {
        // given
        User user = userRepository.save(createUser("user@test.com", "password", SnsType.NORMAL));
        given(securityService.getCurrentLoginUserInfo()).willReturn(createLoginUserInfo(user.getId()));

        // when
        List<UserSearchResponse> result = familyMemberReadService.searchUsersByEmail("nonexistent");

        // then
        assertThat(result).isEmpty();
    }
}

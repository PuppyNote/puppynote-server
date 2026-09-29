package com.puppynoteserver.pet.familyMembers.application.port.out;

/**
 * [APPLICATION · OUT PORT] notification 서비스에 알림 발송을 요청한다.
 *
 * <p>
 * 포트로 뽑아 두는 이유는 제공자가 계약이 아니기 때문이다 — notification이 REST가 아니라 이벤트로
 * 바뀌어도 {@code FamilyMemberService}는 변경되지 않는다.
 */
public interface NotificationSender {

    void sendFamilyInvite(Long inviteeUserId, String petName);
}

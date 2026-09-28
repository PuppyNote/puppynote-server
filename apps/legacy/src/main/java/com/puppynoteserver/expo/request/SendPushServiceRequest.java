package com.puppynoteserver.expo.request;

import com.puppynoteserver.user.push.entity.Push;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendPushServiceRequest {

    private Push push;
    private String sound;
    private String body;
    private SendPushDataDto sendPushDataDto;

    @Builder
    private SendPushServiceRequest(Push push, String sound, String body, SendPushDataDto sendPushDataDto) {
        this.push = push;
        this.sound = sound;
        this.body = body;
        this.sendPushDataDto = sendPushDataDto;
    }
}

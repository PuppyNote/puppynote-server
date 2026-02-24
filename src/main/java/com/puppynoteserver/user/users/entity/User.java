package com.puppynoteserver.user.users.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.jwt.dto.JwtToken;
import com.puppynoteserver.user.push.entity.Push;
import com.puppynoteserver.user.refreshToken.entity.RefreshToken;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.Sex;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickName;

    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private SnsType snsType;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String useYn;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Push> pushes = new ArrayList<>();

    @Builder
    public User(String email, Long id, String nickName, String password, String profileUrl, List<Push> pushes, List<RefreshToken> refreshTokens, Role role, SnsType snsType, String useYn) {
        this.email = email;
        this.id = id;
        this.nickName = nickName;
        this.password = password;
        this.profileUrl = profileUrl;
        this.pushes = pushes;
        this.refreshTokens = refreshTokens;
        this.role = role;
        this.snsType = snsType;
        this.useYn = useYn;
    }

    public void withdraw() {
        this.useYn = "N";
    }

    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void checkSnsType(SnsType snsType) {
        if (!this.snsType.equals(snsType)) {
            this.snsType.checkSnsType();
        }
    }

    public void checkRefreshToken(JwtToken jwtToken, String deviceId) {
        refreshTokens.stream()
                .filter(refreshToken -> deviceId.equals(refreshToken.getDeviceId()))
                .findFirst()
                .ifPresentOrElse(
                        existToken -> existToken.updateRefreshToken(jwtToken.getRefreshToken()),
                        () -> {
                            RefreshToken refreshToken = RefreshToken.of(this, jwtToken.getRefreshToken(), deviceId);
                            refreshToken.setUser(this);
                        }
                );
    }

    public void checkPushKey(String pushToken, String deviceId) {
        pushes.stream()
                .filter(push -> deviceId.equals(push.getDeviceId()))
                .findFirst()
                .ifPresentOrElse(
                        existPush -> existPush.updatePushToken(pushToken),
                        () -> {
                            Push push = Push.of(deviceId, this, pushToken);
                            push.updateUser(this);
                        }
                );
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateProfile(String nickName, String introduction, Sex sex, String birthDay, String profileUrl) {
        updateNickName(nickName);
        updateProfileUrl(profileUrl);
    }

}

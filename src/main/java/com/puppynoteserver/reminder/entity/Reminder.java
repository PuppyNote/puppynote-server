package com.puppynoteserver.reminder.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.pets.entity.Pet;
import com.puppynoteserver.reminder.entity.enums.ActivityType;
import com.puppynoteserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reminders")
public class Reminder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    private Long relatedActivityId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ActivityType activityType;

    @Column(nullable = false)
    private LocalDateTime notifyAt;

    private Boolean isEnabled;

    public static Reminder of(User user, Pet pet, ActivityType activityType, LocalDateTime notifyAt, Long relatedActivityId) {
        Reminder reminder = new Reminder();
        reminder.user = user;
        reminder.pet = pet;
        reminder.activityType = activityType;
        reminder.notifyAt = notifyAt;
        reminder.relatedActivityId = relatedActivityId;
        reminder.isEnabled = true;
        return reminder;
    }

    public void toggleEnabled() {
        this.isEnabled = !this.isEnabled;
    }

    public void updateNotifyAt(LocalDateTime notifyAt) {
        this.notifyAt = notifyAt;
    }
}

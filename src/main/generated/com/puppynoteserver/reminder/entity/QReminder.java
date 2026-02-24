package com.puppynoteserver.reminder.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReminder is a Querydsl query type for Reminder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReminder extends EntityPathBase<Reminder> {

    private static final long serialVersionUID = 42576339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReminder reminder = new QReminder("reminder");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    public final EnumPath<com.puppynoteserver.reminder.entity.enums.ActivityType> activityType = createEnum("activityType", com.puppynoteserver.reminder.entity.enums.ActivityType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isEnabled = createBoolean("isEnabled");

    public final DateTimePath<java.time.LocalDateTime> notifyAt = createDateTime("notifyAt", java.time.LocalDateTime.class);

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    public final NumberPath<Long> relatedActivityId = createNumber("relatedActivityId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final com.puppynoteserver.user.users.entity.QUser user;

    public QReminder(String variable) {
        this(Reminder.class, forVariable(variable), INITS);
    }

    public QReminder(Path<? extends Reminder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReminder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReminder(PathMetadata metadata, PathInits inits) {
        this(Reminder.class, metadata, inits);
    }

    public QReminder(Class<? extends Reminder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
        this.user = inits.isInitialized("user") ? new com.puppynoteserver.user.users.entity.QUser(forProperty("user")) : null;
    }

}


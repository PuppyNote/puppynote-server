package com.puppynoteserver.feeding.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFeeding is a Querydsl query type for Feeding
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeeding extends EntityPathBase<Feeding> {

    private static final long serialVersionUID = 412303765L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeeding feeding = new QFeeding("feeding");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    public final StringPath amount = createString("amount");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> fedAt = createDateTime("fedAt", java.time.LocalDateTime.class);

    public final StringPath foodType = createString("foodType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final com.puppynoteserver.user.users.entity.QUser user;

    public QFeeding(String variable) {
        this(Feeding.class, forVariable(variable), INITS);
    }

    public QFeeding(Path<? extends Feeding> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFeeding(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFeeding(PathMetadata metadata, PathInits inits) {
        this(Feeding.class, metadata, inits);
    }

    public QFeeding(Class<? extends Feeding> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
        this.user = inits.isInitialized("user") ? new com.puppynoteserver.user.users.entity.QUser(forProperty("user")) : null;
    }

}


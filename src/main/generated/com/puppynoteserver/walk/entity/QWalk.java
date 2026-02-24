package com.puppynoteserver.walk.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalk is a Querydsl query type for Walk
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalk extends EntityPathBase<Walk> {

    private static final long serialVersionUID = -983518335L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalk walk = new QWalk("walk");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public final EnumPath<com.puppynoteserver.walk.entity.enums.WalkStatus> status = createEnum("status", com.puppynoteserver.walk.entity.enums.WalkStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QWalk(String variable) {
        this(Walk.class, forVariable(variable), INITS);
    }

    public QWalk(Path<? extends Walk> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalk(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalk(PathMetadata metadata, PathInits inits) {
        this(Walk.class, metadata, inits);
    }

    public QWalk(Class<? extends Walk> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
    }

}


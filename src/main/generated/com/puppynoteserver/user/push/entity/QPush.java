package com.puppynoteserver.user.push.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPush is a Querydsl query type for Push
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPush extends EntityPathBase<Push> {

    private static final long serialVersionUID = 1327022466L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPush push = new QPush("push");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath deviceId = createString("deviceId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath pushToken = createString("pushToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final com.puppynoteserver.user.users.entity.QUser user;

    public QPush(String variable) {
        this(Push.class, forVariable(variable), INITS);
    }

    public QPush(Path<? extends Push> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPush(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPush(PathMetadata metadata, PathInits inits) {
        this(Push.class, metadata, inits);
    }

    public QPush(Class<? extends Push> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.puppynoteserver.user.users.entity.QUser(forProperty("user")) : null;
    }

}


package com.puppynoteserver.user.users.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1460461151L;

    public static final QUser user = new QUser("user");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final StringPath profileUrl = createString("profileUrl");

    public final ListPath<com.puppynoteserver.user.push.entity.Push, com.puppynoteserver.user.push.entity.QPush> pushes = this.<com.puppynoteserver.user.push.entity.Push, com.puppynoteserver.user.push.entity.QPush>createList("pushes", com.puppynoteserver.user.push.entity.Push.class, com.puppynoteserver.user.push.entity.QPush.class, PathInits.DIRECT2);

    public final ListPath<com.puppynoteserver.user.refreshToken.entity.RefreshToken, com.puppynoteserver.user.refreshToken.entity.QRefreshToken> refreshTokens = this.<com.puppynoteserver.user.refreshToken.entity.RefreshToken, com.puppynoteserver.user.refreshToken.entity.QRefreshToken>createList("refreshTokens", com.puppynoteserver.user.refreshToken.entity.RefreshToken.class, com.puppynoteserver.user.refreshToken.entity.QRefreshToken.class, PathInits.DIRECT2);

    public final EnumPath<com.puppynoteserver.user.users.entity.enums.Role> role = createEnum("role", com.puppynoteserver.user.users.entity.enums.Role.class);

    public final EnumPath<com.puppynoteserver.user.users.entity.enums.SnsType> snsType = createEnum("snsType", com.puppynoteserver.user.users.entity.enums.SnsType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath useYn = createString("useYn");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}


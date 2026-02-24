package com.puppynoteserver.pet.familyMembers.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFamilyMember is a Querydsl query type for FamilyMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFamilyMember extends EntityPathBase<FamilyMember> {

    private static final long serialVersionUID = -1840826321L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFamilyMember familyMember = new QFamilyMember("familyMember");

    public final QFamilyMemberId id;

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    public final StringPath role = createString("role");

    public final com.puppynoteserver.user.users.entity.QUser user;

    public QFamilyMember(String variable) {
        this(FamilyMember.class, forVariable(variable), INITS);
    }

    public QFamilyMember(Path<? extends FamilyMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFamilyMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFamilyMember(PathMetadata metadata, PathInits inits) {
        this(FamilyMember.class, metadata, inits);
    }

    public QFamilyMember(Class<? extends FamilyMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QFamilyMemberId(forProperty("id")) : null;
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
        this.user = inits.isInitialized("user") ? new com.puppynoteserver.user.users.entity.QUser(forProperty("user")) : null;
    }

}


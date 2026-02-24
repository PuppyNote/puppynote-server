package com.puppynoteserver.pet.familyMembers.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFamilyMemberId is a Querydsl query type for FamilyMemberId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QFamilyMemberId extends BeanPath<FamilyMemberId> {

    private static final long serialVersionUID = 492433834L;

    public static final QFamilyMemberId familyMemberId = new QFamilyMemberId("familyMemberId");

    public final NumberPath<Long> petId = createNumber("petId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QFamilyMemberId(String variable) {
        super(FamilyMemberId.class, forVariable(variable));
    }

    public QFamilyMemberId(Path<? extends FamilyMemberId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFamilyMemberId(PathMetadata metadata) {
        super(FamilyMemberId.class, metadata);
    }

}


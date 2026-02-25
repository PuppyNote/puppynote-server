package com.puppynoteserver.pet.pets.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPet is a Querydsl query type for Pet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPet extends EntityPathBase<Pet> {

    private static final long serialVersionUID = 1127120973L;

    public static final QPet pet = new QPet("pet");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath profileImage = createString("profileImage");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QPet(String variable) {
        super(Pet.class, forVariable(variable));
    }

    public QPet(Path<? extends Pet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPet(PathMetadata metadata) {
        super(Pet.class, metadata);
    }

}


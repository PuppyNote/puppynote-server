package com.puppynoteserver.supply.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSupply is a Querydsl query type for Supply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSupply extends EntityPathBase<Supply> {

    private static final long serialVersionUID = -916685619L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSupply supply = new QSupply("supply");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    public final EnumPath<com.puppynoteserver.supply.entity.enums.SupplyCategory> category = createEnum("category", com.puppynoteserver.supply.entity.enums.SupplyCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastPurchasedDate = createDate("lastPurchasedDate", java.time.LocalDate.class);

    public final StringPath name = createString("name");

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    public final NumberPath<Integer> purchaseCycleDays = createNumber("purchaseCycleDays", Integer.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> reorderPoint = createNumber("reorderPoint", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QSupply(String variable) {
        this(Supply.class, forVariable(variable), INITS);
    }

    public QSupply(Path<? extends Supply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSupply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSupply(PathMetadata metadata, PathInits inits) {
        this(Supply.class, metadata, inits);
    }

    public QSupply(Class<? extends Supply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
    }

}


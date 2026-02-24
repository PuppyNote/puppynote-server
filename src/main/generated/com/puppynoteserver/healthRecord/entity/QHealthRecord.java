package com.puppynoteserver.healthRecord.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHealthRecord is a Querydsl query type for HealthRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthRecord extends EntityPathBase<HealthRecord> {

    private static final long serialVersionUID = -1700968567L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthRecord healthRecord = new QHealthRecord("healthRecord");

    public final com.puppynoteserver.global.QBaseTimeEntity _super = new com.puppynoteserver.global.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.puppynoteserver.pet.pets.entity.QPet pet;

    public final DatePath<java.time.LocalDate> recordDate = createDate("recordDate", java.time.LocalDate.class);

    public final EnumPath<com.puppynoteserver.healthRecord.entity.enums.RecordType> recordType = createEnum("recordType", com.puppynoteserver.healthRecord.entity.enums.RecordType.class);

    public final StringPath unit = createString("unit");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath value = createString("value");

    public QHealthRecord(String variable) {
        this(HealthRecord.class, forVariable(variable), INITS);
    }

    public QHealthRecord(Path<? extends HealthRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHealthRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHealthRecord(PathMetadata metadata, PathInits inits) {
        this(HealthRecord.class, metadata, inits);
    }

    public QHealthRecord(Class<? extends HealthRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pet = inits.isInitialized("pet") ? new com.puppynoteserver.pet.pets.entity.QPet(forProperty("pet")) : null;
    }

}


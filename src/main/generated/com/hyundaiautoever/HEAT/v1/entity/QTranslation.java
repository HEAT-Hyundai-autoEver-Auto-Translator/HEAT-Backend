package com.hyundaiautoever.HEAT.v1.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTranslation is a Querydsl query type for Translation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTranslation extends EntityPathBase<Translation> {

    private static final long serialVersionUID = -1784955769L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTranslation translation = new QTranslation("translation");

    public final DateTimePath<java.sql.Timestamp> createDatetime = createDateTime("createDatetime", java.sql.Timestamp.class);

    public final QLanguage requestLanguage;

    public final StringPath requestText = createString("requestText");

    public final QLanguage resultLanguage;

    public final StringPath resultText = createString("resultText");

    public final NumberPath<Long> translationNo = createNumber("translationNo", Long.class);

    public final QUser user;

    public QTranslation(String variable) {
        this(Translation.class, forVariable(variable), INITS);
    }

    public QTranslation(Path<? extends Translation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTranslation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTranslation(PathMetadata metadata, PathInits inits) {
        this(Translation.class, metadata, inits);
    }

    public QTranslation(Class<? extends Translation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.requestLanguage = inits.isInitialized("requestLanguage") ? new QLanguage(forProperty("requestLanguage")) : null;
        this.resultLanguage = inits.isInitialized("resultLanguage") ? new QLanguage(forProperty("resultLanguage")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}


package com.hyundaiautoever.HEAT.v1.entity;

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

    private static final long serialVersionUID = -1356142923L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QLanguage language;

    public final DatePath<java.time.LocalDate> lastAccessDate = createDate("lastAccessDate", java.time.LocalDate.class);

    public final StringPath passwordHash = createString("passwordHash");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath refreshToken = createString("refreshToken");

    public final DatePath<java.time.LocalDate> signupDate = createDate("signupDate", java.time.LocalDate.class);

    public final ListPath<Translation, QTranslation> translationList = this.<Translation, QTranslation>createList("translationList", Translation.class, QTranslation.class, PathInits.DIRECT2);

    public final NumberPath<Long> userAccountNo = createNumber("userAccountNo", Long.class);

    public final StringPath userEmail = createString("userEmail");

    public final StringPath userName = createString("userName");

    public final EnumPath<com.hyundaiautoever.HEAT.v1.util.UserRole> userRole = createEnum("userRole", com.hyundaiautoever.HEAT.v1.util.UserRole.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QLanguage(forProperty("language")) : null;
    }

}


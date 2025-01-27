
CREATE TABLE member_address
(
    member_address_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    street                  varchar(255) NULL,
    city                    varchar(255) NOT NULL,
    province_state          varchar(255) NULL,
    region                  varchar(255) NULL,
    country                 varchar(255) NOT NULL,
    created_at              timestamp NOT NULL,
    updated_at              timestamp NOT NULL,
    CONSTRAINT PK_2 PRIMARY KEY ( member_address_id )
);

CREATE TABLE "member"
(
    member_id   uuid DEFAULT uuid_generate_v4() NOT NULL,
    first_name  varchar(255) NOT NULL,
    last_name   varchar(255) NOT NULL,
    email       varchar(255) NOT NULL,
    password    varchar(255) NOT NULL,
    phone_number varchar(255) NOT NULL,
    member_address_id uuid UNIQUE,  -- Foreign key to the a
    created_at  timestamp NOT NULL,
    updated_at  timestamp NOT NULL,
    CONSTRAINT PK_1 PRIMARY KEY ( member_id ),
    CONSTRAINT FK_1 FOREIGN KEY ( member_address_id ) REFERENCES member_address ( member_address_id )
);

CREATE INDEX FK_1 ON "member"
    (
     member_address_id
        );

CREATE TABLE membership_type
(
    membership_type_id uuid NOT NULL,
    organization_id    uuid NOT NULL,
    name               varchar(255) NOT NULL,
    description        varchar(255) NOT NULL,
    created_at         timestamp NOT NULL,
    updated_at         timestamp NOT NULL,
    CONSTRAINT PK_13 PRIMARY KEY ( membership_type_id )
);


CREATE TABLE membership
(
    membership_id      uuid NOT NULL,
    organization_id    uuid NOT NULL,
    member_id          uuid NOT NULL,
    membership_type_id uuid NOT NULL,
    status             varchar(50) NOT NULL,
    start_date         timestamp NOT NULL,
    end_date           timestamp NOT NULL,
    created_at         timestamp NOT NULL,
    updated_at         timestamp NOT NULL,
    CONSTRAINT PK_12 PRIMARY KEY ( membership_id ),
    CONSTRAINT FK_3 FOREIGN KEY ( member_id ) REFERENCES "member" ( member_id ),
    CONSTRAINT FK_13 FOREIGN KEY ( membership_type_id ) REFERENCES membership_type ( membership_type_id )
);

CREATE INDEX FK_2 ON membership
    (
     member_id
        );

CREATE INDEX FK_3 ON membership
    (
     membership_type_id
        );



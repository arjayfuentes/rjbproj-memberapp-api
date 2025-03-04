
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






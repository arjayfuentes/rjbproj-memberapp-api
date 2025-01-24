CREATE TABLE if not exists organization_address
(
    organization_address_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    street                  varchar(255) NULL,
    city                    varchar(255) NOT NULL,
    province_state          varchar(255) NULL,
    region                  varchar(255) NULL,
    country                 varchar(255) NOT NULL,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_8 PRIMARY KEY ( organization_address_id )
    );


CREATE TABLE if not exists organization
(
    organization_id         uuid DEFAULT uuid_generate_v4() NOT NULL,
    name                    varchar(255) NOT NULL,
    description             varchar(255) NOT NULL,
    organization_address_id uuid NOT NULL,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT PK_3 PRIMARY KEY ( organization_id ),
    CONSTRAINT FK_9 FOREIGN KEY ( organization_address_id ) REFERENCES organization_address ( organization_address_id )
    );

CREATE INDEX FK_1 ON organization
    (
     organization_address_id
        );
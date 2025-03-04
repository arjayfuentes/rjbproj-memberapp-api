CREATE TABLE member_address
(
    member_address_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    street            varchar(255) NULL,
    city              varchar(255) NOT NULL,
    province_state    varchar(255) NULL,
    region            varchar(255) NULL,
    country           varchar(255) NOT NULL,
    created_at        timestamp NOT NULL,
    updated_at        timestamp NOT NULL,
    CONSTRAINT PK_member_address PRIMARY KEY (member_address_id)
);

CREATE TABLE "member"
(
    member_id          uuid DEFAULT uuid_generate_v4() NOT NULL,
    first_name         varchar(255) NOT NULL,
    last_name          varchar(255) NOT NULL,
    email              varchar(255) NOT NULL,
    password           varchar(255) NOT NULL,
    phone_number       varchar(255) NOT NULL,
    member_address_id  uuid UNIQUE,
    created_at         timestamp NOT NULL,
    updated_at         timestamp NOT NULL,
    CONSTRAINT PK_member PRIMARY KEY (member_id),
    CONSTRAINT FK_member_address FOREIGN KEY (member_address_id) REFERENCES member_address (member_address_id)
);

CREATE INDEX idx_member_address_id ON "member" (member_address_id);

CREATE TABLE membership_type
(
    membership_type_id uuid DEFAULT uuid_generate_v4() NOT NULL,
    organization_id    uuid NOT NULL,
    name               varchar(255) NOT NULL,
    description        varchar(255) NOT NULL,
    created_at         timestamp NOT NULL,
    updated_at         timestamp NOT NULL,
    CONSTRAINT PK_membership_type PRIMARY KEY (membership_type_id)
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
    CONSTRAINT PK_membership PRIMARY KEY (membership_id),
    CONSTRAINT FK_membership_member FOREIGN KEY (member_id) REFERENCES "member" (member_id),
    CONSTRAINT FK_membership_type FOREIGN KEY (membership_type_id) REFERENCES membership_type (membership_type_id)
);

CREATE INDEX idx_membership_member_id ON membership (member_id);
CREATE INDEX idx_membership_type_id ON membership (membership_type_id);

CREATE TABLE role
(
    role_id    uuid NOT NULL,
    name       varchar(50) NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    CONSTRAINT PK_role PRIMARY KEY (role_id)
);

CREATE TABLE "permission"
(
    permission_id uuid NOT NULL,
    name          varchar(255) NOT NULL,
    version       varchar(255) NOT NULL,
    created_at    timestamp NOT NULL,
    updated_at    timestamp NOT NULL,
    CONSTRAINT PK_permission PRIMARY KEY (permission_id)
);

CREATE TABLE role_permission
(
    permission_id uuid NOT NULL,
    role_id       uuid NOT NULL,
    CONSTRAINT PK_role_permission PRIMARY KEY (permission_id, role_id),
    CONSTRAINT FK_role_permission FOREIGN KEY (permission_id) REFERENCES "permission" (permission_id),
    CONSTRAINT FK_permission_role FOREIGN KEY (role_id) REFERENCES role (role_id)
);

CREATE INDEX idx_role_permission_id ON role_permission (permission_id);
CREATE INDEX idx_permission_role_id ON role_permission (role_id);

CREATE TABLE member_role
(
    member_id       uuid NOT NULL,
    role_id         uuid NOT NULL,
    organization_id uuid NOT NULL,
    CONSTRAINT PK_member_role PRIMARY KEY (member_id, role_id, organization_id),
    CONSTRAINT FK_member_role_member FOREIGN KEY (member_id) REFERENCES "member" (member_id),
    CONSTRAINT FK_member_role FOREIGN KEY (role_id) REFERENCES role (role_id)
);

CREATE INDEX idx_member_role_member_id ON member_role (member_id);
CREATE INDEX idx_member_role_role_id ON member_role (role_id);
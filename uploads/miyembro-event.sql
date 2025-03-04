CREATE TABLE event
(
    event_id         UUID NOT NULL,
    organization_id  UUID,
    name             VARCHAR(255),
    description      VARCHAR(255),
    start_event_date TIMESTAMP WITHOUT TIME ZONE,
    end_event_date   TIMESTAMP WITHOUT TIME ZONE,
    event_address_id UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_event PRIMARY KEY (event_id)
);

CREATE TABLE event_address
(
    event_address_id UUID NOT NULL,
    street           VARCHAR(255),
    city             VARCHAR(255),
    province_state   VARCHAR(255),
    region           VARCHAR(255),
    country          VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_eventaddress PRIMARY KEY (event_address_id)
);

CREATE TABLE event_confirmation
(
    event_confirmation_id UUID         NOT NULL,
    event_id              UUID         NOT NULL,
    member_id             UUID,
    confirmation_status   VARCHAR(255) NOT NULL,
    confirmation_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_eventconfirmation PRIMARY KEY (event_confirmation_id)
);

ALTER TABLE event
    ADD CONSTRAINT uc_event_event_address UNIQUE (event_address_id);

ALTER TABLE event_confirmation
    ADD CONSTRAINT FK_EVENTCONFIRMATION_ON_EVENT FOREIGN KEY (event_id) REFERENCES event (event_id);

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_EVENT_ADDRESS FOREIGN KEY (event_address_id) REFERENCES event_address (event_address_id);
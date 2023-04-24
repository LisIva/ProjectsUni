

CREATE TABLE client (
    client_id          INTEGER NOT NULL,
    first_name         VARCHAR2(20 CHAR) NOT NULL,
    last_name          VARCHAR2(30 CHAR) NOT NULL,
    loyal_card_number  VARCHAR2(20 CHAR)
);

ALTER TABLE client ADD CONSTRAINT cet_pk PRIMARY KEY ( client_id );

CREATE TABLE menu (
    dish_id   INTEGER NOT NULL,
    name      VARCHAR2(20 CHAR) NOT NULL,
    price     INTEGER NOT NULL,
    discount  NUMBER
);

ALTER TABLE menu ADD CONSTRAINT mnu_pk PRIMARY KEY ( dish_id );

ALTER TABLE menu ADD CONSTRAINT mnu_name_un UNIQUE ( name );

CREATE TABLE "ORDER" (
    odr_order_id   INTEGER NOT NULL,
    cet_client_id  INTEGER NOT NULL,
    odr_total_sum  NUMBER NOT NULL,
    std_table_id   INTEGER
);

ALTER TABLE "ORDER" ADD CONSTRAINT odr_pk PRIMARY KEY ( odr_order_id,
                                                        cet_client_id );

CREATE TABLE order_details (
    quantity          INTEGER NOT NULL,
    odr_odr_order_id  INTEGER NOT NULL,
    odr_client_id     INTEGER NOT NULL,
    mnu_dish_id       INTEGER NOT NULL
);

ALTER TABLE order_details
    ADD CONSTRAINT ods_pk PRIMARY KEY ( odr_odr_order_id,
                                        odr_client_id,
                                        mnu_dish_id );

CREATE TABLE "TABLE" (
    table_id       INTEGER NOT NULL,
    wtr_waiter_id  INTEGER NOT NULL
);

ALTER TABLE "TABLE" ADD CONSTRAINT tae_pk PRIMARY KEY ( table_id );

CREATE TABLE waiter (
    waiter_id     INTEGER NOT NULL,
    first_name    VARCHAR2(30 CHAR) NOT NULL,
    last_name     VARCHAR2(30 CHAR) NOT NULL,
    phone_number  VARCHAR2(20 CHAR) NOT NULL
);

ALTER TABLE waiter ADD CONSTRAINT wtr_pk PRIMARY KEY ( waiter_id );

ALTER TABLE "ORDER"
    ADD CONSTRAINT odr_cet_fk FOREIGN KEY ( cet_client_id )
        REFERENCES client ( client_id );

ALTER TABLE order_details
    ADD CONSTRAINT ods_mnu_fk FOREIGN KEY ( mnu_dish_id )
        REFERENCES menu ( dish_id );

ALTER TABLE order_details
    ADD CONSTRAINT ods_odr_fk FOREIGN KEY ( odr_odr_order_id,
                                            odr_client_id )
        REFERENCES "ORDER" ( odr_order_id,
                             cet_client_id );

ALTER TABLE "TABLE"
    ADD CONSTRAINT tae_wtr_fk FOREIGN KEY ( wtr_waiter_id )
        REFERENCES waiter ( waiter_id );

ALTER TABLE "ORDER"
    ADD CONSTRAINT odr_tae_fk FOREIGN KEY ( std_table_id )
        REFERENCES "TABLE" ( table_id );

ALTER TABLE "CLIENT" ADD CONSTRAINT
      cet_chk1 CHECK(loyal_card_number LIKE '____-____-____-____');

ALTER TABLE "CLIENT" ADD CONSTRAINT cet_loyal_card_number_un UNIQUE ( loyal_card_number );
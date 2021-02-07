-- Users schema

-- !Ups

CREATE TABLE User (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    isMod boolean NOT NULL,
    PRIMARY KEY (id)
);

-- !Downs

DROP TABLE User;
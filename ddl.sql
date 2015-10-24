CREATE database user DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use user
CREATE TABLE user
(
    id BIGINT PRIMARY KEY NOT NULL,
    username VARCHAR(45),
    password VARCHAR(255),
    valid TINYINT
);
CREATE UNIQUE INDEX username_UNIQUE ON user (username);

BEGIN;
INSERT INTO user VALUES ('1', 'test', 'guanhong', '1');
COMMIT;
-- Mini-program / app client users (MySQL 8+; H2 tests use MODE=MySQL)
-- CREATE DATABASE IF NOT EXISTS integration_client DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS client_user (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    openid       VARCHAR(64)  NOT NULL,
    unionid      VARCHAR(64)  NULL,
    nickname     VARCHAR(128) NULL,
    avatar_url   VARCHAR(512) NULL,
    enabled      TINYINT(1)   NOT NULL DEFAULT 1,
    last_login_at DATETIME    NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_client_user_openid UNIQUE (openid)
);

CREATE INDEX IF NOT EXISTS idx_client_user_unionid ON client_user (unionid);

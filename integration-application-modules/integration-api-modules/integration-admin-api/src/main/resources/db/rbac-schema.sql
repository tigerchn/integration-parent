-- RBAC tables for integration-admin-api (MySQL 8+ compatible; also used in H2 tests with MODE=MySQL)
-- Create database (run once in MySQL client): CREATE DATABASE IF NOT EXISTS integration_admin DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS admin_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(64)  NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    enabled         TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admin_user_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS admin_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admin_role_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS admin_permission (
    id          BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(128)  NOT NULL,
    name        VARCHAR(128)  NOT NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admin_permission_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS admin_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS admin_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

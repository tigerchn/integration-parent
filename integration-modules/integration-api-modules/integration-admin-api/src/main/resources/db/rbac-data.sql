-- Seed RBAC data. Default login: admin / admin123
-- password_hash is BCrypt for "admin123"
-- Deletes allow re-running this script (e.g. multiple Spring test contexts).

DELETE FROM admin_role_permission;
DELETE FROM admin_user_role;
DELETE FROM admin_permission;
DELETE FROM admin_role;
DELETE FROM admin_user;

INSERT INTO admin_user (id, username, password_hash, enabled)
VALUES (1, 'admin', '$2a$10$nVyiKo3TNs2FkYimpdBj4usIbrVRG3.7oe74j/bwrXUpQsTXG7IPG', 1);

INSERT INTO admin_role (id, code, name)
VALUES (1, 'SUPER_ADMIN', 'Super administrator');

INSERT INTO admin_permission (id, code, name)
VALUES (1, 'admin:system:ping', 'Call admin ping API');

INSERT INTO admin_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO admin_role_permission (role_id, permission_id) VALUES (1, 1);

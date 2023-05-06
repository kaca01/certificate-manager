insert into restapi.user (email, name, surname, phone, country, enabled) values
    ('sara@gmail.com', 'Sara', 'Saric', '+381123123', 'Serbia', true);
insert into restapi.user (email, name, surname, phone, country, enabled) values
    ('nena@gmail.com', 'Nevenka', 'Nešić', '+381124343', 'Serbia', true);
insert into restapi.user (email, name, surname, phone, country, enabled) values
    ('miki@gmail.com', 'Miki', 'Mirić', '+381444123', 'Serbia', true);
insert into restapi.user (email, name, surname, phone, country, enabled) values
    ('pedja@gmail.com', 'Peđa', 'Simonović', '+38111123', 'Serbia', true);

insert into restapi.password (password, last_password_reset_date, user_id) values
    ('$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', '2023-01-01T12:33:24.893', 1);
insert into restapi.password (password, last_password_reset_date, user_id) values
    ('$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', '2023-01-01T12:33:24.893', 2);
insert into restapi.password (password, last_password_reset_date, user_id) values
    ('$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', '2023-01-01T12:33:24.893', 3);
insert into restapi.password (password, last_password_reset_date, user_id) values
    ('$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', '2023-01-01T12:33:24.893', 4);

insert into restapi.role (name) values ('ROLE_USER');
insert into restapi.role (name) values ('ROLE_ADMIN');

insert into restapi.user_role (user_id, role_id) VALUES (1, 1);
insert into restapi.user_role (user_id, role_id) VALUES (2, 1);
insert into restapi.user_role (user_id, role_id) VALUES (3, 1);
insert into restapi.user_role (user_id, role_id) VALUES (4, 2);

insert into restapi.user_activation (user_id, date, life) values (1, '2022-01-01T12:33:24.893', 180);
insert into restapi.user_activation (user_id, date, life) values (2, '2022-01-01T11:42:24.893', 180);
insert into restapi.user_activation (user_id, date, life) values (3, '2022-12-11T21:57:24.893', 180);
insert into restapi.user_activation (user_id, date, life) values (4, '2022-02-18T14:18:24.893', 180);

insert into restapi.reset_password (user_id, expired_date, code) values (1, '2022-01-01T12:33:24.893', 12345);
insert into restapi.reset_password (user_id, expired_date, code) values (2, '2022-01-01T11:42:24.893', 12345);
insert into restapi.reset_password (user_id, expired_date, code) values (3, '2022-12-11T21:57:24.893', 12345);
insert into restapi.reset_password (user_id, expired_date, code) values (4, '2022-02-18T14:18:24.893', 12345);

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (1, null, 0, 4, '');
insert into restapi.certificate (type, valid_from, valid_to, subject_id, is_withdrawn, withdrawn_reason, serial_number, issuer_serial_number) values (0, '2023-05-01T11:00:00.000', '2024-05-01T00:00:00.000', 4, false, '', '4604902006381533243', null);

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (1, 1, 1, 2, '');
insert into restapi.certificate (type, valid_from, valid_to, subject_id, is_withdrawn, withdrawn_reason, serial_number, issuer_serial_number) values (1, '2023-05-01T00:00:00.000', '2024-05-01T00:00:00.000', 2, false, '', '5440992161912455484', '4604902006381533243');

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (1, 1, 1, 1, '');
insert into restapi.certificate (type, valid_from, valid_to, subject_id, is_withdrawn, withdrawn_reason, serial_number, issuer_serial_number) values (1, '2023-05-01T00:00:00.000', '2024-05-01T00:00:00.000', 1, false, '', '-3360052214249816916', '4604902006381533243');

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (1, 3, 1, 3, '');
insert into restapi.certificate (type, valid_from, valid_to, subject_id, is_withdrawn, withdrawn_reason, serial_number, issuer_serial_number) values (1, '2023-05-01T00:00:00.000', '2024-05-01T00:00:00.000', 3, false, '', '-1347043084476417129', '-3360052214249816916');

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (1, 3, 2, 4, '');
insert into restapi.certificate (type, valid_from, valid_to, subject_id, is_withdrawn, withdrawn_reason, serial_number, issuer_serial_number) values (2, '2023-05-01T00:00:00.000', '2024-05-01T00:00:00.000', 4, false, '', '-3750336939555358699', '-3360052214249816916');

insert into restapi.certificate_request (status, issuer_id, type, subject_id, refusal_reason) values (0, 3, 2, 4, '');

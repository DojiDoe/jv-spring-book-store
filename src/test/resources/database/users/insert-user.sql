INSERT INTO users(id, email, password, first_name, last_name, shipping_address)
VALUES (2, 'dojidoe@gmail.com', '$2a$10$Ul8.kr1zQbH8deHIXuGBzO1tlQYairNeXZ0MB0Mg7FYcOS.dcxU62',
        'Doji', 'Doe', 'Stepana Bandery St, 1');
INSERT INTO users_roles(user_id, role_id)
VALUES (2, 3);

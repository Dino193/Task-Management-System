DROP DATABASE IF EXISTS TaskManagementSystem;

CREATE DATABASE TaskManagementSystem;

USE TaskManagementSystem;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role), 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

select * from user_roles;
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    description TEXT,
    status ENUM('TODO', 'IN_PROGRESS', 'DONE'),
    priority ENUM('LOW', 'MEDIUM', 'HIGH'),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE task_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);
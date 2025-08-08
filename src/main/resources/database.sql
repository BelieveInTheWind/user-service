-- Create database
CREATE DATABASE userservice_db;
\c userservice_db;

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS permission_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT NOT NULL DEFAULT nextval('users_user_id_seq'::regclass),
    user_email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

-- Create role table
CREATE TABLE IF NOT EXISTS role (
    role_id BIGINT NOT NULL DEFAULT nextval('role_role_id_seq'::regclass),
    role_name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    CONSTRAINT role_pkey PRIMARY KEY (role_id)
);

-- Create permission table
CREATE TABLE IF NOT EXISTS permission (
    permission_id BIGINT NOT NULL DEFAULT nextval('permission_permission_id_seq'::regclass),
    permission_type VARCHAR(255) NOT NULL,
    role_id BIGINT,
    CONSTRAINT permission_pkey PRIMARY KEY (permission_id),
    CONSTRAINT fk_permission_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
);

-- Create user_role table
CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT NOT NULL DEFAULT nextval('user_role_id_seq'::regclass),
    user_id BIGINT,
    role_id BIGINT,
    CONSTRAINT user_role_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE,
    CONSTRAINT unique_user_role UNIQUE (user_id, role_id)
);

-- Create profile table
CREATE TABLE IF NOT EXISTS profile (
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255),
    full_name VARCHAR(255),
    user_name VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    pic_url VARCHAR(500),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    postcode VARCHAR(20),
    city VARCHAR(100),
    CONSTRAINT profile_pkey PRIMARY KEY (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(user_email);
CREATE INDEX IF NOT EXISTS idx_role_name ON role(role_name);
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role(role_id);
CREATE INDEX IF NOT EXISTS idx_permission_role_id ON permission(role_id);
CREATE INDEX IF NOT EXISTS idx_permission_type ON permission(permission_type);
CREATE INDEX IF NOT EXISTS idx_profile_user_email ON profile(user_email);
CREATE INDEX IF NOT EXISTS idx_profile_user_name ON profile(user_name);

-- Insert default roles
INSERT INTO role (role_name, description) VALUES 
    ('ADMIN', 'Admin'),
    ('USER', 'Regular user')
ON CONFLICT (role_name) DO NOTHING;

-- Insert default permissions for admin 
INSERT INTO permission (permission_type, role_id) 
SELECT 
    unnest(ARRAY['CREATE_USER', 'UPDATE_USER', 'DELETE_USER', 'VIEW_ALL_USERS', 'MANAGE_ROLES']),
    role_id 
FROM role WHERE role_name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insert default permissions for user 
INSERT INTO permission (permission_type, role_id) 
SELECT 
    unnest(ARRAY['VIEW_OWN_PROFILE', 'UPDATE_OWN_PROFILE']),
    role_id 
FROM role WHERE role_name = 'USER'
ON CONFLICT DO NOTHING;

-- Create default admin user (password: admin123)
INSERT INTO users (user_email, password) VALUES 
    ('admin@admin.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM3lTlTUiFfvgs3DOmwe')
ON CONFLICT (user_email) DO NOTHING;
INSERT INTO user_role (user_id, role_id) 
SELECT u.user_id, r.role_id 
FROM users u, role r 
WHERE u.user_email = 'admin@admin.com' AND r.role_name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create users
-- password: password123 
INSERT INTO users (user_email, password) VALUES 
('user1@gmail.com', '$2a$12$4Muun5tCBwuNjCu9fxlL.uIdTLPkievlBaqOIYxq0hGDim3r3.VFy')
ON CONFLICT (user_email) DO NOTHING;
INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE u.user_email = 'user1@gmail.com' AND r.role_name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO users (user_email, password) VALUES 
('user2@gmail.com', '$2a$12$4Muun5tCBwuNjCu9fxlL.uIdTLPkievlBaqOIYxq0hGDim3r3.VFy')
ON CONFLICT (user_email) DO NOTHING;
INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE u.user_email = 'user2@gmail.com' AND r.role_name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();



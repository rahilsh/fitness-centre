-- Application Database Schema for H2 Database
-- Auto-created on application startup

-- App User table
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    last_login TIMESTAMP
);

-- User Roles mapping table (many-to-many between app_user and UserRole enum)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Refresh Token table
CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Fitness Centre table
CREATE TABLE IF NOT EXISTS fitness_centre (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Slot table
CREATE TABLE IF NOT EXISTS slot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fitness_centre_id BIGINT NOT NULL,
    slot_date DATE NOT NULL,
    activity VARCHAR(50) NOT NULL,
    start_time INT NOT NULL,
    end_time INT NOT NULL,
    no_of_seats INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (fitness_centre_id) REFERENCES fitness_centre(id) ON DELETE CASCADE,
    UNIQUE (fitness_centre_id, slot_date, start_time)
);

-- Booking table
CREATE TABLE IF NOT EXISTS booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    booked_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id) REFERENCES slot(id) ON DELETE CASCADE
);

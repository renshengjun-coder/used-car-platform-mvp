CREATE TABLE IF NOT EXISTS sellers (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(100),
    created_at    DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS listings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id     BIGINT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    price         DECIMAL(12,2) NOT NULL,
    make          VARCHAR(50),
    model         VARCHAR(50),
    year          SMALLINT,
    mileage       INT,
    fuel_type     VARCHAR(20),
    transmission  VARCHAR(20),
    city          VARCHAR(50),
    description   TEXT,
    status        VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    published_at  DATETIME NULL,
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME NOT NULL,
    INDEX idx_status_published (status, published_at),
    INDEX idx_seller (seller_id)
);

CREATE TABLE IF NOT EXISTS listing_photos (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id  BIGINT NOT NULL,
    url         VARCHAR(500) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    INDEX idx_listing (listing_id)
);

CREATE TABLE IF NOT EXISTS view_events (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(64) NOT NULL,
    user_id     BIGINT NULL,
    car_id      BIGINT NOT NULL,
    created_at  DATETIME NOT NULL,
    INDEX idx_session_car_time (session_id, car_id, created_at),
    INDEX idx_car_time (car_id, created_at)
);

CREATE TABLE IF NOT EXISTS search_events (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(64) NOT NULL,
    user_id     BIGINT NULL,
    keyword     VARCHAR(200) NULL,
    filters_json JSON NULL,
    created_at  DATETIME NOT NULL,
    INDEX idx_session_time (session_id, created_at)
);

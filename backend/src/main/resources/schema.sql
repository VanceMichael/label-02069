-- Blockchain Secondhand Market Database Schema
-- All Chinese data will be initialized by Java DataInitializer

CREATE DATABASE IF NOT EXISTS secondhand_market DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE secondhand_market;

SET NAMES utf8mb4;

-- user table
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `avatar` VARCHAR(255) DEFAULT NULL,
    `balance` DECIMAL(10,2) DEFAULT 0.00,
    `role` VARCHAR(20) DEFAULT 'USER',
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- category table
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `icon` VARCHAR(50) DEFAULT NULL,
    `sort` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- product table
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `seller_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10,2) NOT NULL,
    `original_price` DECIMAL(10,2) DEFAULT NULL,
    `category_id` BIGINT DEFAULT NULL,
    `category` VARCHAR(50) DEFAULT NULL,
    `images` VARCHAR(1000) DEFAULT NULL,
    `condition_level` TINYINT DEFAULT 9,
    `status` TINYINT DEFAULT 1,
    `view_count` INT DEFAULT 0,
    `blockchain_hash` VARCHAR(100) DEFAULT NULL,
    `blockchain_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- order table
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `buyer_id` BIGINT NOT NULL,
    `seller_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_title` VARCHAR(100) DEFAULT NULL,
    `product_image` VARCHAR(255) DEFAULT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` TINYINT DEFAULT 0,
    `pay_time` DATETIME DEFAULT NULL,
    `ship_time` DATETIME DEFAULT NULL,
    `complete_time` DATETIME DEFAULT NULL,
    `blockchain_hash` VARCHAR(100) DEFAULT NULL,
    `blockchain_time` DATETIME DEFAULT NULL,
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- blockchain_record table
DROP TABLE IF EXISTS `blockchain_record`;
CREATE TABLE `blockchain_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tx_hash` VARCHAR(100) NOT NULL,
    `block_number` BIGINT DEFAULT NULL,
    `data_type` VARCHAR(20) NOT NULL,
    `ref_id` BIGINT NOT NULL,
    `data_content` TEXT,
    `data_hash` VARCHAR(100) DEFAULT NULL,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tx_hash` (`tx_hash`),
    KEY `idx_data_type_ref` (`data_type`, `ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- operation_log table
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT DEFAULT NULL,
    `username` VARCHAR(50) DEFAULT NULL,
    `operation` VARCHAR(50) NOT NULL,
    `method` VARCHAR(200) DEFAULT NULL,
    `params` TEXT,
    `ip` VARCHAR(50) DEFAULT NULL,
    `result` TINYINT DEFAULT 1,
    `error_msg` TEXT,
    `cost_time` BIGINT DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기존 테이블 삭제 (있는 경우)
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `space_photo`;
DROP TABLE IF EXISTS `product_photo`;
DROP TABLE IF EXISTS `guest_book_card_photo`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `guest_book_card`;
DROP TABLE IF EXISTS `host_kakao`;
DROP TABLE IF EXISTS `space_host_map`;
DROP TABLE IF EXISTS `space`;
DROP TABLE IF EXISTS `host`;
DROP TABLE IF EXISTS `guest`;

CREATE TABLE `user`
(
    `id`       BIGINT       NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL,
    `email`    VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `post`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `content`   TEXT   NOT NULL,
    `author_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `guest`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `nickname`   VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `host`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `name`         VARCHAR(255) NULL,
    `picture_url`  VARCHAR(255) NULL,
    `agreed_terms` TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `space`
(
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `code`               VARCHAR(64)  NOT NULL,
    `name`               VARCHAR(255) NOT NULL,
    `description`        VARCHAR(255) NOT NULL DEFAULT '',
    `is_public`          TINYINT(1)   NOT NULL DEFAULT 0,
    `instagram_username` VARCHAR(255) NOT NULL DEFAULT '',
    `email`              VARCHAR(255) NOT NULL DEFAULT '',
    `created_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `space_host_map`
(
    `id`         BIGINT    NOT NULL AUTO_INCREMENT,
    `space_id`   BIGINT    NOT NULL,
    `host_id`    BIGINT    NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `host_kakao`
(
    `id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `host_id` BIGINT       NOT NULL,
    `user_id` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `guest_book_card`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `space_id`   BIGINT       NOT NULL,
    `guest_id`   BIGINT       NOT NULL,
    `message`    VARCHAR(500) NOT NULL,
    `is_read`    TINYINT      NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `guest_book_card_photo`
(
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `guest_book_card_id` BIGINT       NOT NULL,
    `original_name`      VARCHAR(255) NOT NULL,
    `path`               VARCHAR(255) NOT NULL,
    `capacity`           BIGINT       NOT NULL,
    `created_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `product`
(
    `id`                   BIGINT        NOT NULL AUTO_INCREMENT,
    `space_id`             BIGINT        NOT NULL,
    `title`                VARCHAR(255)  NOT NULL,
    `category`             VARCHAR(255)  NOT NULL DEFAULT '',
    `author_name`          VARCHAR(255)  NOT NULL DEFAULT '',
    `description`          VARCHAR(2000) NOT NULL,
    `video_url`            VARCHAR(512)  NOT NULL DEFAULT '',
    `is_video_after_photo` TINYINT       NOT NULL DEFAULT 0,
    `created_at`           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `product_photo`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `product_id`    BIGINT       NOT NULL,
    `sort_order`    INT          NOT NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `path`          VARCHAR(255) NOT NULL,
    `capacity`      BIGINT       NOT NULL,
    `created_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `space_photo`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `space_id`      BIGINT       NOT NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `path`          VARCHAR(255) NOT NULL,
    `capacity`      BIGINT       NOT NULL,
    `created_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- Foreign Key Constraints
ALTER TABLE `post`
    ADD CONSTRAINT `FK_user_TO_post` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`);

ALTER TABLE `space_host_map`
    ADD CONSTRAINT `FK_space_TO_space_host_map` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`);

ALTER TABLE `space_host_map`
    ADD CONSTRAINT `FK_host_TO_space_host_map` FOREIGN KEY (`host_id`) REFERENCES `host` (`id`);

ALTER TABLE `host_kakao`
    ADD CONSTRAINT `FK_host_TO_host_kakao` FOREIGN KEY (`host_id`) REFERENCES `host` (`id`);

ALTER TABLE `guest_book_card`
    ADD CONSTRAINT `FK_space_TO_guest_book_card` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`);

ALTER TABLE `guest_book_card`
    ADD CONSTRAINT `FK_guest_TO_guest_book_card` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`id`);

ALTER TABLE `guest_book_card_photo`
    ADD CONSTRAINT `FK_guest_book_card_TO_guest_book_card_photo` FOREIGN KEY (`guest_book_card_id`) REFERENCES `guest_book_card` (`id`);

ALTER TABLE `product`
    ADD CONSTRAINT `FK_space_TO_product` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`);

ALTER TABLE `product_photo`
    ADD CONSTRAINT `FK_product_TO_product_photo` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `space_photo`
    ADD CONSTRAINT `FK_space_TO_space_photo` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`);

CREATE UNIQUE INDEX `UX_space_code` ON `space` (`code`);

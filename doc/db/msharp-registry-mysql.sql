CREATE database if NOT EXISTS `xxl-registry` default character set utf8 collate utf8_general_ci;
use `xxl-registry`;

## 注册信息
CREATE TABLE `xxl_registry`
(
  `id`      int(11)      NOT NULL AUTO_INCREMENT,
  `biz`     varchar(255) NOT NULL COMMENT '业务标识',
  `env`     varchar(255) NOT NULL COMMENT '环境标识',
  `key`     varchar(255) NOT NULL COMMENT '注册Key',
  `data`    text         NOT NULL COMMENT '注册Value有效数据',
  `version` varchar(255) NOT NULL COMMENT '版本',
  `status`  tinyint(4)   NOT NULL DEFAULT '0' COMMENT '状态:0-正常、1-锁定、2-禁用,3-下线',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_biz_env_key` (`biz`, `env`, `key`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

## 注册表详细信息
CREATE TABLE `xxl_registry_data`
(
  `id`          int(11)      NOT NULL AUTO_INCREMENT,
  `registry_id` BIGINT(20)   NOT NULL COMMENT 'registry id',
  `biz`         varchar(255) NOT NULL COMMENT '业务标识',
  `env`         varchar(255) NOT NULL COMMENT '环境标识',
  `key`         varchar(255) NOT NULL COMMENT '注册Key',
  `value`       varchar(255) NOT NULL COMMENT '注册Value',
  `updateTime`  datetime     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_biz_env_key_value` (`biz`, `env`, `key`, `value`),
  key `idx_registry_id` (`registry_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# 消息队列
CREATE TABLE `message_queue`
(
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
  `biz`         varchar(255) NOT NULL DEFAULT '' COMMENT '业务标识',
  `env`         varchar(255) NOT NULL DEFAULT '' COMMENT '环境标识',
  `key`         varchar(255) NOT NULL DEFAULT '' COMMENT '注册Key',
  `sequence_id` bigint(20)   NOT NULL DEFAULT 0 COMMENT '序列id',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sequence_id` (`sequence_id`),
  UNIQUE KEY `uq_biz_env_key` (`biz`, `env`, `key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

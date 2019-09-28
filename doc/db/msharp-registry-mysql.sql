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
  UNIQUE KEY `I_b_e_k` (`biz`, `env`, `key`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

## 注册表详细信息
CREATE TABLE `xxl_registry_data`
(
  `id`         int(11)      NOT NULL AUTO_INCREMENT,
  `biz`        varchar(255) NOT NULL COMMENT '业务标识',
  `env`        varchar(255) NOT NULL COMMENT '环境标识',
  `key`        varchar(255) NOT NULL COMMENT '注册Key',
  `value`      varchar(255) NOT NULL COMMENT '注册Value',
  `updateTime` datetime     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_b_e_k_v` (`biz`, `env`, `key`, `value`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `xxl_registry_message`
(
  `id`      int(11)    NOT NULL AUTO_INCREMENT,
  `type`    tinyint(4) NOT NULL DEFAULT '0' COMMENT '消息类型：0-注册更新',
  `data`    text       NOT NULL COMMENT '消息内容',
  `addTime` datetime   NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
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
  UNIQUE KEY `I_b_e_k_v` (`biz`, `env`, `key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

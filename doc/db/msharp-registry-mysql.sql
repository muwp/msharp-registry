CREATE database if NOT EXISTS `msharp-registry` default character set utf8 collate utf8_general_ci;
use `msharp-registry`;

## 注册服务信息
CREATE TABLE `registry_service`
(
  `id`           bigint(22)   NOT NULL AUTO_INCREMENT,
  `appkey`       varchar(255) NOT NULL COMMENT '业务标识',
  `env`          varchar(255) NOT NULL COMMENT '环境标识',
  `service_name` varchar(255) NOT NULL COMMENT '注册Key',
  `data`         text         NOT NULL COMMENT '注册Value有效数据',
  `version`      varchar(255) NOT NULL COMMENT '版本',
  `status`       tinyint(4)   NOT NULL DEFAULT '0' COMMENT '状态:0-正常、1-锁定、2-禁用,3-下线',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_biz_env_key` (`appkey`, `env`, `service_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

## 注册表详细信息
CREATE TABLE `client_node`
(
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `client_appkey` varchar(50)  NOT NULL COMMENT '客户端appkey',
  `env`           varchar(255) NOT NULL COMMENT '环境标识',
  `service_name`  varchar(255) NOT NULL COMMENT '服务名称',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
    COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_service_name_env_client_appkey` (`service_name`, `client_appkey`, `env`),
  key `update_time` (`update_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

## 注册结点表详细信息
CREATE TABLE `registry_node`
(
  `id`           bigint(22)    NOT NULL AUTO_INCREMENT,
  `registry_id`  BIGINT(20)    NOT NULL COMMENT 'registry_id',
  `appkey`       varchar(255)  NOT NULL COMMENT '业务标识',
  `env`          varchar(255)  NOT NULL COMMENT '环境标识',
  `service_name` varchar(255)  NOT NULL COMMENT '注册serviceName',
  `value`        varchar(255)  NOT NULL COMMENT '注册Value',
  `status`       int(11)       NOT NULL DEFAULT 1 COMMENT '状态:0-删除 1-正常',
  `meta`         varchar(1000) NOT NULL DEFAULT '' COMMENT '注册结点元数据',
  `updateTime`   datetime      NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_biz_env_key_value` (`appkey`, `env`, `service_name`, `value`),
  key `idx_registry_id` (`registry_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# 消息队列
CREATE TABLE `message_queue`
(
  `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
  `appkey`       varchar(255) NOT NULL DEFAULT '' COMMENT '业务标识',
  `env`          varchar(255) NOT NULL DEFAULT '' COMMENT '环境标识',
  `service_name` varchar(255) NOT NULL DEFAULT '' COMMENT '注册Key',
  `sequence_id`  bigint(20)   NOT NULL DEFAULT 0 COMMENT '序列id',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sequence_id` (`sequence_id`),
  UNIQUE KEY `uq_biz_env_key` (`appkey`, `env`, `service_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

##
CREATE TABLE `biz_token`
(
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `client_appkey` VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '',
  `token`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '',
  `env`           VARCHAR(25)  NOT NULL DEFAULT 0,
  `status`        tinyint(4)   NOT NULL DEFAULT '0',
  `create_time`   DATETIME     NOT NULL DEFAULT '1970-10-10 10:00:00' COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
    COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_appkey_env` (`client_appkey`, `env`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='注册中心的鉴权中心';



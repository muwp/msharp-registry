## 《分布式服务注册中心XXL-REGISTRY》

[![Build Status](https://travis-ci.org/xuxueli/xxl-registry.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-registry)
[![Docker Status](https://img.shields.io/badge/docker-passing-brightgreen.svg)](https://hub.docker.com/r/xuxueli/xxl-registry-admin/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-registry/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-registry.svg)](https://github.com/xuxueli/xxl-registry/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat-square)](http://www.xuxueli.com/page/donate.html)


## 一、简介

### 1.1 概述
XXL-REGISTRY 是一个轻量级分布式服务注册中心，在分布式系统中提供服务注册与发现功能。现已开放源代码，开箱即用。

### 1.2 特性

- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心内部10s会全量同步一次磁盘数据，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置三种状态：
    - 正常状态=支持动态注册、发现，服务注册信息实时更新；
    - 锁定状态=人工维护注册信息，服务注册信息固定不变；
    - 禁用状态=禁止使用，服务注册信息固定为空；
- 7、跨语言：注册中心提供HTTP接口供客户端实用，语言无关，通用性更强；
- 8、兼容性：项目立项之初是为XXL-RPC量身设计，但是不限于XXL-RPC使用。兼容支持任何服务框架服务注册实用，如dubbo、springboot等；
- 9、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现 "服务注册中心" 产品开箱即用；

### 1.3 下载

#### 文档地址

- [中文文档](http://www.xuxueli.com/xxl-registry/)

#### 源码仓库地址

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-registry](https://github.com/xuxueli/xxl-registry) | [Download](https://github.com/xuxueli/xxl-registry/releases)
[https://gitee.com/xuxueli0323/xxl-registry](https://gitee.com/xuxueli0323/xxl-registry) | [Download](https://gitee.com/xuxueli0323/xxl-registry/releases)  


#### 技术交流
- [社区交流](http://www.xuxueli.com/page/community.html)


### 1.4 环境
- Maven3+
- Jdk1.7+
- Mysql5.6+


## 二、快速入门

### 2.1 初始化 "服务注册中心" 数据库
请下载项目源码并解压，获取 "服务注册中心" 数据库初始化SQL脚本并执行即可

数据库初始化SQL脚本位置为:

    /xxl-registry/doc/db/xxl-registry-mysql.sql
    
"服务注册中心" 支持集群部署，集群情况下各节点务必连接同一个mysql实例;

### 2.2 编译项目
解压源码,按照maven格式将源码导入IDE, 使用maven进行编译即可，源码结构如下：

    - /doc
    - /xxl-registry-admin       ：分布式服务中心
    - /xxl-registry-client      ：客户端核心依赖；

### 2.3 配置部署“服务注册中心”

#### 步骤一：配置项目：
配置文件地址：

```
/xxl-registry/xxl-registry-admin/src/main/resources/application.properties
```

消息中心配置内容说明：

```
### 数据库配置
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-registry?Unicode=true&characterEncoding=UTF-8

### 注册中心，心跳间隔，单位秒
xxl.registry.beattime=10
### 服务注册数据磁盘同步目录
xxl.registry.data.filepath=/data/applogs/xxl-registry/registrydata

### 登陆信息配置
xxl.registry.login.username=admin
xxl.registry.login.password=123456
``` 

#### 步骤二：部署项目：

如果已经正确进行上述配置，可将项目编译打包部署。
访问地址：http://localhost:8080/xxl-registry-admin  (该地址接入方项目将会使用到，作为注册地址)，登录后运行界面如下图所示

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-registry/master/doc/images/img_01.png "在这里输入图片标题")

至此“服务注册中心”项目已经部署成功。

#### 步骤三：服务注册中心集群（可选）：
服务注册中心支持集群部署，提升消息系统容灾和可用性。

集群部署时，几点要求和建议：
- DB配置保持一致；
- 登陆账号配置保持一致；
- 集群机器时钟保持一致（单机集群忽视）；
- 建议：推荐通过nginx为集群做负载均衡，分配域名。访问、客户端使用等操作均通过该域名进行。

#### 其他：Docker 镜像方式搭建消息中心：
- 下载镜像

```
// Docker地址：https://hub.docker.com/r/xuxueli/xxl-registry-admin/
docker pull xuxueli/xxl-registry-admin
```

- 创建容器并运行

```
docker run -p 8080:8080 -v /tmp:/data/applogs --name xxl-registry-admin  -d xuxueli/xxl-registry-admin

/**
* 如需自定义 mysql 等配置，可通过 "PARAMS" 指定；
* 配置项参考文件：/xxl-registry/xxl-registry-admin/src/main/resources/application.properties
*/
docker run -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-registry?Unicode=true&characterEncoding=UTF-8" -p 8080:8080 -v /tmp:/data/applogs --name xxl-registry-admin  -d xuxueli/xxl-registry-admin
```



## 四、系统设计

### 4.1 系统架构图
![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-registry/master/doc/images/img_02.png "在这里输入图片标题")

### 4.2 核心思想
提供稳定高性能的RPC远程服务调用功能，简化分布式服务通讯开发。

### 4.3 角色构成
- 1、provider：服务提供方；
- 2、invoker：服务消费方；
- 3、serializer: 序列化模块；
- 4、remoting：服务通讯模块；
- 5、registry：服务注册中心；
- 6、admin：服务治理、监控中心：管理服务节点信息，统计服务调用次数、QPS和健康情况；（非必选，暂未整理发布...）


##### 原理    
“XXL-RPC原生轻量级注册中心”内部通过广播机制，集群节点实时同步服务注册信息，确保一致。客户端借助 long pollong 实时感知服务注册信息，简洁、高效；

“XXL-RPC原生轻量级注册中心”对外提供的API服务：
- /registry/registry：服务注册API
    - 说明：接入方向注册中心注册服务使用，接入方需要循环心跳注册，间隔周期与注册中心一致；
    - 参数：
        - biz：业务线标识
        - env：环境标识
        - keys：批量服务注册key，推荐批量注册
        - value：服务注册值，通常为服务IP端口地址
- /registry/remove：服务摘除API
    - 说明：接入方向注册中心注摘除服务使用，服务停止时触发一次即可，将会立即广播全部节点、并通知各接入方服务下线；
    - 参数：
        - biz：业务线标识
        - env：环境标识
        - keys：批量服务注册key，推荐批量注册
        - value：服务注册值，通常为服务IP端口地址
- /registry/discovery：服务发现API
    - 说明：接入方发现注册中心服务使用，建议接入方循环请求该接口，用于全量同步服务信息，间隔周期与注册中心一致；该服务只会查询磁盘数据，性能非常高；
    - 参数：
        - biz：业务线标识
        - env：环境标识
        - keys：批量服务注册key，推荐批量注册
- /registry/monitor ：服务实时监控API
    - 说明：接入方监控注册中心服务变动使用，该接口为 long polling 接口，将会阻塞三倍注册中心心跳时间，期间如监控的服务由变动将会立即响应通知客户端；
    接入方可以结合“服务实时监控API”与“服务发现API”一起实现服务的实时感知。循环请求前者阻塞监控服务变动信息，得到监控响应时主动全量同步一次即可。
    - 参数：
        - biz：业务线标识
        - env：环境标识
        - keys：批量服务注册key，推荐批量注册

“XXL-RPC原生轻量级注册中心”更易于集群部署、横向扩展，搭建与学习成本更低，推荐采用该方式；


## 五、版本更新日志
### 5.1 版本 v1.1 新特性
- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心内部10s会全量同步一次磁盘数据，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置三种状态：
    - 正常状态=支持动态注册、发现，服务注册信息实时更新；
    - 锁定状态=人工维护注册信息，服务注册信息固定不变；
    - 禁用状态=禁止使用，服务注册信息固定为空；
- 7、跨语言：注册中心提供HTTP接口供客户端实用，语言无关，通用性更强；
- 8、兼容性：项目立项之初是为XXL-RPC量身设计，但是不限于XXL-RPC使用。兼容支持任何服务框架服务注册实用，如dubbo、springboot等；
- 9、容器化：提供官方docker镜像，并实时更新推送dockerhub，进一步实现 "服务注册中心" 产品开箱即用；


## 六、其他

### 6.1 项目贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/xuxueli/xxl-registry/issues/) 讨论新特性或者变更。

### 6.2 用户接入登记
更多接入的公司，欢迎在 [登记地址](https://github.com/xuxueli/xxl-registry/issues/1 ) 登记，登记仅仅为了产品推广。

### 6.3 开源协议和版权
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### 捐赠
无论金额多少都足够表达您这份心意，非常感谢 ：）      [前往捐赠](http://www.xuxueli.com/page/donate.html )

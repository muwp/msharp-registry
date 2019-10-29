<p align="center">
    <h3 align="center">MSHARP-REGISTRY</h3>
    <p align="center">
        MSHARP-REGISTRY, A lightweight distributed service registry and discovery platform.
        <br> 
</p>

## Introduction
MSHARP-REGISTRY 是一个轻量级分布式服务注册中心，只依赖mysql,拥有"轻量级、秒级注册上线、多环境、跨语言"等特性,开箱即用。

## Features
- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心会定期全量同步数据至磁盘文件，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置多状态进行管理
- 7、跨语言：注册中心提供HTTP接口（RESTFUL 格式）供客户端实用，语言无关，通用性更强；
- 8、跨机房：得益于服务注册中心集群关系对等特性，集群各节点提供幂等的配置服务;
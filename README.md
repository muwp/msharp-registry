<p align="center">
    <h3 align="center">MSHARP-REGISTRY</h3>
    <p align="center">
        MSHARP-REGISTRY, A lightweight distributed service registry and discovery platform.
        <br> 
</p>


## Introduction

MSHARP-REGISTRY is a lightweight distributed service registry and discovery platform.
Features such as "lightweight, second dynamic registration, multi-environment, cross-language, cross-room deployment".
Now, it's already open source, real "out-of-the-box".

MSHARP-REGISTRY 是一个轻量级分布式服务注册中心，拥有"轻量级、秒级注册上线、多环境、跨语言、跨机房"等特性。现已开放源代码，开箱即用。


## Documentation
- [中文文档](http://www.ruijing.com/msharp-registry/)


## Features

- 1、轻量级：基于DB与磁盘文件，只需要提供一个DB实例即可，无第三方依赖；
- 2、实时性：借助内部广播机制，新服务上线、下线，可以在1s内推送给客户端；
- 3、数据同步：注册中心会定期全量同步数据至磁盘文件，清理无效服务，确保服务数据实时可用；
- 4、性能：服务发现时仅读磁盘文件，性能非常高；服务注册、摘除时通过磁盘文件校验，防止重复注册操作；
- 5、扩展性：可方便、快速的横向扩展，只需保证服务注册中心配置一致即可，可借助负载均衡组件如Nginx快速集群部署；
- 6、多状态：服务内置三种状态：
    - 正常状态=支持动态注册、发现，服务注册信息实时更新；
    - 锁定状态=人工维护注册信息，服务注册信息固定不变；
    - 禁用状态=禁止使用，服务注册信息固定为空；
- 7、跨语言：注册中心提供HTTP接口（RESTFUL 格式）供客户端实用，语言无关，通用性更强；
- 8、跨机房：得益于服务注册中心集群关系对等特性，集群各节点提供幂等的配置服务；因此，异地跨机房部署时，只需要请求本机房服务注册中心即可，实现异地多活；
- 9、访问令牌（accessToken）：为提升系统安全性，注册中心和客户端进行安全性校验，双方AccessToken匹配才允许通讯；


## Communication

- [社区交流](http://www.ruijing.com/page/community.html)


## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue] to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue] 讨论新特性或者变更。


产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

# 基于微服务的车票管理系统:blush:
#### 功能描述：微服务车票系统，分管理员和普通用户两种角色。除了基本功能，可实现限流，一人一单，每日热搜等功能  
#### 涉及技术栈：MySQL，Redis，RabbitMQ，MybatisPlus，SpringBoot，SpringCloud，Seata，Sentinel，Gateway，Jsoup，Nginx，Docker  
***
>管理员样例
>> 账号：wsh  
>> 密码：wsh
>> 
>普通用户样例  
>> 账号：test  
>> 密码：test
>> 
>云服务器配置：内存2核8G(服务器到期，暂时不续费)
>[测试地址](http://123.207.210.137/)    
***
## 设计流程
### 1. 基本框架
+ SpringBoot构建后台管理系统，Redis+Token+MVC拦截器做权限校验
+ 基于Nacos做服务和配置中心，共包含用户、订单、车次、事务、网关、验证码六个服务
+ :star:利用OpenFeign做同步调用，利用RabbitMQ异步调用验证码服务
### 2. 数据库及事务
+ 基于MySQL存储业务数据，相关字段建立联合索引
+ 基于Redis缓存Token、验证码、MQ分布式ID和每日热搜，基于Redisson解决一人一单
+ :star:基于Seata解决订单和车次服务之间的分布式事务
### 3. 流量与进程监控
+ :star:基于Gateway针对同一IP的所有请求做令牌桶限流，基于Sentinel对热门车次的购票请求做限流降级
+ 基于jmx和visualVM监控业务服务的内存和CPU消耗
### 4. 网络编程
+ 利用Httpclient调用第三方API，利用FastJSON解析响应
+ :star:利用Scheduled开启CompletableFuture任务，利用Jsoup每日定期爬取新闻热搜

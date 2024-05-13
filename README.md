# SpringBoot 项目初始模板

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

只需 1 分钟即可完成内容网站的后端！！！大家还可以在此基础上快速开发自己的项目。

项目地址：https://github.com/nanshuo0814/springboot-template

# 快速开始

- 该模板基于 **Spring Boot 2.7.6**，使用 Maven 管理依赖。项目运行前请先浏览配置文件**application.yml**，改好所需的相关配置，检查 **todo** 注释。
- 项目里使用了Spring AOP切面，通过自定义@Check和@CheckParam注解来就行权限校验、登录校验、参数校验等等。
- 使用MyBatis-Plus，配置了MyBatis-Plus的分页插件，进行简单的crud操作。
- 集成了阿里云OSS，方便上传文件到OSS。
- 集成了腾讯云COS，方便上传文件到COS。
- 邮箱验证码库+自定义邮箱验证码工具类，方便发送验证码。
- 图片验证码库+自定义图片验证码工具类，方便生成图片验证码。
- 自定义全局异常拦截器和工具类，方便统一处理异常。
- 配置跨域拦截器，方便跨域请求。
- 整合redisession+spring data redis及其工具类，方便使用redis缓存。
- 统一返回响应格式，方便前后端交互。
- 错误状态码枚举，方便前端根据错误状态码进行错误提示。
- Jackson+自定义Json工具类实现序列化和反序列化，方便前后端交互。
- EasyExcel，方便excel导入导出。
- hutool工具类，方便开发。
- devtools热部署，方便开发。
- sql脚本，提供测试创建数据库和表结构的脚本。
- ...

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.6（贼新）
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储
- 阿里云 OSS 对象存储

### 工具类

- Easy Excel 表格处理
- Hutool 工具库
- Apache Commons Lang3 工具类
- Lombok 注解
- Jackson JSON 序列化

### 业务特性

- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户、帖子、帖子点赞、帖子收藏表）
- 用户登录、注册、注销、更新、检索、权限管理
- 帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
- 帖子点赞、取消点赞
- 帖子收藏、取消收藏、检索已收藏帖子
- 帖子全量同步 ES、增量同步 ES 定时任务
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层
# SpringBoot 项目初始模板

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

只需 1 分钟即可完成内容网站的后端！！！大家还可以在此基础上快速开发自己的项目。

项目地址：https://github.com/nanshuo0814/springboot-template

# 技术选型

- Spring Boot: 2.7.6
- Spring AOP: 2.7.6
- Java: 1.8
- MySQL 驱动: 8.0.33
- MyBatis: 2.2.2
- MyBatis-Plus: 3.5.2
- Knife4j: 3.0.3
- 阿里云 OSS SDK: 3.15.1
- Activation: 1.1.1
- Mail: 1.4.7
- Email: 1.5
- Jackson: 2.16.1
- EasyExcel: 3.1.1
- hutool-All: 5.8.8
- devtools: 热部署
- redission: 3.26.0
- Lombok: 1.18.24
- Elasticsearch：2.7.6

# 快速开始

- 该模板基于 **Spring Boot 2.7.6**，使用 Maven 管理依赖。项目运行前请先浏览配置文件**application.yml**，改好所需的相关配置，检查 **todo** 注释。

- 项目里使用了Spring AOP切面，通过自定义@Check和@CheckParam注解来就行权限校验、登录校验、参数校验、打印日志等。

- 使用MyBatis-Plus，配置了MyBatis-Plus的分页插件，进行简单的crud操作。

- 集成了阿里云OSS，方便上传文件到OSS。
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



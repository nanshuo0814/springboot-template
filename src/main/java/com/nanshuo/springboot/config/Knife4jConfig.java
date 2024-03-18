package com.nanshuo.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 接口文档配置
 *
 * @author 小鱼儿
 * @date 2024/01/06 21:42:46
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Knife4jConfig {

    @Bean(value = "dockerBean")
    public Docket dockerBean() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("接口文档")
                        .contact(new Contact("小鱼儿", "https://blog.ydg.icu/", "inanshuo@foxmail.com"))
                        .description("springboot-template")
                        .termsOfServiceUrl("https://doc.xiaominfo.com/")
                        .version("1.0")
                        .build())
                //分组名称
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.nanshuo.springboot.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}

//Swagger 提供了一系列的注解，用于在生成 API 文档时提供更详细的信息。以下是一些常用的 Swagger 注解：
//
//基本信息注解：
//
//@Api: 用于描述整个 API 文档的信息，包括标题、描述、版本等。
//@ApiModel: 用于描述数据模型（DTO等）的信息。
//@ApiModelProperty: 用于描述模型属性的信息。
//操作注解：
//
//@ApiOperation: 用于描述单个 API 操作，提供对该操作的详细描述。
//@ApiParam: 用于描述操作参数的信息。
//@ApiResponse: 用于描述操作响应的信息。
//@ApiResponses: 用于包裹多个 @ApiResponse。
//分组注解：
//
//@Api(tags = "分组名称"): 用于对 API 进行分组，方便在 Swagger 文档中查看。
//支持注解：
//
//@ApiSupport: 这可能是你项目中自定义的注解，用于提供额外的支持信息，例如作者、排序等。
//其他注解：
//
//@ApiIgnore: 用于标记不需要在 Swagger 文档中展示的类、方法或字段。
//@ApiImplicitParam 和 @ApiImplicitParams: 用于描述请求参数的信息。
//SpringFox 提供的额外注解：
//
//@RequestHeader, @RequestParam: 用于描述请求头和请求参数的信息，SpringFox 提供的额外注解，用于增强 Swagger 的支持。
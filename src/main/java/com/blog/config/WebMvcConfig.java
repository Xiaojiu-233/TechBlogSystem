package com.blog.config;

import com.blog.utils.JacksonObjectMapper;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        System.out.println("开始进行资源映射……");
//        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");//
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
//        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//    }
//
//    //扩展MVC框架的消息转换器
//    @Override
//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //创建消息转换器对象
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        //设置对象转换器，底层使用Jackson将Java类对象转换为Json
//        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        //将上面的消息转换器追加到mvc框架的转换器集合中
//        converters.set(0,messageConverter);
//    }
//
//    @Bean
//    public Docket createRestApi(){
////        文档类型
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.xiaojiu.reggie.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("小究的外卖")
//                .version("1.0")
//                .description("我的外卖接口文档")
//                .build();
//    }

}

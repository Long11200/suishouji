package com.zl.blog.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zl.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

/**
 * @author shkstart
 * @create 2022-06-23 17:50
 */
//...
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，不可设置为*，不安全, 前后端分离项目，可能域名不一致
        //本地测试 端口不一致 也算跨域
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/comments/create/change")
                .addPathPatterns("/articles/publish");
    }

//    /**
//     * 注册自定义类型转换器
//     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //获取Json转换器
//        MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) converters.get(0);
//        //转换Long 和 long类型
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(Long.class, new LongToStringSerializer());
//        module.addSerializer(long.class, new LongToStringSerializer());
//        //
//        converter.getObjectMapper().registerModule(module);
//    }
//
//    public static class LongToStringSerializer extends JsonSerializer<Long> {
//        @Override
//        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            if (null != value) {
//                gen.writeString(value.toString());
//            } else {
//                gen.writeNull();
//            }
//        }
//    }
}

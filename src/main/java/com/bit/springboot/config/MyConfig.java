package com.bit.springboot.config;

import com.bit.springboot.josn.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author LeLe
 * @date 2024/5/23 2:38
 * @Description:
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setObjectMapper(new JacksonObjectMapper());
//        converters.add(0,converter);
//    }
}

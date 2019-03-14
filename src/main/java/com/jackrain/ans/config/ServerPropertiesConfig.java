package com.jackrain.ans.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * @author: z.c
 * @since: 2019/1/16
 * create at : 2019/1/16 3:42 PM
 *
 */
@Configuration
public class ServerPropertiesConfig {

    @Bean("restTemplateRibbon")
    @LoadBalanced
    public RestTemplate restTemplateRibbon(SimpleClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);

        List<HttpMessageConverter<?>> httpMessageConverterList= restTemplate.getMessageConverters();
        //创建FastJson信息转换对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //创建Fastjosn对象并设定序列化规则
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue);
        // 中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);//设定json格式且编码为UTF-8
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        //规则赋予转换对象
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        httpMessageConverterList.add(0,fastJsonHttpMessageConverter);
        return restTemplate;
    }

}

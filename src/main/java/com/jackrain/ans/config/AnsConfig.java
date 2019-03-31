package com.jackrain.ans.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.jackrain.ans.server.AnsApplicationProperties;
import com.jackrain.ans.server.HeartBeatServer;
import com.jackrain.nea.web.conf.CusResponseErrorHandler;
import com.jackrain.nea.web.conf.CusRestTemplate;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.alicloud.ans.registry.AnsAutoServiceRegistration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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
@Slf4j
public class AnsConfig {

    @Bean("restTemplateRibbon")
    @LoadBalanced
    public CusRestTemplate restTemplateRibbon(SimpleClientHttpRequestFactory factory){
        CusRestTemplate restTemplate = new CusRestTemplate(factory);
        restTemplate.setErrorHandler(new CusResponseErrorHandler());

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
        //添加拦截器 添加cookie
        restTemplate.setInterceptors( Collections.singletonList(new AuthRequestInterceptor()));
        return restTemplate;
    }

    @Bean
    public AnsApplicationProperties ansApplicationProperties(){
        return new AnsApplicationProperties();
    }

    @Bean
    public HeartBeatServer heartBeatServer(){
        return new HeartBeatServer();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                // 如果在Cookie内通过如下方式取
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        requestTemplate.header(cookie.getName(), cookie.getValue());
                    }
                } else {
                    log.warn("FeignHeadConfiguration", "获取Cookie失败！");
                }
            }
        };
    }



}

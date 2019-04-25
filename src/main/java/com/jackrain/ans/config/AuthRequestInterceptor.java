package com.jackrain.ans.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RestTemplate权限拦截 添加相关Header
 * @author: yao.j
 * @since: 2019/3/15
 * create at : 2019/3/15 12:32 PM
 */
@Slf4j
public class AuthRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            // 如果在Cookie内通过如下方式取
            Cookie[] cookieArr = request.getCookies();
            List<String> cookieList = new ArrayList<>();
            if (cookieArr != null && cookieArr.length > 0) {
                for (Cookie cookie : cookieArr) {
                    cookieList.add( cookie.getName()+"="+cookie.getValue());
                }
            } else {
                log.warn("HeadConfiguration", "获取Cookie失败！");
            }

            if(cookieList != null){
                headers.put(HttpHeaders.COOKIE,cookieList);
            }
        }
        return clientHttpRequestExecution.execute(httpRequest,bytes);
    }
}

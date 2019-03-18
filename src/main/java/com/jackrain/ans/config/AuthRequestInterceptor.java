package com.jackrain.ans.config;

import lombok.extern.slf4j.Slf4j;
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
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    headers.add(cookie.getName(), cookie.getValue());
                }
            } else {
                log.warn("HeadConfiguration", "获取Cookie失败！");
            }
        }
        return clientHttpRequestExecution.execute(httpRequest,bytes);
    }
}

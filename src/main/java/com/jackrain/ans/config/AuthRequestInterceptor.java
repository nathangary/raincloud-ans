package com.jackrain.ans.config;

import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * RestTemplate权限拦截 添加相关Header
 * @author: yao.j
 * @since: 2019/3/15
 * create at : 2019/3/15 12:32 PM
 */
@Slf4j
public class AuthRequestInterceptor implements ClientHttpRequestInterceptor {

    private final static String ASYNC_MAIN_TASK_ID = "ASYNC_MAIN_TASK_ID";
    private final static String ASYNC_PARENT_TASK_ID = "ASYNC_PARENT_TASK_ID";
    private final static String COOKIE = "COOKIE";

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HashMap header = null != R3ThreadLocal.local.get() ? R3ThreadLocal.local.get() : new HashMap();
        log.info("authRequestInterceptor pass successfully");

        List<String> cookieList = new ArrayList<>();
        Object mainTaskId = null;
        Object parentTaskId = null;
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            // 如果在Cookie内通过如下方式取
            Cookie[] cookieArr = request.getCookies();

            if (cookieArr != null && cookieArr.length > 0) {
                for (Cookie cookie : cookieArr) {
                    cookieList.add(new String(cookie.getName()+"="+cookie.getValue()));
                }
            }

            mainTaskId = attrs.getRequest().getHeader(ASYNC_MAIN_TASK_ID);
            parentTaskId = attrs.getRequest().getHeader(ASYNC_PARENT_TASK_ID);

        }else {
            Object cookie = header.get(COOKIE);
            if (null != cookie){
                JSONArray cookies = (JSONArray) cookie;
                for (Object o : cookies){
                    cookieList.add(new String(String.valueOf(o)));
                }
            }
        }

        if(cookieList != null ){
            headers.put(HttpHeaders.COOKIE,cookieList);
            log.info(String.format("Request:%s,HeadConfiguration:%s",httpRequest.getURI(),cookieList));
        }else{
            log.warn(String.format("Request:请求Cookie失败,HeadConfiguration:%s",httpRequest.getURI(),cookieList));
        }

        if (null == mainTaskId){
            mainTaskId = header.get(ASYNC_MAIN_TASK_ID);
        }
        if (null == parentTaskId){
            parentTaskId = header.get(ASYNC_PARENT_TASK_ID);
        }
        if (null != mainTaskId){
            headers.add(ASYNC_MAIN_TASK_ID,new String(String.valueOf(mainTaskId)));
        }
        if (null != parentTaskId){
            headers.add(ASYNC_PARENT_TASK_ID,new String(String.valueOf(parentTaskId)));
        }
        return clientHttpRequestExecution.execute(httpRequest,bytes);
    }
}

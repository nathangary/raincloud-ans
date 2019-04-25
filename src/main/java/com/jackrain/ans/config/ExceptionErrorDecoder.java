package com.jackrain.ans.config;

import com.alibaba.fastjson.JSON;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 重写错误解析
 *
 * @author: z.c
 * @since: 2019/1/22
 * create at : 2019/1/22 11:34 AM
 *
 */
@Slf4j
@Configuration
public class ExceptionErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {

            if (response.body() != null) {
                String body = Util.toString(response.body().asReader());
                Object o = JSON.parse(body);
                return (Exception) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FeignException.errorStatus(methodKey, response);
    }
}

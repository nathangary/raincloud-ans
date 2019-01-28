package com.jackrain.ans.server;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 *
 *
 * @author: z.c
 * @since: 2019/1/23
 * create at : 2019/1/23 9:57 AM
 *
 */
@Component
@ConfigurationProperties(prefix = "spring.cloud.burgeon.ans")
@Data
public class AnsApplicationProperties {

//    @Value("${spring.cloud.burgeon.ans.heartBea}")
    private boolean heartBeat = true;


}

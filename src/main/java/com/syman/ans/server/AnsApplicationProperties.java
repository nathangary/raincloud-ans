package com.syman.ans.server;

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
public class AnsApplicationProperties {

    public Boolean getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(Boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

//    @Value("${spring.cloud.burgeon.ans.heartBea}")
    private Boolean heartBeat;

}

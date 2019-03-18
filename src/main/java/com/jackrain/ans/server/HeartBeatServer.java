package com.jackrain.ans.server;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alicloud.ans.registry.AnsRegistration;
import org.springframework.cloud.alicloud.context.ans.AnsProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 发送心跳服务
 *
 * @author: z.c
 * @since: 2019/1/15
 * create at : 2019/1/15 3:58 PM
 */
@Slf4j
public class HeartBeatServer {

    @Autowired
    private AnsProperties ansProperties;

    @Autowired
    private AnsRegistration ansRegistration;

    @Autowired
    private AnsApplicationProperties ansApplicationProperties;

    private RestTemplate restTemplate;

    public HeartBeatServer() {
        new HeartBeat();
    }

    /**
     * 定时心跳
     */
    private class HeartBeat implements Runnable {

        private Thread thread = new Thread(this, "com.syman.ans.server.heartbeat.task");

        public HeartBeat() {
            this.thread.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sendHeartBeat();
                    //60秒一次
                    Thread.sleep(60000L);
                } catch (Exception var3) {
                    log.error("check task error.", var3);
                }
            }
        }
    }

    /**
     * 发送心跳
     */
    private void sendHeartBeat() {

        try {

            if (ansApplicationProperties == null) {
                log.debug("ansApplicationProperties is null");
                return;
            }

            if (ansProperties == null) {
                log.debug("ansProperties is null");
                return;
            }
//            if (serverProperties == null) {
//                log.error("serverProperties is null");
//                return;
//            }

            boolean heartBeat = ansApplicationProperties.isHeartBeat();
            if (!heartBeat){
                log.debug("heartBeat=====>" + heartBeat);
                return;
            }

            String ip = ansProperties.getClientIp();
//            Integer port = serverProperties.getPort();
            int port = ansProperties.getClientPort();
            String serverList = ansProperties.getServerList();
            String serverPort = ansProperties.getServerPort();
            String dom = ansRegistration.getServiceId();

            log.debug("ip:" + ip + " port:" + port + " serverList:" + serverList + " serverPort:" + serverPort + " dom:" + dom);

            request(ip, port, serverList, serverPort, dom);

        } catch (Exception e) {
//            e.printStackTrace();
            log.error("sendHeartBeat Exception==>" + e.getMessage());
        }

    }

    /**
     * 请求服务端
     *
     * @param ip
     * @param port
     * @param url
     * @param serverPort
     */
    private void request(String ip, int port, String url, String serverPort, String dom) {

        if (restTemplate == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setReadTimeout(30000);
            factory.setConnectTimeout(10000);
            restTemplate = new RestTemplate(factory);
        }
        //
        JSONObject paramObject = new JSONObject();
        paramObject.put("ip", ip);
        paramObject.put("port", port);
        paramObject.put("dom", dom);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("param", paramObject.toJSONString());

        url = "http://" + url + ":" + serverPort + "/vipserver/api/heartbeat";
        log.debug(url);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "*");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<JSONObject> resp = restTemplate.postForEntity(url, request, JSONObject.class);

//        LOG.debug(resp.getBody().toJSONString());


    }

}

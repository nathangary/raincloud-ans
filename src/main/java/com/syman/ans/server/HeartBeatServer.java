package com.syman.ans.server;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
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
 * @author: z.c
 * @since: 2019/1/15
 * create at : 2019/1/15 3:58 PM
 */
@Service
public class HeartBeatServer {

    private static Logger LOG = LoggerFactory.getLogger(HeartBeatServer.class);

    @Autowired
    private AnsProperties ansProperties;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private AnsRegistration ansRegistration;

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

        public void run() {
            while (true) {
                try {
                    sendHeartBeat();
                    //60秒一次
                    Thread.sleep(60000L);
                } catch (Exception var3) {
                    LOG.error("check task error.", var3);
                }
            }
        }
    }

    /**
     * 发送心跳
     */
    private void sendHeartBeat() {

        try {

            if (ansProperties == null) {
                LOG.error("ansProperties is null");
                return;
            }
            if (serverProperties == null) {
                LOG.error("serverProperties is null");
                return;
            }

            String ip = ansProperties.getClientIp();
            int port = serverProperties.getPort();
            String serverList = ansProperties.getServerList();
            String serverPort = ansProperties.getServerPort();
            String dom = ansRegistration.getServiceId();

//            LOG.debug("ip:" + ip + " port:" + port + " serverList:" + serverList + " serverPort:" + serverPort + " dom:" + dom);

            request(ip,port,serverList,serverPort,dom);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 请求服务端
     * @param ip
     * @param port
     * @param url
     * @param serverPort
     */
    private void request(String ip,int port,String url,String serverPort,String dom){

        if (restTemplate == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setReadTimeout(30000);
            factory.setConnectTimeout(10000);
            restTemplate = new RestTemplate(factory);
        }
        //
        JSONObject paramObject = new JSONObject();
        paramObject.put("ip",ip);
        paramObject.put("port",port);
        paramObject.put("dom",dom);
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("param",paramObject.toJSONString());

        url = "http://" + url + ":" + serverPort + "/vipserver/api/heartbeat";
        LOG.debug(url);

        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept","*");
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map,headers);
        ResponseEntity<JSONObject> resp = restTemplate.postForEntity(url,request,JSONObject.class);

//        LOG.debug(resp.getBody().toJSONString());


    }

}

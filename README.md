# raincloud-ans

#### 介绍
 新ans增加了心跳机制，关闭了原来的健康检查，解决配置中心和服务提供者不再同一网段，IP检查不通出现的配置中心服务列表里不显示服务的问题。

#### 软件架构
    #定时（1分钟）心跳发送，走的http协议。
    #配置中心接受心跳并处理，2分钟未发送心跳视为断开，支持断开重连，服务提供者无需重启。

#### 使用说明
    #maven使用方式：
    <dependency>
        <groupId>org.syman</groupId>
        <artifactId>raincloud-ans</artifactId>
        <version>${parent.version}</version>
    </dependency>
     
    Application主入口加上@EnableDiscoveryClient注解即可
    #配置参数 true开启心跳，false关闭心跳，默认false
    spring.cloud.burgeon.ans.heartBeat=false


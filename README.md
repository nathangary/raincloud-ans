# raincloud-ans

#### 介绍
 新ans增加了心跳机制，解决配置中心和服务提供者不再同一网段，IP检查不通出现的配置中心服务列表里不显示服务的问题。

#### 软件架构
    #定时心跳发送，走的http协议。

#### 使用说明
    #maven使用方式：
    <dependency>
        <groupId>org.syman</groupId>
        <artifactId>raincloud-ans</artifactId>
        <version>${parent.version}</version>
    </dependency>
     
    Application主入口加上@EnableDiscoveryClient注解即可


package com.chen.zookeeper.register;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import java.io.IOException;

/**
 * 服务提供者
 * @author ChenTian
 * @date 2020/7/10
 */
public class ProviderServer {
    public static void main(String[] args) {

        new ProviderServer().openServer(-1);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openServer(int port) {
        //构建应用
        ApplicationConfig config = new ApplicationConfig();
        config.setName("sample-service");

        //通信协议
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo",port);
        protocolConfig.setThreads(200);

        ServiceConfig<UserService>  serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(config);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        serviceConfig.setInterface(UserService.class);
        UserServiceImpl ref = new UserServiceImpl();
        serviceConfig.setRef(ref);

        //开始提供服务
        serviceConfig.export();
        System.out.println("服务启动完成！端口："+serviceConfig.getExportedUrls().get(0).getPort());
    }
}

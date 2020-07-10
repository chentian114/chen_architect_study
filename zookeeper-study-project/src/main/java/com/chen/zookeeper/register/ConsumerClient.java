package com.chen.zookeeper.register;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import java.io.IOException;

/**
 * 服务调用者
 * @author ChenTian
 * @date 2020/7/10
 */
public class ConsumerClient {

    private UserService service;

    public static void main(String[] args) {
        ConsumerClient client = new ConsumerClient();
        client.buildService("");
        String cmd;

        while (!(cmd = read()).equals("exit")){
            try {
                String result = client.service.get(Integer.valueOf(cmd));
                System.out.println("result:" + result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String read() {
        byte[] b = new byte[1024];
        try {
            System.out.print("请输入：");
            int size = System.in.read(b);
            return new String(b, 0, size).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserService buildService(String url) {
        ApplicationConfig config = new ApplicationConfig("client-sample");
        // 构建一个引用对象
        ReferenceConfig<UserService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(config);
        referenceConfig.setInterface(UserService.class);
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        referenceConfig.setTimeout(5000);
        // 透明化
        this.service = referenceConfig.get();
        return service;
    }
}

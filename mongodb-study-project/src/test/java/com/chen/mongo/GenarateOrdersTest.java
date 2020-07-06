package com.chen.mongo;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.chen.MongoDBServiceApplication;
import com.chen.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@SpringBootTest(classes = MongoDBServiceApplication.class)
@RunWith(SpringRunner.class)
public class GenarateOrdersTest {

    private static final Logger logger = LoggerFactory.getLogger(GenarateOrdersTest.class);

    @Test
    public void test(){
        System.out.println(System.currentTimeMillis());
        System.out.println(DateUtil.offsetMonth(RandomUtil.randomDay(1,20),-66));
    }
    @Resource
    private MongoOperations tempelate;

    //随机生成orderTest数据
    @Test
    public void batchInsertOrder() {
        String[] userCodes = new String[] { "user1", "user2", "user3", "user4",
                "user5", "user6", "user7"};
        String[] auditors = new String[] { "auditor1","auditor2","auditor3","auditor4","auditor5"};
        List<Order> list = new ArrayList<Order>();
        Random rand = new Random();
        for (int i = 0; i < 100000; i++) {
            Order order = new Order();
            int num = rand.nextInt(userCodes.length);
            order.setUserCode(userCodes[num]);
            order.setOrderCode(UUID.randomUUID().toString());
            order.setOrderTime(DateUtil.offsetMonth(RandomUtil.randomDay(1,20),-66));//randomDate("2015-01-01","2017-10-31")
            order.setPrice(RandomUtil.randomInt(10000));
            int length = rand.nextInt(5)+1;
            String[] temp = new String[length];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = getFromArrays(temp,auditors,rand);
            }
            order.setAuditors(temp);
            list.add(order);
        }
        tempelate.insertAll(list);
    }


    private String getFromArrays(String[] temp, String[] auditors, Random rand) {
        String ret = null;
        boolean test = true;
        while (test) {
            ret = auditors[rand.nextInt(5)];
            int i =0;
            for (String _temp : temp) {
                i++;
                if(ret.equals(_temp)){
                    break;
                }
            }
            if(i==temp.length){
                test=false;
            }

        }
        return ret;

    }


}

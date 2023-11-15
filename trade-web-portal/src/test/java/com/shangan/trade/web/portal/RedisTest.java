package com.shangan.trade.web.portal;

import com.shangan.trade.lightning.deal.utils.RedisWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {


    @Autowired
    public RedisWorker redisWorker;

    @Test
    public void setValue() {
        redisWorker.setValue("testName","你好");
    }

    @Test
    public void getValue() {
        System.out.println(redisWorker.getValueByKey("testName"));
    }

    @Test
    public void setStockTest() {
        //stock:秒杀活动ID    库存数
        redisWorker.setValue("stock:668899", 29L);
    }

    @Test
    public void stockCheckTest() {
        redisWorker.stockDeductCheck("stock:668899");
        System.out.println(redisWorker.getValueByKey("stock:668899"));
    }

    @Test
    public void setJmeterStockTest(){
        redisWorker.setValue("stock:5", 2L);
    }
    //test
    @Test
    public void stockCheckTest2() {
        redisWorker.stockDeductCheck("stock:5");
        System.out.println(redisWorker.getValueByKey("stock:5"));
    }
}

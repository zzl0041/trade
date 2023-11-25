package com.shangan.trade.order;

import com.shangan.trade.order.service.LimitBuyService;
import com.shangan.trade.order.service.RiskBlackListService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlackListTest {
    @Autowired
    public RiskBlackListService riskBlackListService;

    @Test
    public void addBlockUser(){
        riskBlackListService.addRiskBlackListMember(123456);
    }

    @Test
    public void removeBlockUser(){
        riskBlackListService.removeRiskBlackListMember(1);
    }
}

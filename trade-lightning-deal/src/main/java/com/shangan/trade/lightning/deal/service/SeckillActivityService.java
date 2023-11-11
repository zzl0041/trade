package com.shangan.trade.lightning.deal.service;

import com.shangan.trade.lightning.deal.db.model.SeckillActivity;

import java.util.List;

public interface SeckillActivityService {

    /**
     * 插入一个秒杀活动
     *
     * @param seckillActivity
     * @return
     */
    boolean insertSeckillActivity(SeckillActivity seckillActivity);

    /**
     * 查询秒杀活动
     *
     * @param id
     * @return
     */
    SeckillActivity querySeckillActivityById(long id);

    List<SeckillActivity> queryActivitysByStatus(int status);

}

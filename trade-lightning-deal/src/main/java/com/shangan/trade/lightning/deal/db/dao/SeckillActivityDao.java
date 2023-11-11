package com.shangan.trade.lightning.deal.db.dao;
import com.shangan.trade.lightning.deal.db.model.SeckillActivity;

import java.util.List;

public interface SeckillActivityDao {


    /**
     * 插入一个秒杀活动
     *
     * @param seckillActivity
     * @return
     */
    boolean insertSeckillActivity(SeckillActivity seckillActivity);


    /**
     * 查询秒杀活动
     * @param id
     * @return
     */
    SeckillActivity querySeckillActivityById(long id);

    /**
     * 根据活动状态查询秒杀活动列表
     * @param status
     * @return
     */
    List<SeckillActivity> queryActivitysByStatus(int status);

    /**
     * 更新可用库存
     * @param id
     * @return
     */
    boolean updateAvailableStockByPrimaryKey(long id);

    /**
     * 锁定秒杀的库存
     * @param id
     * @return
     */
    boolean lockStock(long id);

    /**
     * 库存扣减
     * @param id
     * @return
     */
    boolean deductStock(long id);

    /**
     * 锁定的库存回补
     * @param id
     * @return
     */
    boolean revertStock(long id);
}

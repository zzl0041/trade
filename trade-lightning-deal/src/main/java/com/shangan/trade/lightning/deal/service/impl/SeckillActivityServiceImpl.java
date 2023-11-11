package com.shangan.trade.lightning.deal.service.impl;

import com.shangan.trade.lightning.deal.db.dao.SeckillActivityDao;
import com.shangan.trade.lightning.deal.db.model.SeckillActivity;
import com.shangan.trade.lightning.deal.service.SeckillActivityService;
import com.shangan.trade.lightning.deal.utils.RedisWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityDao seckillActivityDao;

    @Autowired
    private RedisWorker redisWorker;


    @Override
    public boolean insertSeckillActivity(SeckillActivity seckillActivity) {
        return seckillActivityDao.insertSeckillActivity(seckillActivity);
    }


    @Override
    public SeckillActivity querySeckillActivityById(long id) {
        return seckillActivityDao.querySeckillActivityById(id);
    }

    @Override
    public List<SeckillActivity> queryActivitysByStatus(int status) {
        return seckillActivityDao.queryActivitysByStatus(status);
    }

    /**
     * 处理秒杀请求
     * 高并发时会出现超卖
     *
     * @param seckillActivityId
     * @return
     */
    @Override
    public boolean processSeckillReqBase(long seckillActivityId) {
        //1.查询对应的秒杀活动信息
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);

        if (seckillActivity == null) {
            log.error("seckillActivityId={} 查询不到对应的秒杀活动", seckillActivityId);
            throw new RuntimeException("查询不到对应的秒杀活动");
        }
        int availableStock = seckillActivity.getAvailableStock();
        if (availableStock > 0) {
            log.info("商品抢购成功");
            seckillActivityDao.updateAvailableStockByPrimaryKey(seckillActivityId);
            return true;
        } else {
            log.info("商品抢购失败，商品已经售完");
            return false;
        }
    }

    /**
     * 处理秒杀请求
     * 通过Redis Lua脚本先进行校验
     *
     * @param seckillActivityId
     * @return
     */
    @Override
    public boolean processSeckill(long seckillActivityId) {
        String key = "stock:" + seckillActivityId;
        boolean checkReslut = redisWorker.stockDeductCheck(key);
        if (!checkReslut) {
            return false;
        }

        //1.查询对应的秒杀活动信息
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);

        if (seckillActivity == null) {
            log.error("seckillActivityId={} 查询不到对应的秒杀活动", seckillActivityId);
            throw new RuntimeException("查询不到对应的秒杀活动");
        }
        int availableStock = seckillActivity.getAvailableStock();
        if (availableStock > 0) {
            log.info("商品抢购成功");
            seckillActivityDao.updateAvailableStockByPrimaryKey(seckillActivityId);
            return true;
        } else {
            log.info("商品抢购失败，商品已经售完");
            return false;
        }
    }
}

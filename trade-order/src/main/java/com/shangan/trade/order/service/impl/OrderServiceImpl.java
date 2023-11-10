package com.shangan.trade.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.shangan.trade.goods.db.model.Goods;
import com.shangan.trade.goods.service.GoodsService;
import com.shangan.trade.order.db.dao.OrderDao;
import com.shangan.trade.order.db.model.Order;
import com.shangan.trade.order.mq.OrderMessageSender;
import com.shangan.trade.order.service.OrderService;
import com.shangan.trade.order.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderMessageSender orderMessageSender;

    /**
     * datacenterId;  数据中心
     * machineId;     机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */
    private SnowflakeIdWorker snowFlake = new SnowflakeIdWorker(6, 8);


    /**
     * 创建订单和库存锁定在一个事务中，要么同时成功，要么同时失败
     * 使用 @Transactional(rollbackFor = Exception.class)
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order createOrder(long userId, long goodsId) {
        Order order = new Order();
        //普通商品购买默认无活动
        order.setId(snowFlake.nextId());
        order.setActivityId(0L);
        order.setActivityType(0);
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        /*
         * 状态:0,没有可用库存订单创建失败;1,已创建，等待付款;2 已支付,等待发货;99 订单关闭，超时未付款
         */
        order.setStatus(1);
        order.setCreateTime(new Date());

        //1.商品查询
        Goods goods = goodsService.queryGoodsById(goodsId);
        if (goods == null) {
            log.error("goods is null goodsId={},userId={}", goodsId, userId);
            throw new RuntimeException("商品不存在");
        }
        //2.判断库存是否充足
        if (goods.getAvailableStock() <= 0) {
            log.error("goods stock not enough goodsId={},userId={}", goodsId, userId);
            throw new RuntimeException("商品库存库存不足");
        }

        //3.锁定库存
        boolean lockResult = goodsService.lockStock(goodsId);
        if (!lockResult) {
            log.error("order lock stock error order={}", JSON.toJSONString(order));
            throw new RuntimeException("订单锁定库存失败");
        }

        //4.创建订单
        order.setPayPrice(goods.getPrice());
        boolean insertResult = orderDao.insertOrder(order);
        if (!insertResult) {
            log.error("order insert error order={}", JSON.toJSONString(order));
            throw new RuntimeException("订单生成失败");
        }
        return order;
    }

    @Override
    public Order queryOrder(long orderId) {
        return orderDao.queryOrderById(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payOrder(long orderId) {
        log.info("支付订单  订单号：{}", orderId);
        Order order = orderDao.queryOrderById(orderId);
        /*
         * 订单支付校验
         * 1.判断订单号对应订单是否存在
         * 2.判断查询到的订单状态是否为未支付
         */
        if (order == null) {
            log.error("orderId={} 对应订单不存在", orderId);
            throw new RuntimeException("对应订单不存在");
        }
        if (order.getStatus() != 1) {
            log.error("orderId={} 订单状态无法支付", orderId);
            throw new RuntimeException("订单状态无法支付");
        }
        //Mock 模拟调用支付平台
        log.info("调用第三方支付平台付款.......");

        order.setPayTime(new Date());
        /*
         * 0:没有可用库存，无效订单
         * 1:已创建等待付款
         * 2:支付完成
         */
        order.setStatus(2);
        boolean updateRessult = orderDao.updateOrder(order);
        if (!updateRessult) {
            log.error("orderId={} 订单支付状态更新失败", orderId);
            throw new RuntimeException("订单支付状态更新失败");
        }

        //库存扣减
        boolean deductResult = goodsService.deductStock(order.getGoodsId());
        if (!deductResult) {
            log.error("orderId={} 库存扣减失败", orderId);
            throw new RuntimeException("库存扣减失败");
        }
    }

}
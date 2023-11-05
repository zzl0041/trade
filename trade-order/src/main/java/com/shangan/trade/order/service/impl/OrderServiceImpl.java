package com.shangan.trade.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.shangan.trade.goods.db.dao.GoodsDao;
import com.shangan.trade.goods.db.model.Goods;
import com.shangan.trade.order.db.dao.OrderDao;
import com.shangan.trade.order.db.model.Order;
import com.shangan.trade.order.service.OrderService;
import com.shangan.trade.order.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private GoodsDao goodsDao;

    /**
     * datacenterId;  数据中心
     * machineId;     机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */
    private SnowflakeIdWorker snowFlake = new SnowflakeIdWorker(6, 8);


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
        Goods goods = goodsDao.queryGoodsById(goodsId);
        if (goods == null) {
            log.error("goods is null goodsId={},userId={}", goodsId, userId);
            return null;
        }
        order.setPayPrice(goods.getPrice());
        boolean res = orderDao.insertOrder(order);
        if (!res) {
            log.error("order insert error order={}", JSON.toJSONString(order));
            return null;
        }
        return order;
    }

    @Override
    public Order queryOrder(long orderId) {
        return orderDao.queryOrderById(orderId);
    }

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
            return;
        }
        if (order.getStatus() != 1) {
            log.error("orderId={}  订单状态无法支付：", orderId);
            return;
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
        orderDao.updateOrder(order);
    }
}
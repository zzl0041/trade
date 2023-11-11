package com.shangan.trade.web.portal.controller;

import com.alibaba.fastjson.JSON;
import com.shangan.trade.goods.db.model.Goods;
import com.shangan.trade.goods.service.GoodsService;
import com.shangan.trade.goods.service.SearchService;
import com.shangan.trade.lightning.deal.db.model.SeckillActivity;
import com.shangan.trade.lightning.deal.service.SeckillActivityService;
import com.shangan.trade.order.db.model.Order;
import com.shangan.trade.order.service.OrderService;
import com.shangan.trade.web.portal.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class PortalController {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillActivityService seckillActivityService;

    /**
     * 跳转到主页面
     *
     * @return
     */
    @RequestMapping("/goods_detail")
    public String index() {
        return "goods_detail";
    }

    /**
     * 商品详情页
     *
     * @param goodsId
     * @return
     */
    @RequestMapping("/goods/{goodsId}")
    public ModelAndView itemPage(@PathVariable long goodsId) {
        Goods goods = goodsService.queryGoodsById(goodsId);
        log.info("goodsId={},goods={}", goodsId, JSON.toJSON(goods));
        String showPrice = CommonUtils.changeF2Y(goods.getPrice());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("showPrice", showPrice);
        modelAndView.setViewName("goods_detail");
        return modelAndView;
    }

    /**
     * 购买请求处理
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @RequestMapping("/buy/{userId}/{goodsId}")
    public ModelAndView buy(@PathVariable long userId, @PathVariable long goodsId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            log.info("userId={}, goodsId={}", userId, goodsId);
            Order order = orderService.createOrder(userId, goodsId);
            //下单成功
            modelAndView.addObject("order", order);
            modelAndView.addObject("resultInfo", "下单成功");
            modelAndView.setViewName("buy_result");
            return modelAndView;
        } catch (Exception e) {
            //下单失败
            log.error("buy error,errorMessage:{}", e.getMessage());
            modelAndView.addObject("resultInfo", "下单失败,原因" + e.getMessage());
            modelAndView.setViewName("buy_result");
            return modelAndView;
        }
    }

    /**
     * 跳转到搜索页
     *
     * @return
     */
    @RequestMapping("/search")
    public String searchPage() {
        return "search";
    }

    /**
     * 搜索查询
     *
     * @return
     */
    @RequestMapping("/searchAction")
    public String search(@RequestParam("searchWords") String searchWords, Map<String, Object> resultMap) {
        log.info("search searchWords:{}", searchWords);
        List<Goods> goodsList = searchService.searchGoodsList(searchWords, 0, 10);
        resultMap.put("goodsList", goodsList);
        return "search";
    }

    @RequestMapping("/order/query/{orderId}")
    public String orderQuery(Map<String, Object> resultMap, @PathVariable long orderId) {
        Order order = orderService.queryOrder(orderId);
        log.info("orderId={} order={}", orderId, JSON.toJSON(order));
        String orderShowPrice = CommonUtils.changeF2Y(order.getPayPrice());
        resultMap.put("order", order);
        resultMap.put("orderShowPrice", orderShowPrice);
        return "order_detail";
    }

    /**
     * 订单支付
     *
     * @return
     */
    @RequestMapping("/order/payOrder/{orderId}")
    public String payOrder(Map<String, Object> resultMap, @PathVariable long orderId) throws Exception {
        try {
            orderService.payOrder(orderId);
            return "redirect:/order/query/" + orderId;
        } catch (Exception e) {
            log.error("payOrder error,errorMessage:{}", e.getMessage());
            resultMap.put("errorInfo", e.getMessage());
            return "error";
        }
    }


    /**
     * 秒杀活动详情页
     *
     * @param resultMap
     * @param seckillId
     * @return
     */
    @RequestMapping("/seckill/{seckillId}")
    public String seckillInfo(Map<String, Object> resultMap, @PathVariable long seckillId) {
        try {
            SeckillActivity seckillActivity = seckillActivityService.querySeckillActivityById(seckillId);
            if (seckillActivity == null) {
                log.error("秒杀的对应的活动信息 没有查询到 seckillId:{} ", seckillId);
                throw new RuntimeException("秒杀的对应的活动信息 没有查询到");
            }
            log.info("seckillId={},seckillActivity={}", seckillId, JSON.toJSON(seckillActivity));
            String seckillPrice = CommonUtils.changeF2Y(seckillActivity.getSeckillPrice());
            String oldPrice = CommonUtils.changeF2Y(seckillActivity.getOldPrice());
            Goods goods = goodsService.queryGoodsById(seckillActivity.getGoodsId());
            if (goods == null) {
                log.error("秒杀的对应的商品信息 没有查询到 seckillId:{} goodsId:{}", seckillId, seckillActivity.getGoodsId());
                throw new RuntimeException("秒杀的对应的商品信息 没有查询到");
            }

            resultMap.put("seckillActivity", seckillActivity);
            resultMap.put("seckillPrice", seckillPrice);
            resultMap.put("oldPrice", oldPrice);
            resultMap.put("goods", goods);
            return "seckill_item";
        } catch (Exception e) {
            log.error("获取秒杀信息详情页失败 get seckillInfo error,errorMessage:{}", e.getMessage());
            resultMap.put("errorInfo", e.getMessage());
            return "error";
        }
    }

    /**
     * 获取秒杀活动列表
     *
     * @param resultMap
     * @return
     */
    @RequestMapping("/seckill/list")
    public String activityList(Map<String, Object> resultMap) {
        List<SeckillActivity> seckillActivities = seckillActivityService.queryActivitysByStatus(1);
        resultMap.put("seckillActivities", seckillActivities);
        return "seckill_activity_list";
    }


    @RequestMapping("/seckill/buy/{userId}/{seckillId}")
    public ModelAndView seckill(@PathVariable long userId, @PathVariable long seckillId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Order order = seckillActivityService.processSeckill(userId, seckillId);
            modelAndView.addObject("resultInfo", "秒杀抢购成功");
            modelAndView.addObject("order", order);
            modelAndView.setViewName("buy_result");
        } catch (Exception e) {
            modelAndView.addObject("errorInfo", e.getMessage());
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}

package com.shangan.trade.web.portal.controller;

import com.alibaba.fastjson.JSON;
import com.shangan.trade.goods.db.model.Goods;
import com.shangan.trade.goods.service.GoodsService;
import com.shangan.trade.goods.service.SearchService;
import com.shangan.trade.order.db.model.Order;
import com.shangan.trade.order.service.OrderService;
import com.shangan.trade.web.portal.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String buy(Map<String, Object> resultMap, @PathVariable long userId, @PathVariable long goodsId) {
        log.info("userId={}, goodsId={}", userId, goodsId);
        Order order = orderService.createOrder(userId, goodsId);
        resultMap.put("order", order);
        resultMap.put("resultInfo", "下单成功");
        return "buy_result";
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

}

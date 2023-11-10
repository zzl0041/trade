package com.shangan.trade.goods.service;

import com.shangan.trade.goods.db.model.Goods;

import java.util.List;


public interface SearchService {

    /**
     * 添加商品到ES中
     * @param goods
     */
    void addGoodsToES(Goods goods);

    /**
     * 根据关键词搜索
     * @param keyword
     * @param from
     * @param size
     * @return
     */
    List<Goods> searchGoodsList(String keyword, int from, int size);
}

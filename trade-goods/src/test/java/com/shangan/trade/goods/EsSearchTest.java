package com.shangan.trade.goods;

import com.alibaba.fastjson.JSON;
import com.shangan.trade.goods.db.model.Goods;
import com.shangan.trade.goods.model.Person;
import com.shangan.trade.goods.service.SearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class EsSearchTest {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private SearchService searchService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void esTest() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("http://127.0.0.1/", 9200, "http")
        ));
        System.out.println(JSON.toJSONString(client));
    }

    /**
     * 添加文档
     *
     * @throws Exception
     */
    @Test
    public void addDoc() throws Exception {
        Person person = new Person();
        person.setId("125");
        person.setName("张学友");
        person.setAddress("香港铜锣湾");
        person.setAge(18);
        //将对象转为json
        String data = JSON.toJSONString(person);
        IndexRequest request = new IndexRequest("person").id(person.getId()).source(data, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * 查询所有
     */
    @Test
    public void matchAll() throws IOException {
        //构建查询请求，指定查询的索引库
        SearchRequest searchRequest = new SearchRequest("person");

        //创建查询条件构造器 SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /*
         * 构建查询条件
         * 查询所有文档
         */
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();


        //指定查询条件
        searchSourceBuilder.query(query);

        /*
         * 指定分页查询信息
         * 从哪里开始查
         */
        searchSourceBuilder.from(0);
        //每次查询的数量
        searchSourceBuilder.size(2);

        searchRequest.source(searchSourceBuilder);

        //查询获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse));

        //获取命中对象
        SearchHits searchHits = searchResponse.getHits();
        long totalNum = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + totalNum);

        List<Person> personList = new ArrayList<>();
        //获取命中的hits数据,搜索结果数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = searchHit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString, Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }

    /**
     * term词条查询
     */
    @Test
    public void termAll() throws IOException {
        //构建查询请求，指定查询的索引库
        SearchRequest searchRequest = new SearchRequest("person");

        //创建查询条件构造器 SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /*
         * 构建查询条件
         * 查询所有文档
         */
        TermQueryBuilder query = QueryBuilders.termQuery("address", "台北");


        //指定查询条件
        searchSourceBuilder.query(query);

        /*
         * 指定分页查询信息
         * 从哪里开始查
         */
        searchSourceBuilder.from(0);
        //每次查询的数量
        searchSourceBuilder.size(5);

        searchRequest.source(searchSourceBuilder);

        //查询获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse));

        //获取命中对象
        SearchHits searchHits = searchResponse.getHits();
        long totalNum = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + totalNum);

        List<Person> personList = new ArrayList<>();
        //获取命中的hits数据,搜索结果数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = searchHit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString, Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }

    /**
     * term词条查询
     */
    @Test
    public void mathc() throws IOException {
        //构建查询请求，指定查询的索引库
        SearchRequest searchRequest = new SearchRequest("person");

        //创建查询条件构造器 SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /*
         * 构建查询条件
         * 查询所有文档
         */
        MatchQueryBuilder query = QueryBuilders.matchQuery("name", "张学友");


        //指定查询条件
        searchSourceBuilder.query(query);

        /*
         * 指定分页查询信息
         * 从哪里开始查
         */
        searchSourceBuilder.from(0);
        //每次查询的数量
        searchSourceBuilder.size(5);

        searchRequest.source(searchSourceBuilder);

        //查询获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse));

        //获取命中对象
        SearchHits searchHits = searchResponse.getHits();
        long totalNum = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + totalNum);

        List<Person> personList = new ArrayList<>();
        //获取命中的hits数据,搜索结果数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = searchHit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString, Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }

    /**
     * term词条查询
     */
    @Test
    public void queryString() throws IOException {
        //构建查询请求，指定查询的索引库
        SearchRequest searchRequest = new SearchRequest("person");

        //创建查询条件构造器 SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /*
         * 构建查询条件
         * 查询所有文档
         */
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("香港 OR 台湾").field("name").field("address").defaultOperator(Operator.OR);


        //指定查询条件
        searchSourceBuilder.query(query);

        /*
         * 指定分页查询信息
         * 从哪里开始查
         */
        searchSourceBuilder.from(0);
        //每次查询的数量
        searchSourceBuilder.size(5);

        searchRequest.source(searchSourceBuilder);

        //查询获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse));

        //获取命中对象
        SearchHits searchHits = searchResponse.getHits();
        long totalNum = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + totalNum);

        List<Person> personList = new ArrayList<>();
        //获取命中的hits数据,搜索结果数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = searchHit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString, Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }


    /**
     * term词条查询
     */
    @Test
    public void queryMatch() throws IOException {
        //构建查询请求，指定查询的索引库
        SearchRequest searchRequest = new SearchRequest("goods");

        //创建查询条件构造器 SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        /**
         * 查询单个,等于
         */

        //单个匹配，搜索name为li的文档
        // QueryBuilder query = QueryBuilders.matchQuery("name", "张学*");

        //搜索名字中含有li文档（name中只要包含li即可）
        //WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name","张学*");
//搜索name中或nickname中包含有li的文档（必须与li一致）
        //  QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("张学友","name", "address");

        // WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("张学*","name","address");

//        //创建查询条件构造器 SearchSourceBuilder
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        //模糊查询
//        WildcardQueryBuilder queryBuilder1 = QueryBuilders.wildcardQuery("name", "*张学*");//搜索name中含有 张学 的文档
//        WildcardQueryBuilder queryBuilder2 = QueryBuilders.wildcardQuery("address", "*香*");//搜索address中含有 香 的文档
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        //name中必须含有 张学,address中必须含有 香 ,must相当于SQL中的 and
//        boolQueryBuilder.must(queryBuilder1);
//        boolQueryBuilder.must(queryBuilder2);
//
//        //指定查询条件
//        searchSourceBuilder.query(boolQueryBuilder);


//        //创建查询条件构造器 SearchSourceBuilder
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        //模糊查询
//        WildcardQueryBuilder queryBuilder1 = QueryBuilders.wildcardQuery("name", "*张学*");//搜索name中含有 张学 的文档
//        WildcardQueryBuilder queryBuilder2 = QueryBuilders.wildcardQuery("address", "*台*");//搜索address中含有 香 的文档
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        //name中必须含有 张学,address中必须含有 香 ,should 相当于SQL中的 or
//        boolQueryBuilder.should(queryBuilder1);
//        boolQueryBuilder.should(queryBuilder2);


        String keyword ="曜金黑";
        // WildcardQueryBuilder queryBuilder_title = QueryBuilders.wildcardQuery("title", "*曜金黑*");
        //description中含有 keyword 的文档
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("8G+256G","description");
        //  WildcardQueryBuilder queryBuilder_description = QueryBuilders.wildcardQuery("description", "*全网通*");
        //keywords中含有 keyword 的文档
        //WildcardQueryBuilder queryBuilder_keywords = QueryBuilders.wildcardQuery("keywords", "*曜金黑*");
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //should 相当于SQL中的 or
        // boolQueryBuilder.should(queryBuilder_title);
        //  boolQueryBuilder.should(queryBuilder_description);
        // boolQueryBuilder.should(queryBuilder_keywords);

        //指定查询条件
        searchSourceBuilder.query(queryBuilder);

        /*
         * 指定分页查询信息
         * 从哪里开始查
         */
        searchSourceBuilder.from(0);
        //每次查询的数量
        searchSourceBuilder.size(5);

        searchRequest.source(searchSourceBuilder);

        //查询获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse));

        //获取命中对象
        SearchHits searchHits = searchResponse.getHits();
        long totalNum = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + totalNum);

        List<Goods> goodsList = new ArrayList<>();
        //获取命中的hits数据,搜索结果数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = searchHit.getSourceAsString();
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }

        System.out.println(JSON.toJSONString(goodsList));
    }

    /**
     * 向ES中添加商品数据
     */
    @Test
    public void addGoodsToES() {

        Goods goods = new Goods();
        goods.setTitle("华为mate50 pro");
        goods.setBrand("华为");
        goods.setCategory("手机");
        goods.setNumber("NO12360");
        goods.setImage("test");
        goods.setDescription("华为mate50 新品手机 曜金黑 8G+256G 全网通");
        goods.setKeywords("华为mate50 新品手机 曜金黑");
        goods.setSaleNum(58);
        goods.setAvailableStock(10000);
        goods.setPrice(899999);
        goods.setStatus(1);
        goods.setId(25L);
        searchService.addGoodsToES(goods);
    }

    @Test
    public void goodsSearch(){
        List<Goods> goodsList = searchService.searchGoodsList("曜金黑", 0, 10);
        System.out.println(JSON.toJSONString(goodsList));
    }
}
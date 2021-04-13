package com.akira;

import com.akira.domain.User;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class EsDemoApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
    }

    /********************* 索引API *********************/
    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("idx_java");
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("idx_java");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(exists);
    }

    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("idx_java");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /********************* Document API *********************/

    @Test
    void testAddDoc() throws IOException {
        User user = new User("akira", 26);
        IndexRequest request = new IndexRequest("idx_java");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(3L));
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(response);
        System.out.println(response.status());
    }

    @Test
    void testIsExistDoc() throws IOException {
        GetRequest request = new GetRequest("idx_java", "1");
        //不获取上下文 提高效率
        request.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testGetDoc() throws IOException {
        GetRequest request = new GetRequest("idx_java", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testUpdateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("idx_java", "1");
        User user = new User("akira_v2", 26);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("idx_java", "1");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testBulkAddDoc() throws IOException {

        BulkRequest request = new BulkRequest("idx_java");
        request.timeout(TimeValue.timeValueSeconds(3L));

        for (int i = 0; i < 6; i++) {
            request.add(new IndexRequest()
                    .id(i + 1 + "")
                    .source(JSON.toJSONString(
                            new User("akira" + (i + 1), 20)),
                            XContentType.JSON));
        }

        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(response.status());
    }

    @Test
    void testSearchDoc() throws IOException {
        SearchRequest request = new SearchRequest("idx_java");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("age", 20);
        builder.query(queryBuilder);
        request.source(builder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));

        System.out.println("**********************");
        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}

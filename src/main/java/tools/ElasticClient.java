package tools;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import items.NewsItem;
import items.UrlItem;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class ElasticClient {
    private static ElasticClient instance = null;

    ElasticsearchClient client;
    private final String indexName = "articles";
    private final String dbURL = "http://localhost:9200";

    private ElasticClient() {

        // Установка соединения с клиентом
        RestClient restClient = RestClient
                .builder(HttpHost.create(dbURL))
                .build();
        ObjectMapper mapper = JsonMapper.builder().build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(mapper));

        this.client = new ElasticsearchClient(transport);
    }

    public static synchronized ElasticClient getInstance() {
        if (instance == null)
            instance = new ElasticClient();

        return instance;
    }

    public synchronized void createIndex() {
        try {
            if (client.indices().exists(ex -> ex.index(indexName)).value()) {
                // TODO: Добавить лог
                return;
            }

            client.indices().create(c -> c.index(indexName)
                    .mappings(mp -> mp
                            .properties("title", p -> p.text(t -> t))
                            .properties("body", p -> p.text(t -> t))
                            .properties("date", p -> p.text(t -> t))
                            .properties("url", p -> p.text(t -> t))
                            .properties("hash", p -> p.text(t -> t))
                    ));
        } catch (IOException e) {
            // TODO: Добавить лог
        }
    }

    public synchronized void insertNote(NewsItem item) {
        try {
            IndexResponse response =client
                    .index(i -> i
                    .index(indexName)
                    .document(item));

        } catch (IOException e) {
            // TODO: Добавить лог
        }
    }

    public synchronized Boolean checkNote(UrlItem item) {
        SearchResponse<NewsItem> res = null;
        try {
            res = client.search(s -> s
                    .index(indexName)
                    .query(q -> q
                            .match(t -> t
                                    .field("hash")
                                    .query(item.getHash()))), NewsItem.class);
        } catch (IOException e) {
            // TODO: Добавить лог
        }

        return res.hits().total().value() != 0;
    }

    public void searchNote(UrlItem item) {}
    public synchronized void printNote() {}


}

package tools;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
import java.util.List;

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

    public void searchNote() {
        SearchResponse<NewsItem> response = null;
        MgetResponse<NewsItem> mgetResponses = null;

        try {
             System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- AND QUERY -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
             response = andQuery();
             printNote(response);

             System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- OR QUERY -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
             response = orQuery();
             printNote(response);

             System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- Script QUERY -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
             response = scriptQuery();
             printNote(response);

             System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+- MultiGet QUERY -+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
             mgetResponses = mgetQuery();
             printMultiGet(mgetResponses);

        } catch (IOException e) {
            // TODO: Добавить лог
        }
    }

    // Собственные функции поиска
    private SearchResponse<NewsItem> andQuery() throws IOException {

        // Задание условий A и B
        Query condA = MatchQuery.of(m -> m.field("title")
                        .query("Европы"))
                        ._toQuery();

        Query condB = MatchQuery.of(m -> m.field("date")
                        .query("5/31/2024"))
                        ._toQuery();

        // Запрос A and B
        SearchResponse<NewsItem> response = client
                .search(s -> s.query(q -> q.bool(b -> b.must(condA).must(condB))), NewsItem.class);

        return response;
    }

    private SearchResponse<NewsItem> orQuery() throws IOException {

        // Задание условий A и B
        Query condA = MatchQuery.of(m -> m.field("title")
                        .query("Европы"))
                ._toQuery();

        Query condB = MatchQuery.of(m -> m.field("date")
                        .query("Россия"))
                ._toQuery();

        // Запрос A or B
        SearchResponse<NewsItem> response = client
                .search(s -> s.query(q -> q.bool(b -> b.should(condA).should(condB))), NewsItem.class);

        return response;
    }

    private MgetResponse<NewsItem> mgetQuery() throws IOException {
        MgetResponse<NewsItem> response = client.mget(mgq -> mgq
                        .index(indexName)
                        .docs(d -> d
                                .id("nZQ7F5ABbNG45bL-9f0A")
                                .id("o5Q7F5ABbNG45bL-_v2U")
                                .id("nJQ7F5ABbNG45bL-8_0P")),
                NewsItem.class
        );

        return response;
    }

    private SearchResponse<NewsItem> scriptQuery () throws IOException {
        SearchResponse<NewsItem> response = client.search(s -> s
                        .index(indexName)
                        .query(q0 -> q0
                                .scriptScore(ss -> ss
                                        .query(q -> q
                                                .matchAll(ma -> ma))
                                        .script(scr -> scr
                                                .inline(in -> in
                                                        .lang("painless")
                                                        .source("1"))))),
                NewsItem.class
        );

        return response;
    }


    // Вывод запросов к БД
    public void printNote(SearchResponse<NewsItem> response) {
        List<Hit<NewsItem>> hits = response.hits().hits();

        if (hits.isEmpty()) {
            // TODO: Добавить логгирование
            return;
        }

        for (Hit<NewsItem> hit: hits) {
            NewsItem note = hit.source();
            assert note != null;
            System.out.println(note);
        }
    }

    public void printMultiGet(MgetResponse<NewsItem> response) {
        for (MultiGetResponseItem<NewsItem> doc : response.docs()) {
            System.out.println(doc.result().source());
        }
    }
}

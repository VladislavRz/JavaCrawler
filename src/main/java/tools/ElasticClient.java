package tools;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
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
                            .properties("title", p -> p.text(t -> t.fielddata(true)))
                            .properties("date", p -> p.date(d -> d.format("yyyy-MM-dd").docValues(true)))
                            .properties("body", p -> p.text(t -> t.fielddata(true)))
                            .properties("url", p -> p.text(t -> t.fielddata(true)))
                            .properties("hash", p -> p.text(t -> t.fielddata(true)))
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

    public void buildAggregations() {
        SearchResponse<NewsItem> aggregation;
        try {
            System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+- Date Aggregation -+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
            aggregation = dateAggregation();
            System.out.println(aggregation.aggregations());

            System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+-+-+- Term Aggregation -+-+-+-+-+-+-+-+-+-+-+-+-+-+\n\n");
            aggregation = termAggregation();
            System.out.println(aggregation.aggregations());

            System.out.println("\n\n+-+-+-+-+-+-+-+-+-+-+-+- Day of week Aggregation -+-+-+-+-+-+-+-+-+-+-+-+\n\n");
            aggregation = dayOfWeekAggregation();
            System.out.println(aggregation.aggregations());


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
                        .query("2024-05-31"))
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

        Query condB = MatchQuery.of(m -> m.field("body")
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
                                .id("E6ZaKJABAnWIVb4_2oEA")
                                .id("B6ZaKJABAnWIVb4_xoGW")
                                .id("DaZaKJABAnWIVb4_0IFv")),
                NewsItem.class
        );

        return response;
    }

    private SearchResponse<NewsItem> scriptQuery () throws IOException {
        SearchResponse<NewsItem> response = client
                .search(s -> s
                        .query(q -> q
                                .bool(b -> b
                                        .filter(f -> f
                                                .script(sc0 -> sc0
                                                        .script(sc1 -> sc1
                                                                .inline(i -> i
                                                                        .lang("expression")
                                                                        .source("doc['date'].date.dayOfWeek == 1"))))))),
                        NewsItem.class);
        return response;
    }

    // Агрегации
    private SearchResponse<NewsItem> dateAggregation() throws IOException {
        SearchResponse<NewsItem> aggregation = client.search(s -> s
                        .index(indexName)
                        .aggregations("ArticlesPerDay", a -> a
                                .dateHistogram(dh -> dh
                                        .field("date")
                                        .calendarInterval(CalendarInterval.Day))
                        ),
                NewsItem.class
        );

        return aggregation;
    }

    private SearchResponse<NewsItem> termAggregation() throws IOException {
        SearchResponse<NewsItem> aggregation = client.search(s -> s
                        .index(indexName)
                        .aggregations("TermsInBody", a -> a
                                .terms(t -> t
                                        .field("title"))
                        ),
                NewsItem.class
        );

        return aggregation;
    }

    private SearchResponse<NewsItem> dayOfWeekAggregation() throws IOException {
        SearchResponse<NewsItem> aggregation = client.search(s -> s
                        .index(indexName)
                        .aggregations("DayOfWeek", a -> a
                                .histogram(h -> h
                                        .script(sc -> sc
                                                .inline(i -> i
                                                        .lang("expression")
                                                        .source("doc['date'].date.dayOfWeek")))
                                        .interval(1.0))
                        ),
                NewsItem.class
        );

        return aggregation;
    }

    // Вывод запросов к БД
    private void printNote(SearchResponse<NewsItem> response) {
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

    private void printMultiGet(MgetResponse<NewsItem> response) {
        for (MultiGetResponseItem<NewsItem> doc : response.docs()) {
            System.out.println(doc.result().source());
        }
    }
}

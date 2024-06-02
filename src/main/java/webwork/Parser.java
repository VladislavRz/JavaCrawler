package webwork;

import items.NewsItem;
import items.UrlItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
    private static String getTitle(Document doc) {
        return doc.select("h1").first().text();
    }

    private static String getBody(Document doc) {
        String text = "";
        Element body;
        Elements nodes;
        body = doc.select("div.page-body").first();
        nodes = body.select("p");
        for(Element node : nodes) {
            text = text + node.text() + "\n";
        }

        return text;
    }

    private static String getDate(Document doc) {
        return  doc.select("div.absolute").last()
                   .select("div.absolute").last().text();
    }

    public static NewsItem parse(UrlItem url, Document doc) {
        NewsItem item = new NewsItem();
        String body;
        String title;
        String date;

        // Получение заголовка
        title = getTitle(doc);
        body = getBody(doc);
        date = getDate(doc);

        // Инициализация полей новости
        item.setUrl(url.getUrl());
        item.setBody(body);
        item.setTitle(title);
        item.setDate(date);
        item.setHash(url.getHash());

        return item;
    }
}

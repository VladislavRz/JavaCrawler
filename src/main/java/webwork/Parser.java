package webwork;

import items.NewsItem;
import items.UrlItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Parser {
    private static final String DOMAIN = "https://histrf.ru";

    private static String getTitle(Element el) {
        return el.select("h2").first().text();
    }

    private static String getUrl(Element el) {
        return DOMAIN + el.select("a").first().attr("href");
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
        String date = doc.select("div.absolute").last()
                .select("div.absolute").last().text();

        String[] split_str = date.split("/");
        date = split_str[2] + "-";

        if (split_str[0].length() == 1) {
            date += "0" + split_str[0] + "-";
        } else {
            date += split_str[0] + "-";
        }

        if (split_str[1].length() == 1) {
            date += "0" + split_str[1];
        } else {
            date += split_str[1];
        }

        return date;
    }

    public static String calcHash(String str) throws NoSuchAlgorithmException {
        String hash;
        byte[] urlBytes = (str).getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashedBytes = digest.digest(urlBytes);
        hash = String.format("%032X", new BigInteger(1, hashedBytes));

        return hash;
    }

    public static Elements parseMain(Document doc) {
        Elements articles = doc.select("div.flex.flex-wrap")
                .last()
                .select("div.w-full")
                .not(".relative")
                .not(".aspect-wrap")
                .not(".flex")
                .not("overflow-hidden");

        return articles;
    }

    public static UrlItem parseNote(Element el) throws NoSuchAlgorithmException {
        UrlItem item = new UrlItem();
        String title;
        String url;
        String hash;

        // Получение данных
        title = getTitle(el);
        url = getUrl(el);
        hash = calcHash(url + ":" + title);

        // Инициализация полей
        item.setUrl(url);
        item.setTitle(title);
        item.setHash(hash);

        return item;
    }
    public static NewsItem parseNews(UrlItem url, Document doc) {
        NewsItem item = new NewsItem();
        String body;
        String date;

        // Получение данных
        body = getBody(doc);
        date = getDate(doc);

        // Инициализация полей
        item.setUrl(url.getUrl());
        item.setBody(body);
        item.setTitle(url.getTitle());
        item.setDate(date);
        item.setHash(url.getHash());

        return item;
    }
}

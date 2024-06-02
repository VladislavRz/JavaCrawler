package utils;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class Requester {
    public static Document getRequest(String url) throws IOException {
        Response response = Jsoup.connect(url).execute();

        switch (response.statusCode()) {
            case 200:
                return response.parse();
            case 403:
                // TODO: Добавить логгирование
                break;
            case 404:
                //TODO: Добавить логгирование
                break;
            case 500:
                //TODO: Добавить логгирование
                break;
        }

        return null;
    }

}

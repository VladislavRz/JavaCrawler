package webwork;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class Requester {
    private static final Logger logger = LoggerFactory.getLogger(Requester.class);

    public static Document getRequest(String targetUrl) throws IOException {

        // Настройка параметров запроса
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        // Отправка запроса
        DataOutputStream wr = new DataOutputStream (conn.getOutputStream());
        wr.close();

        // Получение ответа
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        // Обработка кодов ответа
        Document result = null;

        switch (conn.getResponseCode()) {
            case 200:
                 result = Jsoup.parse(response.toString());
                 break;
            case 403:
                logger.error("GET response on URL <{}> status 403!", targetUrl);
                break;
            case 404:
                logger.error("GET response on URL <{}> status 404!", targetUrl);
                break;
            case 500:
                logger.error("GET response on URL <{}> status 500!", targetUrl);
                break;
        }

        if (conn != null) {
            conn.disconnect();
        }

        return result;
    }

}

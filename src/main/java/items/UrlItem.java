package items;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UrlItem {
    private String hash;
    private String title;
    private String url;

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    // Getters
    public String getUrl() {
        return url;
    }
    public String getTitle() {
        return title;
    }
    public String getHash() {
        return hash;
    }

    // Вычисление хэша
    public static String calcHash(String str) throws NoSuchAlgorithmException {
        String hash;
        byte[] urlBytes = (str).getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashedBytes = digest.digest(urlBytes);
        hash = String.format("%032X", new BigInteger(1, hashedBytes));

        return hash;
    }

    @Override
    public String toString() {
        return "UrlItem{" +
                "url='" + url + "'" +
                ", title='" + title + "'" +
                ", hash='" + hash + "'" +
                "}";
    }
}

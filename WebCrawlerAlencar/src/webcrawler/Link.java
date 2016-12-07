package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:48:36
 */
public class Link implements Comparable<Link> {

    private final String stringURL;
    private final URL url;
    private boolean online;

    public Link(String url) throws MalformedURLException {
        this.stringURL = url;
        this.url = new URL(url);
    }

    @Override
    public int compareTo(Link o) {
        return this.stringURL.compareToIgnoreCase(o.getStringURL());
    }

    public String getStringURL() {
        return stringURL;
    }

    public URL getUrl() {
        return url;
    }

}

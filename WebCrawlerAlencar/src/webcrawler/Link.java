package webcrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Link other = (Link) obj;
        return Objects.equals(this.stringURL, other.stringURL);
    }

    public String getStringURL() {
        return stringURL;
    }

    public URL getUrl() {
        return url;
    }

}

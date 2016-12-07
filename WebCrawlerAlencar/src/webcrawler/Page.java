package webcrawler;

import webcrawler.pattern.Pattern;
import webcrawler.util.HtmlUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import webcrawler.pattern.HtmlPattern;
import webcrawler.tree.BTree;
import java.net.MalformedURLException;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:45:53
 */
public class Page implements Comparable<Page> {

    private static final HtmlPattern LINK_PATTERN = new HtmlPattern(".*", "<a", "</a>");
    private static final Pattern LINK_PROBLEMATICO = new Pattern(".*.pdf");
    private final int nivel;
    private final Link link;
    private final Page pageOrigem;
    private BTree<Page> filhos;
    private String html;
    private boolean downloaded;
    private boolean problem;

    public Page(int nivel, Link link, Page pageOrigem) {
        this.nivel = nivel;
        this.link = link;
        this.pageOrigem = pageOrigem;
    }

    public void downloadPage() {
        InputStream is;
        BufferedReader br;
        String line;

        try {
            is = link.getUrl().openStream();
            br = new BufferedReader(new InputStreamReader(is));
            html = "";
            while ((line = br.readLine()) != null) {
                html += (StringEscapeUtils.unescapeHtml4(line));
            }

            downloaded = true;
            problem = false;
        } catch (IOException ex) {
            System.err.println("Problem to download page: " + ex.getMessage());
            html = null;
            problem = true;
        }
    }

    public List<String> getOnlyText(List<HtmlPattern> htmlPattern) {
        if (htmlPattern.size() == 1) {
            return getOnlyText(htmlPattern.get(0));
        }
        String htmlTemp = html;
        for (int i = 0; i < htmlPattern.size() - 1; i++) {
            List<String> tempList = getOnlyText(htmlPattern.get(i), htmlTemp, false);
            htmlTemp = "";
            for (String string : tempList) {
                htmlTemp += string;
            }
        }
        return getOnlyText(htmlPattern.get(htmlPattern.size() - 1), htmlTemp, true);
    }

    public List<String> getOnlyText(HtmlPattern htmlPattern) {
        return getOnlyText(htmlPattern, html, true);
    }

    private List<String> getOnlyText(HtmlPattern htmlPattern, String html, boolean removeTagsHTML) {
        if (!downloaded) {
            return new ArrayList<>();
        }
        List<String> paragrafos = new ArrayList<>();

        int start;
        int end;
        int sizeTagBegin = htmlPattern.getTagBegin().length();
        int sizeTagEnd = htmlPattern.getTagEnd().length();
        String temp;
        while (html.length() > (sizeTagBegin + sizeTagEnd)) {
            start = html.indexOf(htmlPattern.getTagBegin());
            end = html.indexOf(htmlPattern.getTagEnd());
            if (start == -1 || end == -1) {
                break;
            }
            if (end > start) {
                temp = html.substring(start + sizeTagBegin, end);
                if (removeTagsHTML) {
                    temp = HtmlUtils.removeTags(temp);
                }
                if (htmlPattern.matchPattern(temp)) {
                    paragrafos.add(temp);
                }
            }
            html = html.substring(end + sizeTagEnd);
        }
        return paragrafos;
    }

    public void criarPaginasFilhas(Pattern urlPattern) {
        criarPaginasFilhas(LINK_PATTERN, urlPattern);
    }

    private void criarPaginasFilhas(HtmlPattern htmlPattern, Pattern urlPattern) {
        if (!downloaded) {
            downloadPage();
        }
        BTree<Page> childPages = new BTree<>();

        List<String> linksPreProcessed = getOnlyText(htmlPattern);

        List<Link> links = createLinks(linksPreProcessed, urlPattern);

        for (Link childLink : links) {
            childPages.insert(new Page(this.nivel + 1, childLink, this));
        }

        //se começar com '/', então adicinar o link desta página na frente.        
        filhos = childPages;
    }

    @Override
    public int compareTo(Page o) {
        return this.link.compareTo(o.getLink());
    }

    private List<Link> createLinks(List<String> linksPreProcessed, Pattern urlPattern) {
        List<Link> linksOK = new ArrayList<>();
        for (String childLink : linksPreProcessed) {
            Link tempLink = formatLink(childLink, urlPattern);
            if (tempLink != null) {
                linksOK.add(tempLink);
            }
        }
        return linksOK;
    }

    private Link formatLink(String childLink, Pattern urlPattern) {
        if (!childLink.contains("href")) {
            return null;
        }
        String locateURL = "href=";
        int local = childLink.indexOf(locateURL);
        int startURL = local + locateURL.length() + 1;
        int end = startURL + childLink.substring(startURL).indexOf("\"");
        try {
            String url = childLink.substring(startURL, end);
            if (url.startsWith("/")) {
                if (pageOrigem == null) {
                    url = this.link.getStringURL() + url;
                } else {
                    url = pageOrigem.getLink().getStringURL() + url;
                }
            }

            if (!urlPattern.matchPattern(url)) {
                return null;
            }
            if (LINK_PROBLEMATICO.matchPattern(url)) {
                return null;
            }
            Link tempLink;
            try {
                tempLink = new Link(url);
            } catch (MalformedURLException ex) {
                return null;
            }
            return tempLink;

        } catch (Exception ex) {
            System.err.println("Problema ao criar a URL: " + ex.getMessage() + "\n -> " + childLink);
            return null;
        }
    }

    public void cleanPage() {
        html = null;
    }

    public int getNivel() {
        return nivel;
    }

    public Link getLink() {
        return link;
    }

    public Page getPageOrigem() {
        return pageOrigem;
    }

    public BTree<Page> getFilhos() {
        return filhos;
    }

    public String getHtml() {
        return html;
    }

    public boolean isProblem() {
        return problem;
    }

}

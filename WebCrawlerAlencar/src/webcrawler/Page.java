package webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webcrawler.pattern.HtmlPattern;
import webcrawler.pattern.Pattern;
import webcrawler.tree.BTree;
import webcrawler.util.HtmlUtils;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:45:53
 */
public class Page implements Comparable<Page> {

    private final int level;
    private final Link link;
    private final Page pageOrigin;
    private BTree<Page> childPages;
    private Document html;
    private boolean downloaded;
    private boolean problem;
    private final boolean showStackTraceOnErrors;
    private List<String> stringsToRemove;

    public Page(int nivel, Link link, Page pageOrigem, boolean showStackTraceOnErrors, List<String> stringsToRemove) {
        Character nbsp = 160;/*html "&nbsp;" is the same as char code 160*/
        this.stringsToRemove = stringsToRemove != null ? stringsToRemove : Arrays.asList(nbsp.toString());
        this.level = nivel;
        this.link = link;
        this.pageOrigin = pageOrigem;
        this.showStackTraceOnErrors = showStackTraceOnErrors;
    }

    public Page(int nivel, Link link, Page pageOrigem) {
        this(nivel, link, pageOrigem, false, null);
    }

    public Page(int nivel, Link link, Page pageOrigin, boolean showStackTraceOnErrors) {
        this(nivel, link, pageOrigin, showStackTraceOnErrors, null);
    }

    public void downloadPage() {
        try {
            html = Jsoup.connect(link.getStringURL()).get();
            downloaded = true;
            problem = false;
        } catch (IOException ex) {
            downloaded = false;
            problem = true;
            if (showStackTraceOnErrors) {
                ex.printStackTrace();
            }
        }

    }

    private Elements applyPatternFilter(Elements all, HtmlPattern pattern) {
        all = all.select(pattern.getJsoupSelectorFilter());
        return pattern.matches(HtmlUtils.removeTags(all.toString())) ? all : null;
    }

    private void addTextToList(Elements all, List<String> texts) {
        String text;
        for (Element element : all) {
            text = HtmlUtils.removeTags(element.ownText());
            for (String stringToRemove : stringsToRemove) {
                text = text.replace(stringToRemove, "");
            }
            if (!text.isEmpty()) {
                texts.add(text);
            }
        }
    }

    public List<String> getContentToSave(List<HtmlPattern> htmlPattern) {
        List<String> texts = new ArrayList<>();
        Elements all = html.select("*");
        for (HtmlPattern pattern : htmlPattern) {
            all = applyPatternFilter(all, pattern);
            if (all == null) {
                return texts;
            }
        }
        addTextToList(all, texts);
        return texts;
    }

    public void createChildPages(Pattern urlPattern) {
        childPages = new BTree<>();
        String stringLink;
        Elements listLinks = html.select("a[href]");
        for (Element tempLink : listLinks) {
            try {
                stringLink = tempLink.attr("abs:href");
                if (stringLink.endsWith("/")) {
                    stringLink = stringLink.substring(0, stringLink.length() - 1);
                }
                if (urlPattern.matches(stringLink)) {
                    Link auxLink = new Link(stringLink);
                    Page auxPage = new Page((this.level + 1), auxLink, this, showStackTraceOnErrors);
                    if (!childPages.contains(auxPage) && !auxPage.equals(this)) {
                        childPages.insert(auxPage);
                    }
                }
            } catch (MalformedURLException ex) {
                if (showStackTraceOnErrors) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public int compareTo(Page o) {
        return this.link.compareTo(o.getLink());
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
        final Page other = (Page) obj;
        return Objects.equals(this.link, other.link);
    }

    public void cleanPage() {
        html = null;
    }

    public void cleanAll() {
        html = null;
        childPages = null;
        stringsToRemove = null;
    }

    public int getLevel() {
        return level;
    }

    public Link getLink() {
        return link;
    }

    public Page getPageOrigin() {
        return pageOrigin;
    }

    public BTree<Page> getChildPages() {
        return childPages;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public boolean isProblem() {
        return problem;
    }

}

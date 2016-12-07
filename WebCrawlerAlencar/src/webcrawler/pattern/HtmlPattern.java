package webcrawler.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 05/11/2016 - 00:42:48
 */
public class HtmlPattern extends Pattern {

    private final String tagBegin;
    private final String tagEnd;

    public HtmlPattern(String pattern, String tagBegin, String tagEnd) {
        super(pattern);
        this.tagBegin = tagBegin;
        this.tagEnd = tagEnd;
    }

    public String getTagBegin() {
        return tagBegin;
    }

    public String getTagEnd() {
        return tagEnd;
    }

}

package webcrawler.pattern;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 20:54:15
 */
public class Pattern {

    private final String pattern;

    public Pattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean matchPattern(String someString) {
        return someString.matches(pattern);
    }

}

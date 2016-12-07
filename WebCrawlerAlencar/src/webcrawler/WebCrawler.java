package webcrawler;

import webcrawler.fileHandler.FileHandler;
import webcrawler.pattern.Pattern;
import webcrawler.pattern.HtmlPattern;
import webcrawler.tree.BTree;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/*
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 21:00:35
 */
public class WebCrawler {

    private final FileHandler fileHandler;

    private final Pattern urlPattern;
    private final List<HtmlPattern> htmlPattern;
//    private final String tag
    private final int MAX_NIVEL;
    private int linksVerificados;

    private static BTree<Page> allPages;
    private static Page rootPage;

    public WebCrawler(String startURL, Pattern urlPattern, List<HtmlPattern> htmlPattern, FileHandler file, int maxNivel) throws MalformedURLException {
        this.linksVerificados = 1;
        allPages = new BTree<>();
        rootPage = new Page(1, new Link(startURL), null);
        allPages.insert(rootPage);
        this.urlPattern = urlPattern;
        this.htmlPattern = htmlPattern;
        this.fileHandler = file;
        this.MAX_NIVEL = maxNivel;
    }

    public void startCrawler() throws IOException {
        System.out.println("WebCrawler iniciado!");
        fileHandler.openFile();

        startCrawler(rootPage);

        fileHandler.closeFile();
        System.out.println("\n\nWebCrawler finalizado!");
        System.out.println("Status finais: ");
        System.out.println("-> Nível máximo: " + MAX_NIVEL);
        System.out.println("-> Links encontrados: " + linksVerificados);
        System.out.println("-> Páginas baixadas: " + allPages.getElementsNumber());
        System.out.println("\n");
    }

    private void startCrawler(Page page) throws IOException {
        if (page == null) {
            return;
        }
        System.out.println("-Page: " + page.getLink().getStringURL());
        if (page.getLink() == null) {
            return;
        }

        //faz o download da página
        page.downloadPage();

        if (page.isProblem()) {
            return;
        }

        //salva o seu conteúdo em disco        
        fileHandler.save(page.getOnlyText(htmlPattern));

        if (page.getNivel() > MAX_NIVEL) {// se a página tem um nível maior que o nível máximo, não precisa criar as páginas filhas.
            page.cleanPage();
            return;
        }

        //cria as páginas baseadas nos links encontrados na página atual
        page.criarPaginasFilhas(urlPattern);

        List<Page> childPages = page.getFilhos().getAllAsList();

        page.cleanPage();

        for (Page childPage : childPages) {
            linksVerificados++;
            if (!allPages.isMember(childPage)) {
                allPages.insert(childPage);
                startCrawler(childPage);
            }
        }

        fileHandler.flush();
    }

}

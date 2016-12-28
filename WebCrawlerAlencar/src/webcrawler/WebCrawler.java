package webcrawler;

import webcrawler.fileHandler.FileHandler;
import webcrawler.pattern.Pattern;
import webcrawler.pattern.HtmlPattern;
import webcrawler.tree.BTree;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 21:00:35
 */
public class WebCrawler {

    private final FileHandler fileHandler;
    private final Queue<Page> pageQeueue;

    private final Pattern urlPattern;
    private final List<HtmlPattern> htmlPattern;
    private final int MAX_NIVEL;
    private int linksVerificados;

    private final BTree<Page> allPages;
    private final Page rootPage;

    public WebCrawler(String startURL, Pattern urlPattern, List<HtmlPattern> htmlPattern, FileHandler file, int maxNivel, boolean showStackTraceOnErrors) throws MalformedURLException {
        this.linksVerificados = 1;
        this.urlPattern = urlPattern;
        this.htmlPattern = htmlPattern;
        this.fileHandler = file;
        this.MAX_NIVEL = maxNivel;
        this.pageQeueue = new LinkedList<>();
        this.allPages = new BTree<>();
        this.rootPage = new Page(1, new Link(startURL), null, showStackTraceOnErrors);

        //init
        pageQeueue.add(rootPage);
        allPages.insert(rootPage);

    }

    public void startCrawler() throws IOException {
        System.out.println("WebCrawler iniciado!");
        fileHandler.openFile();

        while (!pageQeueue.isEmpty()) {
            startCrawler(pageQeueue.poll());
        }

        fileHandler.closeFile();
        System.out.println("\n\nWebCrawler finalizado!");
        System.out.println("Status finais: ");
        System.out.println("-> Nível máximo: " + MAX_NIVEL);
        System.out.println("-> Links encontrados: " + linksVerificados);
        System.out.println("-> Páginas baixadas: " + allPages.getElementsNumber());
        System.out.println("\n");
    }

    private void startCrawler(Page page) throws IOException {
        if (page == null || page.getLink() == null) {
            return;
        }

        //1 - faz o download da página
        page.downloadPage();

        //2 - verifica se houve problema ao baixar
        String track = buildTrack(page);
        if (page.isProblem()) {
            System.err.println("Page With Problem: (" + track + ") - " + page.getLink().getStringURL());
            return;
        } else {
            System.out.println("->Page Downloaded: (" + track + ") - " + page.getLink().getStringURL());
        }

        //3 - salva o seu conteúdo em disco        
        fileHandler.save(page.getContentToSave(htmlPattern));

        //4 - verificar nível da página
        if (page.getLevel() > MAX_NIVEL) {
            page.cleanPage();
            return;
        }

        //5 - Criar páginas filhas
        page.createChildPages(urlPattern);

        //6 - limpa dados da página
        page.cleanPage();

        //7 - percorre as páginas filhas e startCrawler() a partir delas
        page.getChildPages().getAllAsList().forEach(childPage -> {
            linksVerificados++;
            if (!allPages.contains(childPage)) {
                allPages.insert(childPage);
                pageQeueue.add(childPage);
            }
        });

        //8 - throw to HD buffer data
        fileHandler.flush();
    }

    private String buildTrack(Page page) {
//	Código não necessário para criar a string de retorno deste método. 
//	//TODO: verificar necessidade da existência deste código comentado.
//        final StringBuilder sb = new StringBuilder();
//        List<Integer> track = new ArrayList<>();
//        Page temp = page;
//        while (temp.getPageOrigin() != null) {
//            track.add(temp.getPageOrigin().getLevel());
//            temp = temp.getPageOrigin();
//        }
//        for (int i = track.size() - 1; i >= 0; i--) {
//            sb.append(track.get(i)).append("->");
//        }
//        return track.size() > 0 ? sb.append(page.getLevel()).toString() : sb.append("->").append(page.getLevel()).toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < page.getLevel(); i++) {
            sb.append(i).append("->");
        }
        return sb.append(page.getLevel()).toString();
    }

    public int getLinksVerificados() {
        return linksVerificados;
    }

}

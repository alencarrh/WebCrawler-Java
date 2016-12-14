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
	private final int MAX_NIVEL;
	private int linksVerificados;

	private static BTree<Page> allPages;
	private static Page rootPage;

	public WebCrawler(String startURL, Pattern urlPattern, List<HtmlPattern> htmlPattern, FileHandler file, int maxNivel, boolean showStackTraceOnErrors) throws MalformedURLException {
		this.linksVerificados = 1;
		allPages = new BTree<>();
		rootPage = new Page(1, new Link(startURL), null, showStackTraceOnErrors);
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
		if (page == null || page.getLink() == null) {
			return;
		}

		//1 - faz o download da página
		page.downloadPage();

		//2 - verifica se houve problema ao baixar
		if (page.isProblem()) {
			System.err.println("Problem to download page: " + page.getLink().getStringURL());
			return;
		} else {
			System.out.println("-> Page downloaded: " + page.getLink().getStringURL());
		}

		//3 - salva o seu conteúdo em disco        
		fileHandler.save(page.getContentToSave(htmlPattern));

		//4 - verificar nível da página
		if (page.getNivel() > MAX_NIVEL) {
			page.cleanPage();
			return;
		}

		//5 - Criar páginas filhas
		page.createChildPages(urlPattern);

		//6 - limpa dados da página
		page.cleanPage();

		//7 - percorre as páginas filhas e startCrawler() a partir delas
		for (Page childPage : page.getChildPages().getAllAsList()) {
			linksVerificados++;
			if (!allPages.contains(childPage)) {
				allPages.insert(childPage);
				startCrawler(childPage);
			}
		}

		//8 - throw to HD buffer data
		fileHandler.flush();
	}

}

package webcrawler.fileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Alencar Rodrigo Hentges <alencarhentges@gmail.com>
 * @date 04/11/2016 - 21:02:57
 */
public class FileHandler {

    private final File file;
    private final boolean appendMode;
    private FileWriter fw;
    private BufferedWriter bw;

    public FileHandler(File file, boolean appendMode) {
        this.file = file;
        this.appendMode = appendMode;
    }

    public FileHandler(String src, String fileName, boolean appendMode) {
        new File(src).mkdirs();
        this.file = new File(src + fileName);
        this.appendMode = appendMode;
    }

    public void save(String text) throws IOException {
        bw.write(text + "\n");
    }

    public void save(List<String> text) throws IOException {
        for (String t : text) {
            save(t);
        }
    }

    public void openFile() throws IOException {
        fw = new FileWriter(file, appendMode);
        bw = new BufferedWriter(fw);
    }

    public void openFile(boolean append) throws IOException {
        fw = new FileWriter(file, append);
        bw = new BufferedWriter(fw);
    }

    public void flush() throws IOException {
        bw.flush();
//        fw.flush();
    }

    public void closeFile() throws IOException {
        bw.close();
        fw.close();
    }
}

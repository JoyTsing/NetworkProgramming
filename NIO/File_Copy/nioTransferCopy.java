import java.io.*;
import java.nio.channels.FileChannel;

public class nioTransferCopy implements FileCopyRunner {

    private static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void copyFile(File source, File target) {
        FileChannel fin = null;
        FileChannel fout = null;
        try {
            fin = new FileInputStream(source).getChannel();
            fout = new FileOutputStream(target).getChannel();
            long transferred = 0L;
            long size=fin.size();
            while (transferred != fin.size()) {
                transferred += fin.transferTo(0,size, fout);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fin);
            close(fout);
        }
    }
}

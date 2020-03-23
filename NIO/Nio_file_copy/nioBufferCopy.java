import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class nioBufferCopy implements FileCopyRunner {
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

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while ((fin.read(buffer)) != -1) {
                buffer.flip();
                while (buffer.hasRemaining())
                    fout.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(fin);
            close(fout);
        }
    }
}

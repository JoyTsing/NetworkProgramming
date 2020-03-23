import java.io.*;

interface FileCopyRunner {
    void copyFile(File source, File target);
}

public class FileCopy {

    private static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public static void main(String[] args) {

        FileCopyRunner noBufferStreamCopy = (source, target) -> {
            InputStream fin = null;
            OutputStream fout = null;
            try {
                fin = new FileInputStream(source);
                fout = new FileOutputStream(target);
                int result;
                while ((result = fin.read()) != -1) {
                    fout.write(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(fin);
                close(fout);
            }
        };

        FileCopyRunner bufferStreamCopy = (source, target) -> {
            InputStream fin=null;
            OutputStream fout=null;
            try {
                fin=new BufferedInputStream(new FileInputStream(source));
                fout=new BufferedOutputStream(new FileOutputStream(target));

                byte[] buffer=new byte[1024];
                int result;
                while((result=fin.read(buffer))!=-1){
                    fout.write(buffer,0,result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(fin);
                close(fout);
            }
        };

        FileCopyRunner nioBufferCopy = (source, target) -> {

        };

        FileCopyRunner nioTransferCopy = (source, target) -> {

        };
    }


}

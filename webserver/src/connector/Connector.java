package connector;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class Connector implements Runnable {
    private static final int DEFAULT_PORT = 8888;

    private ServerSocket serverSocket;
    private int port;

    public Connector() {
        this(DEFAULT_PORT);
    }

    public Connector(int port) {
        this.port = port;
    }

    public void start() {

    }


    @Override
    public void run() {
        try {
            serverSocket=new ServerSocket(port);
            System.out.println("启动服务器，监听端口:" +port);

            /*
            
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * 保存在线的用户
 */

public class ChatServer {
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER);
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public ChatServer() {
        this(DEFAULT_PORT);
    }

    /**
     * 主要逻辑
     */
    public void start() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);//取消阻塞状态
            server.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();
            //accept事件 连接
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务端，监听端口:" + port + "...");
            /*
            This method performs a blocking selection operation. It returns only after at least one channel is selected
             */
            while (true) {
                //用来阻塞直到有事件发生
                selector.select();
                //发生的事件有关信息包装在SelectionKey中
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    handles(key);
                }
                //每一次处理完后清空不然append到事件中
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(selector);
        }
    }

    private void handles(SelectionKey key) throws IOException {
        //ACCEPT 与客户端建立连接
        if (key.isAcceptable()) {
            //selectionKey.channel()方法返回的  channel是ServerSocketChannel还是SocketChannel是由前边注册这个key时是注册channel确定的
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            //建立完连接 client就会有发送消息的可能 需要注册READ事件
            client.register(selector, SelectionKey.OP_READ);
            System.out.println(getClientName(client) + "]已连接");
        }
        //READ 客户端发送消息
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            String fwdMsg = receive(client);
            if (fwdMsg.isEmpty()) {
                //客户端关闭或异常 取消监听即通道
                key.cancel();
                //通知多线程下可能阻塞的selector
                selector.wakeup();
            } else {
                forwardMessage(client, fwdMsg);
                if (readyToQuit(fwdMsg)) {
                    key.cancel();
                    selector.wakeup();
                    System.out.println(getClientName(client) + "已断开");
                }
            }
        }

    }

    private String getClientName(SocketChannel client) {
        return "客户端[" + client.socket().getPort() + "]";
    }

    private void forwardMessage(SocketChannel origin, String fwdMsg) throws IOException {
        //先找所有在线对象
        for (SelectionKey key : selector.keys()) {
            Channel connectedClient = key.channel();
            if (key.channel() instanceof ServerSocketChannel)
                continue;
            if (key.isValid() && !origin.equals(key.channel())) {
                wBuffer.clear();
                wBuffer.put(charset.encode(getClientName(origin) + ":" + fwdMsg));
                wBuffer.flip();
                while (wBuffer.hasRemaining()) {
                    ((SocketChannel)connectedClient).write(wBuffer);
                }
            }
        }
    }

    private String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        //返回0时是因为缓冲区已满
        while (client.read(rBuffer) > 0) ;
        rBuffer.flip();
        return String.valueOf(charset.decode(rBuffer));
    }

    /**
     * 每一个客户端与服务端建立连接时 会返回一个socket对象
     * 注意线程安全
     */


    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(7777);
        server.start();
    }
}

package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 保存在线的用户
 */

public class ChatServer {
    private final int DEFAULT_PORT = 8888;
    private final String QUIT = "quit";

    private ServerSocket serverSocket = null;
    //从服务器端获取消息 标记每一个客户可以用端口号标记 同时对应着消息发送的write
    private Map<Integer, Writer> connectedClients;

    public ChatServer() {
        connectedClients = new HashMap<>();
    }

    /**
     * 每一个客户端与服务端建立连接时 会返回一个socket对象
     * 注意线程安全
     */
    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int key = socket.getPort();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

            connectedClients.put(key, writer);
            System.out.println("客户端[" + key + "]连接到服务器");
        }

    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int key = socket.getPort();
            if (connectedClients.containsKey(key)) {
                connectedClients.get(key).close();
            }
            connectedClients.remove(key);
            System.out.println("客户端[" + key + "]断开连接");
        }
    }

    /**
     * 转发消息给除发送者外的其他用户
     */
    public synchronized void forwardMessage(Socket socket, String fwdMsg) throws IOException {
        for (Integer id : connectedClients.keySet()) {
            if (!id.equals(socket.getPort())) {
                Writer writer = connectedClients.get(id);
                writer.write(fwdMsg);
                writer.flush();
            }
        }
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public synchronized void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println("关闭serverSocket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 主要逻辑
     */
    public void start() {
        try {
            //绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT + "...");

            while (true) {
                //不停等待是否有新的客户端加入，并为每一个客户端分配一个线程
                Socket socket = serverSocket.accept();
                //创建Handler处理线程
                new Thread(new ChatHandler(this, socket)).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}

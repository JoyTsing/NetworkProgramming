package client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final int DEFAULT_PORT = 8888;
    private final String QUIT = "quit";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void start() {
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_PORT);
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            //处理用户输入
            new Thread(new UserInputHandler(this)).start();

            //读取服务器转发信息
            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭socket");
            close();
        }

    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}

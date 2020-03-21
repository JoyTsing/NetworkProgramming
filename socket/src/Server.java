import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description：TODO
 */
public class Server {

    public static void main(String[] args) {

        final String QUIT = "quit";
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;

        try {
            //绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口" + DEFAULT_PORT);

            while (true) {
                //阻塞式接口 等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端[" + socket.getPort() + "]已连接");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );
                String msg = null;

                while ((msg = bufferedReader.readLine()) != null) {
                    // 读取客户端发送的消息


                    System.out.println("客户端[" + socket.getPort() + "]:" + msg);

                    //回复客户发送的消息
                    bufferedWriter.write("服务器" + msg + "\n");
                    bufferedWriter.flush();

                    if(QUIT.equals(msg)){
                        System.out.println("客户端["+socket.getPort()+"]已断开");
                        break;
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("关闭servers socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

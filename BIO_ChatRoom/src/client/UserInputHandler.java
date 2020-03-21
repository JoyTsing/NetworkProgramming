package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @description：处理输入
 */
public class UserInputHandler implements Runnable {
    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();
                //服务器发送信息
                chatClient.send(input);

                if (chatClient.readyToQuit(input))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

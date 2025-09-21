package net.netty.io;

import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080); // 监听端口8080
        System.out.println("Server is listening on port 8080...");

        while (true) {
            Socket clientSocket = server.accept(); // 阻塞等待客户端连接
            System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

            // 为每个客户端创建一个线程处理
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Received: " + line);
                        out.println("Echo: " + line); // 简单回显
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

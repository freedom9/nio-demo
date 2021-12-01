package com.freedom.niodemo.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author freedom
 * @date 2021/11/30 21:27
 */
public class BClient {

    public static void client() throws IOException {
        final SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        socketChannel.configureBlocking(false);

        final Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);

        new Thread(new ClientThread(selector)).start();

        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            final String msg = scanner.nextLine();
            if (msg.length() > 0) {
                socketChannel.write(StandardCharsets.UTF_8.encode("小明：" + msg));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        client();
    }
}

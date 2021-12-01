package com.freedom.niodemo.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author freedom
 * @date 2021/11/30 21:05
 */
@SpringBootTest
public class Server {

    @Test
    @DisplayName("聊天室服务端")
    public void startServer() throws IOException {
        final Selector selector = Selector.open();

        final ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(9999));
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("服务器已经启动成功了！");

        while (true) {
            if (selector.select() == 0) {
                continue;
            }

            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                final SelectionKey selectionKey = iterator.next();

                iterator.remove();

                // 如果是就绪状态
                if (selectionKey.isAcceptable()) {
                    acceptOperator(socketChannel, selector);
                }

                // 如果可读状态
                if (selectionKey.isReadable()) {
                    readOperator(selector, selectionKey);
                }
            }
        }
    }

    private void acceptOperator(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        socketChannel.write(StandardCharsets.UTF_8.encode("欢迎进入聊天室，请注意隐私安全！"));

    }

    private void readOperator(Selector selector, SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel)selectionKey.channel();

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        StringBuilder sb = new StringBuilder();
        final int length = channel.read(byteBuffer);
        if (length > 0) {
            byteBuffer.flip();
            sb.append(StandardCharsets.UTF_8.decode(byteBuffer));
            byteBuffer.clear();
        }

        if (sb.length() > 0) {
            System.out.println(sb);
            // 广播给其他客户端
            castOtherClient(selector, channel, sb);
        }
    }

    private void castOtherClient(Selector selector, SocketChannel channel, StringBuilder sb) throws IOException {
        final Set<SelectionKey> selectionKeys = selector.keys();

        for (SelectionKey selectionKey : selectionKeys) {
            final SelectableChannel selectableChannel = selectionKey.channel();
            // 排除自己
            if (selectableChannel instanceof SocketChannel && selectableChannel != channel) {
                ((SocketChannel) selectableChannel).write(StandardCharsets.UTF_8.encode(sb.toString()));
            }
        }
    }
}

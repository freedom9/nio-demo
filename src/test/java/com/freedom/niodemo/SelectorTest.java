package com.freedom.niodemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

/**
 * @author freedom
 * @date 2021/11/21 20:49
 */
@SpringBootTest
public class SelectorTest {

    @Test
    @DisplayName("服务端")
    public void server() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(9999));
        ssc.configureBlocking(false);

        final Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            final Set<SelectionKey> keys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    final SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    final SocketChannel socketChannel = (SocketChannel) key.channel();
                    final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                    while (socketChannel.read(byteBuffer) > 0) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array()));
                        byteBuffer.clear();
                    }
                }
            }
            iterator.remove();
        }
    }

    @Test
    @DisplayName("客户端")
    public void Client() throws IOException {
        final SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
        socketChannel.configureBlocking(false);

        final ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put(("hello----" + LocalDateTime.now()).getBytes());
        writeBuffer.flip();

        socketChannel.write(writeBuffer);
        writeBuffer.clear();
    }
}

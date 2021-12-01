package com.freedom.niodemo.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author freedom
 * @date 2021/11/30 21:33
 */
public class ClientThread implements Runnable {

    private Selector selector;

    public ClientThread(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                final int channel = selector.select();
                if (channel == 0) {
                    continue;
                }

                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    // 如果可读状态
                    if (selectionKey.isReadable()) {
                        readOperator(selector, selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readOperator(Selector selector, SelectionKey selectionKey) throws IOException {
        final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        StringBuilder sb = new StringBuilder();
        final int length = socketChannel.read(byteBuffer);
        if (length > 0) {
            byteBuffer.flip();
            sb.append(StandardCharsets.UTF_8.decode(byteBuffer));
            byteBuffer.clear();
        }

        socketChannel.register(selector, SelectionKey.OP_READ);

        if (sb.length() > 0) {
            System.out.println(sb);
        }
    }
}

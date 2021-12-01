package com.freedom.niodemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author freedom
 * @date 2021/11/13 16:01
 */
@SpringBootTest
public class ChannelTest {

    @Test
    @DisplayName("FileChannel读取数据")
    public void testFileChannelReadWithSuccess() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Read.txt", "rw");
        final FileChannel channel = raf.getChannel();

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = channel.read(byteBuffer);
        while (read != -1) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                System.out.println((char) byteBuffer.get());
            }
            byteBuffer.clear();
            read = channel.read(byteBuffer);
        }

        channel.close();
        raf.close();
    }

    @Test
    @DisplayName("FileChannel写数据")
    public void testFileChannelWriteWithSuccess() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Write.txt", "rw");
        final FileChannel channel = raf.getChannel();

        String content = "test NIO，" + LocalDateTime.now();
        int capacity = 1024;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);

        final int length = content.getBytes().length;
        int start = 0;
        while (start < length) {
            byteBuffer.clear();
            for (int i = start; i < start + capacity; i++) {
                if (i == length - 1) {
                    break;
                }
                byteBuffer.put(content.getBytes()[i]);
            }

            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                channel.write(byteBuffer);
            }
            start = start + capacity;
        }

        channel.close();
        raf.close();
    }

    @Test
    @DisplayName("ServerSocketChannel监控端口")
    public void testServerSocketChannelWithSuccess() throws IOException, InterruptedException {
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(3333));
        ssc.configureBlocking(Boolean.FALSE);

        while (true) {
            final SocketChannel socketChannel = ssc.accept();
            if (socketChannel == null) {
                System.out.println("null");
                TimeUnit.SECONDS.sleep(2);
            } else {
                System.out.println("address: " + socketChannel.socket().getRemoteSocketAddress());

                socketChannel.close();
                break;
            }
        }

        ssc.close();
    }

    @Test
    @DisplayName("DatagramChannel发送数据")
    void testDatagramChannelSendWithSuccess() throws IOException, InterruptedException {
        final DatagramChannel datagramChannel = DatagramChannel.open();
        final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        int i = 0;
        while (true) {
            String content = "发送包测试：" + i++;
            datagramChannel.send(ByteBuffer.wrap(content.getBytes()), address);
            System.out.println("发送成功！信息：" + content);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @Test
    @DisplayName("DatagramChannel接收数据")
    void testDatagramChannelReceive() throws IOException {
        final DatagramChannel datagramChannel = DatagramChannel.open();
        final InetSocketAddress address = new InetSocketAddress(9999);
        datagramChannel.bind(address);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            byteBuffer.clear();
            final SocketAddress socketAddress = datagramChannel.receive(byteBuffer);
            byteBuffer.flip();
            System.out.printf("信息来源IP：%s，接收的信息：%s\n", socketAddress.toString(), StandardCharsets.UTF_8.decode(byteBuffer));
        }
    }

}

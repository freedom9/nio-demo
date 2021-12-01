package com.freedom.niodemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

/**
 * @author freedom
 * @date 2021/11/27 21:09
 */
@SpringBootTest
public class AsynchronousFileChannelTest {

    @Test
    @DisplayName("测试Future读取数据")
    public void testFutureRead() throws IOException {
        final Path path = Paths.get("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Read.txt");
        final AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        int capacity = 8;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);

        StringBuilder sb = new StringBuilder();
        final long size = asynchronousFileChannel.size();
        int position = 0;
        while (size > position) {
            final Future<Integer> future = asynchronousFileChannel.read(byteBuffer, position);

            while (!future.isDone()) ;

            byteBuffer.flip();
            sb.append(new String(byteBuffer.array(), 0, byteBuffer.limit()));
            byteBuffer.clear();
            position += capacity;
        }

        System.out.println(sb);
    }

    @Test
    @DisplayName("测试Future写入数据")
    public void testFutureWrite() throws IOException {
        final Path path = Paths.get("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Write.txt");
        final AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        final byte[] bytes = ("write NIO, " + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8);
        for (byte aByte : bytes) {
            byteBuffer.put(aByte);
        }
        byteBuffer.flip();

        final Future<Integer> future = asynchronousFileChannel.write(byteBuffer, 0);

        while (!future.isDone());
    }

    @Test
    @DisplayName("测试CompletionHandler读取数据")
    public void testCompletionHandlerRead() throws IOException {
        final Path path = Paths.get("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Read.txt");
        final AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        asynchronousFileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                attachment.flip();
                System.out.println(new String(attachment.array(), 0, attachment.limit()));
                attachment.clear();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });
    }

    @Test
    @DisplayName("测试CompletionHandler写入数据")
    public void testCompletionHandlerWrite() throws IOException {
        final Path path = Paths.get("E:\\idea\\workspace\\nio-demo\\src\\test\\java\\com\\freedom\\niodemo\\file\\Write.txt");
        final AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        final byte[] bytes = ("write NIO, " + LocalDateTime.now()).getBytes(StandardCharsets.UTF_8);
        for (byte aByte : bytes) {
            byteBuffer.put(aByte);
        }
        byteBuffer.flip();

       asynchronousFileChannel.write(byteBuffer, 0, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                System.out.println("write over");
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });
    }
}

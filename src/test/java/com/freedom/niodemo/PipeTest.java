package com.freedom.niodemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @author freedom
 * @date 2021/11/27 20:40
 */
@SpringBootTest
public class PipeTest {

    @Test
    @DisplayName("测试管道")
    public void testPipe() throws IOException {
        final Pipe pipe = Pipe.open();

        // 获取sink管道道，用来传送数据
        final Pipe.SinkChannel sinkChannel = pipe.sink();
        final ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        writeBuffer.put("Hello World".getBytes());
        writeBuffer.flip();
        sinkChannel.write(writeBuffer);

        // 获取source管道，接收数据
        final Pipe.SourceChannel sourceChannel = pipe.source();
        final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        final int length = sourceChannel.read(readBuffer);
        System.out.println(new String(readBuffer.array(), 0, length));

        sourceChannel.close();
        sinkChannel.close();
    }
}

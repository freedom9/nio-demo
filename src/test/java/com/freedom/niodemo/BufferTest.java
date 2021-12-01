package com.freedom.niodemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;

/**
 * @author freedom
 * @date 2021/11/14 22:03
 */
@SpringBootTest
public class BufferTest {

    @Test
    @DisplayName("缓冲区分片")
    public void testBufferFragmentationWithSuccess() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < byteBuffer.capacity(); i++) {
            byteBuffer.put((byte) i);
        }

        // 创建子缓冲区
        byteBuffer.position(3);
        byteBuffer.limit(5);
        final ByteBuffer slice = byteBuffer.slice();
        for (int i = 0; i < slice.capacity(); i++) {
            slice.put((byte) (slice.get(i) * 10));
        }

        byteBuffer.position(0);
        byteBuffer.limit(10);
        while (byteBuffer.hasRemaining()) {
            System.out.println(byteBuffer.get());
        }
    }
}

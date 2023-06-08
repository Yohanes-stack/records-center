package com.rc.io.bytebuff;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.rc.io.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        //字符串转ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello wrold".getBytes());
        debugAll(buffer);
        //charset
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello world");
        debugAll(buffer2);
        //wrap
        ByteBuffer wrap = ByteBuffer.wrap("hello world".getBytes());
        debugAll(wrap);
        buffer.flip();
        System.out.println(StandardCharsets.UTF_8.decode(buffer));
        System.out.println(StandardCharsets.UTF_8.decode(buffer2));


    }
}

package com.rc.netty.bytebuff;

import java.nio.ByteBuffer;

import static com.rc.netty.ByteBufferUtil.debugAll;

public class TestByteBufferExam {

    public static void main(String[] args) {
        //黏包和半包问题解决
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put("helloworld \n I'm zhangsan \n Ho".getBytes());
        split(buffer);
        buffer.put("w are you \n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int length = i + 1 - buffer.position();
                ByteBuffer buffer1 = ByteBuffer.allocate(length);
                for (int i1 = 0; i1 < length; i1++) {
                    byte b = buffer.get();
                    buffer1.put(b);
                }
                debugAll(buffer1);

            }

        }
        buffer.compact();
    }
}

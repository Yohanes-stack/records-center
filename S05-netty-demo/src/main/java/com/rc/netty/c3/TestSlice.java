package com.rc.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(16);
        buf.writeBytes(new byte[]{'a','b','c','d','e','d','e'});
        //在切片的过程中，没有发生数据复制
        ByteBuf f1 = buf.slice(0, 5);
        ByteBuf f2 = buf.slice(5, 5);
        f1.retain();
//        f1.writeByte('d');

        f1.setByte(0,'P');
        buf.release();
        System.out.println(f1);

    }

}

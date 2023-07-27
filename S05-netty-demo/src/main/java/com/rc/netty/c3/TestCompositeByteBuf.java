package com.rc.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(new byte[]{1, 2, 3, 5, 5});
        ByteBuf byteBuf2 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(new byte[]{5, 6, 7, 8, 9});
//        log(byteBuf1);
//        log(byteBuf2);
        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf.addComponents(true,byteBuf1,byteBuf2);
        log(buf);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE); // Import static constant... netty的
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}

package test.rc.netty.c3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/*
Unpooled 是一个工具类，类如其名，提供了非池化的 ByteBuf 创建、组合、复制等操作
Unpooled.wrappedBuffer 方法

 */
public class e_Unpooled {

    /**
     * 零拷贝 与 池化非池化 buf无关
     */
    public void test0()
    {
        final ByteBuf buf1 = Unpooled.buffer(5);  // [--- 创建非池化 + 堆内存的 ByteBuf ---]
        buf1.writeBytes(new byte[]{1,2,3,4,5});
        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        System.out.println(buf1.getClass()); // : class io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf   非池化堆内存
        System.out.println(buf2.getClass()); // : class io.netty.buffer.PooledUnsafeDirectByteBuf     池化直接内存
        // 非池化堆内存 进行零拷贝
        final ByteBuf s = buf1.slice(0, 2);
        System.out.println(s.getClass()); //  class io.netty.buffer.UnpooledSlicedByteBuf
        log(s);
        /*
        read index:0 write index:2 capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02                                           |..              |
+--------+-------------------------------------------------+----------------+
         */
        buf1.setByte(0,15);
        log(s);
        /*
        read index:0 write index:2 capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 0f 02                                           |..              |
+--------+-------------------------------------------------+----------------+
         */

    }

    public static void main(String[] args) {

        final ByteBuf buf1 = Unpooled.buffer(5);
        buf1.writeBytes(new byte[]{1,2,3,4,5});
        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        System.out.println(buf1.getClass()); // : class io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf   非池化堆内存
        System.out.println(buf2.getClass()); // : class io.netty.buffer.PooledUnsafeDirectByteBuf     池化直接内存

        // wrappedBuffer 包装超过一个时，底层使用 CompositeByteBuf
        final ByteBuf bufAll = Unpooled.wrappedBuffer(buf1, buf2);
        log(bufAll);
        /*
        read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 06 07 08 09 0a                   |..........      |
+--------+-------------------------------------------------+----------------+
         */
        buf1.setByte(0,16); // 改变 buf1 发现 引用地址的 bufAll 数据改了
        log(bufAll);
        /*
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 10 02 03 04 05 06 07 08 09 0a                   |..........      |
+--------+-------------------------------------------------+----------------+
         */



    }

    /**
     * 设置 读写 索引
     */
    public void test1()
    {
        // 1. 初始化
        final ByteBuf buf1 = Unpooled.buffer();  // [--- 创建非池化 + 堆内存的 ByteBuf ---]
        buf1.writeBytes(initByte);
        log(buf1);

        // 2. 读索引改变，创建新buf
        final ByteBuf buf = buf1.readBytes(100);
        log(buf);

        // 3. 设置 读写索引 打印
        buf1.setIndex(0,buf1.writerIndex());
        log(buf1);
    }

    public void test2()
    {
        // 1. 初始化
        final ByteBuf buf1 = Unpooled.buffer();  // [--- 创建非池化 + 堆内存的 ByteBuf ---]
        buf1.writeBytes(initByte);

        // 资料长度
        final ByteBuf b1 = buf1.readBytes(2);
        final String s1 = ByteBufUtil.hexDump(b1);
        final int lenHex = ConvertUtil.hexStrToDec(s1);

        // 识别码
        final ByteBuf b2 = buf1.readBytes(2);
        final String pfcHex = ByteBufUtil.hexDump(b2);

        // 数据包
        final ByteBuf dataBuf = buf1.readBytes(lenHex-2);
        final String dataHex = ByteBufUtil.hexDump(dataBuf);

        // 黑盒
        final ByteBuf b4 = dataBuf.readBytes(16);
        final String uniqueId = ConvertUtil.hexString2String(ByteBufUtil.hexDump(b4));

//        // 版本号 [指定索引]
//        dataBuf.setIndex(16,dataBuf.writerIndex());
//        final String v = ConvertUtil.hexString2String(ByteBufUtil.hexDump(dataBuf.readBytes(5)));
//
//        // 类型连接
//        final String tsUtf8 = ConvertUtil.toStringHex2(ByteBufUtil.hexDump(dataBuf.readBytes(dataBuf.readableBytes()-1)));

        dataBuf.setIndex(16,dataBuf.writerIndex());
        String packetHex = ByteBufUtil.hexDump(dataBuf.readBytes(2));
        String logTypeHex = ByteBufUtil.hexDump(dataBuf.readBytes(2));
        String logLenHex = ByteBufUtil.hexDump(dataBuf.readBytes(2));

        final ByteBuf logDataByteByf = dataBuf.readBytes(dataBuf.readableBytes());
        String logDataHex =  ByteBufUtil.hexDump(logDataByteByf);

        log(logDataByteByf);
        // 解析日志 log
        int lamp = highByte2Dec(logDataByteByf.readBytes(2));
        System.out.println("lamp = " + lamp);

        int produceNumber = highByte2Dec(logDataByteByf.readBytes(2));
        System.out.println("produceNumber = " + produceNumber);

        int ok = highByte2Dec(logDataByteByf.readBytes(2));
        System.out.println("ok = " + ok);

        int ng = highByte2Dec(logDataByteByf.readBytes(2));
        System.out.println("ng = " + ng);

        int err = highByte2Dec(logDataByteByf.readBytes(2));
        System.out.println("err = " + err);


//        final float v = logDataByteByf.readFloatLE();
//        System.out.println("v = " + v);

        final float v = highByte2Float(logDataByteByf.readBytes(4));
        System.out.println("v = " + v);
        final float i = logDataByteByf.readFloatLE();
        System.out.println("i = " + i);
        final float p = logDataByteByf.readFloatLE();
        System.out.println("p = " + p);


        System.out.println("s1 = " + s1);
        System.out.println("lenHex = " + lenHex);
        System.out.println("pfcHex = " + pfcHex);
        System.out.println("dataHex = " + dataHex);
        System.out.println("uniqueId = " + uniqueId);
//        System.out.println("v = " + v);
//        System.out.println("tsUtf8 = " + tsUtf8);

        System.out.println("packetHex = " + packetHex);
        System.out.println("logTypeHex = " + logTypeHex);
        System.out.println("logLenHex = " + logLenHex);
        System.out.println("logDataHex = " + logDataHex);
        log(buf1);
        log(dataBuf);


    }
/*
buf1.setIndex(4,204);
log(buf1);
 */
    // 2字节 大端-转换  // IndexOutOfBoundsException
    public static int highByte2Dec(ByteBuf buf){
        try {
            return (((buf.getByte(1) & 0xFF) << 1) | (buf.getByte(0) & 0xFF));
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            throw new RuntimeException("高低位转换整型错误！");
        }
    }
    // 4字节 小端-转换  // IndexOutOfBoundsException
    public static Float highByte2Float(ByteBuf buf){
        try {
            int i = buf.getByte(0) & 0xff |
                    (buf.getByte( 1) & 0xff) <<  8 |
                    (buf.getByte( 2) & 0xff) << 16 |
                    buf.getByte( 3) << 24;
            return Float.intBitsToFloat(i);

        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            throw new RuntimeException("高低位转换Float错误！");
        }
    }

    static final byte[] initByte = {
             (byte)0x00, (byte)0x30, (byte)0x00, (byte)0x10, (byte)0x30, (byte)0x30, (byte)0x32,
            0x30, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30,
            (byte)0x30, (byte)0x33, (byte)0x38, (byte)0x38, (byte)0x17, (byte)0xc6, (byte)0x00, (byte)0x03,
            (byte)0x00, (byte)0x16, (byte)0x1e, (byte)0x00, (byte)0x1e, (byte)0x00, (byte)0x1e, (byte)0x00,
            (byte)0x1e, (byte)0x00, (byte)0x1e, (byte)0x00, (byte)0x33, (byte)0x33, (byte)0x65, (byte)0x43,
            (byte)0x3d, (byte)0x0a, (byte)0x97, (byte)0x3f, (byte)0x9a, (byte)0xd9, (byte)0x80, (byte)0x43,
            (byte)0xb2, (byte)0x89};


//            (byte)0x00,(byte)0xca,(byte)0x00,(byte)0x01,(byte)0x30,(byte)0x30,(byte)0x32,(byte)0x30,(byte)0x32,
//            (byte)0x30,(byte)0x32,(byte)0x31,(byte)0x31,(byte)0x30,(byte)0x30,(byte)0x30,(byte)0x30,(byte)0x33,
//            (byte)0x38,(byte)0x38,(byte)0x30,(byte)0x34,(byte)0x2e,(byte)0x32,(byte)0x34,(byte)0x30,(byte)0x31,
//            (byte)0x3a,(byte)0xe5,(byte)0xb7,(byte)0xa5,(byte)0xe4,(byte)0xbd,(byte)0x9c,(byte)0xe4,(byte)0xbd,
//            (byte)0x8d,(byte)0xe7,(byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,
//            (byte)0x32,(byte)0x3a,(byte)0xe7,(byte)0x94,(byte)0xb5,(byte)0xe6,(byte)0x9c,(byte)0xba,(byte)0xe4,
//            (byte)0xbd,(byte)0x8d,(byte)0xe6,(byte)0x8e,(byte)0xa7,(byte)0xe5,(byte)0x88,(byte)0xb6,(byte)0xe7,
//            (byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,(byte)0x33,(byte)0x3a,
//            (byte)0xe9,(byte)0x97,(byte)0xb8,(byte)0xe9,(byte)0x81,(byte)0x93,(byte)0xe4,(byte)0xbd,(byte)0x8d,
//            (byte)0xe6,(byte)0x8e,(byte)0xa7,(byte)0xe5,(byte)0x88,(byte)0xb6,(byte)0xe7,(byte)0xb1,(byte)0xbb,
//            (byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,(byte)0x34,(byte)0x3a,(byte)0xe6,(byte)0x95,
//            (byte)0xb0,(byte)0xe6,(byte)0x8d,(byte)0xae,(byte)0xe9,(byte)0x87,(byte)0x87,(byte)0xe9,(byte)0x9b,
//            (byte)0x86,(byte)0x49,(byte)0x4f,(byte)0xe7,(byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,
//            (byte)0x3b,(byte)0x30,(byte)0x35,(byte)0x3a,(byte)0xe5,(byte)0xb7,(byte)0xa5,(byte)0xe4,(byte)0xbd,
//            (byte)0x9c,(byte)0xe4,(byte)0xbd,(byte)0x8d,(byte)0xe8,(byte)0x87,(byte)0xaa,(byte)0xe6,(byte)0xa3,
//            (byte)0x80,(byte)0xe7,(byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,
//            (byte)0x36,(byte)0x3a,(byte)0xe5,(byte)0xb7,(byte)0xa5,(byte)0xe4,(byte)0xbd,(byte)0x9c,(byte)0xe4,
//            (byte)0xbd,(byte)0x8d,(byte)0x42,(byte)0x79,(byte)0x70,(byte)0x61,(byte)0x73,(byte)0x73,(byte)0xe7,
//            (byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,(byte)0x37,(byte)0x3a,
//            (byte)0x55,(byte)0x56,(byte)0xe8,(byte)0xae,(byte)0xbe,(byte)0xe5,(byte)0xa4,(byte)0x87,(byte)0xe7,
//            (byte)0xb1,(byte)0xbb,(byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x30,(byte)0x46,(byte)0x3a,
//            (byte)0xe6,(byte)0x9c,(byte)0xaa,(byte)0xe7,(byte)0x9f,(byte)0xa5,(byte)0xe7,(byte)0xb1,(byte)0xbb,
//            (byte)0xe5,(byte)0x88,(byte)0xab,(byte)0x3b,(byte)0x88,(byte)0x98};
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
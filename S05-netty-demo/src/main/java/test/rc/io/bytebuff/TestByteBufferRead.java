package test.rc.io.bytebuff;

import java.nio.ByteBuffer;

import static test.rc.io.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(10);
        allocate.put(new byte[]{'a', 'b', 'c'});
        allocate.flip();

//        allocate.get(new byte[3]);
//        debugAll(allocate);
        //重新下标为0
//        allocate.rewind();
//        System.out.println((char)allocate.get());
        System.out.println((char)allocate.get(2));
        //mark & reset
        //mark 做一个标记 记录position位置，reset是将position重置到mark的位置
        System.out.println((char)allocate.get());
        System.out.println((char)allocate.get());
        allocate.mark();
        System.out.println((char)allocate.get());
        allocate.reset();
        System.out.println((char)allocate.get());
        debugAll(allocate);

    }
}

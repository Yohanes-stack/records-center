package com.rc.netty.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try {
            FileChannel from = new FileInputStream("world.txt").getChannel();
            FileChannel to = new FileOutputStream("data.txt").getChannel();
            //效率高，底层会使用操作系统的0拷贝 但是最多只能使用2G
            long size = from.size();
            for (long left = size; left > 0; ) {
                left -= from.transferTo((size - left), left, to);
            }
            from.transferTo(0, from.size(), to);
        } catch (IOException e) {

        }
    }
}

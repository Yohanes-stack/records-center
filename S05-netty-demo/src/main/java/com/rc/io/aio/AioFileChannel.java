package com.rc.io.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

import static com.rc.io.ByteBufferUtil.debugAll;

public class AioFileChannel {
    public static void main(String[] args) throws InterruptedException, IOException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            //参数1 ByteBuffer
            //参数2 读取的起始位置
            //参数3 附件
            //参数4 回调对象
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            System.out.println("read begin ... ");
            CountDownLatch latch = new CountDownLatch(1);
            channel.read(byteBuffer, 0, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println("read complated ... ");
                    attachment.flip();
                    debugAll(attachment);
                    latch.countDown();

                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            System.out.println("read end ...");
            latch.await(); // 等待异步读取操作的完成
        } catch (IOException e) {
        }
    }
}

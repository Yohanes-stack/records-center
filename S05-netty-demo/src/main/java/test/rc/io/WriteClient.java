package test.rc.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8888));
        long count = 0;
        while (true) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 4024 * 40);
            int read = sc.read(byteBuffer);
            if(read != -1){
                count += sc.read(byteBuffer);
                System.out.println(count);
                byteBuffer.clear();
            }

        }
    }
}

package test.rc.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8889));
        System.out.println("waiting ... ");

        String message = "Hello, Server!\n666\n大dasd啊\n";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        sc.write(buffer);
        sc.close();
        System.out.println("sent message to server");

    }
}

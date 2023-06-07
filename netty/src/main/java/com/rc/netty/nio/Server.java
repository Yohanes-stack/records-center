package com.rc.netty.nio;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static com.rc.netty.ByteBufferUtil.debugRead;

public class Server {
    public static void main(String[] args) throws IOException {
        nonBlockingMode();
    }
    public static void nonBlockingMode() throws IOException {
        //使用nio来理解阻塞模式
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //非阻塞设置
        ssc.configureBlocking(false);
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8888));
        //3.连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //4.accept 建立与客户端链接，socketChannel用来与客户端之间连接
            //阻塞方法，线程停止运行 ，现在这里进行了非阻塞设置
            SocketChannel sc = ssc.accept();
            if(sc != null){
                System.out.println("connected ...");
                //控制下面是否阻塞
                sc.configureBlocking(false);
            }

            channels.add(sc);
            for (SocketChannel channel : channels) {
                //5.接受客户端发送的数据
                //阻塞 线程停止运行 现在这里进行了 非阻塞设置 如果没有读到数据，read返回0
                int read = channel.read(buffer);
                if(read > 0){
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    System.out.println("after read....");
                }

            }
        }
    }

    public static void blockingMode() throws IOException {
        //使用nio来理解阻塞模式
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8888));
        //3.连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //4.accept 建立与客户端链接，socketChannel用来与客户端之间连接
            System.out.println("connecting ...");
            //阻塞方法，线程停止运行
            SocketChannel sc = ssc.accept();
            System.out.println("connected ...");
            System.out.flush();
            channels.add(sc);
            for (SocketChannel channel : channels) {
                //5.接受客户端发送的数据
                System.out.println("before read ...");
                //阻塞 线程停止运行
                channel.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                System.out.println("after read....");
            }
        }
    }
}

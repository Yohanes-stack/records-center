package test.rc.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static test.rc.io.ByteBufferUtil.debugAll;
import static test.rc.io.ByteBufferUtil.debugRead;

public class Server {
    public static void main(String[] args) throws IOException {
        selectorMode();
    }

    public static void selectorMode() throws IOException {
        //1.创建selector，管理多个channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2.建立selector和channel的联系（注册）
        //selectionKey 将来事件发生后，通过它可以知道事件和哪个channel事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        //关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        System.out.println(sscKey);
        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            //3.select方法 没有事件发生，线程阻塞，有事件线程才会恢复运行
            selector.select();
            //4.处理事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                System.out.println(key);
                //处理key 要从selectKeys集合中删除，否则下次处理会报错
                iterator.remove();
                ;
                //5.区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    //将一个byteBuffer作为附件关联到selectionKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    System.out.println(sc);
                } else if (key.isReadable()) {
                    //拿到触发事件的channel
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        //获取selectionKey上关联的附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if(buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        //客户端断开了，需要将key取消（从selector的keys集合中真正删除）
                        key.cancel();
                    }

                }

            }
        }
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
            if (sc != null) {
                System.out.println("connected ...");
                //控制下面是否阻塞
                sc.configureBlocking(false);
            }

            channels.add(sc);
            for (SocketChannel channel : channels) {
                //5.接受客户端发送的数据
                //阻塞 线程停止运行 现在这里进行了 非阻塞设置 如果没有读到数据，read返回0
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    System.out.println("after read....");
                }

            }
        }
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

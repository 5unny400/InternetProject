package Course03.NIO.Exercise;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServerMsg implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public NIOServerMsg(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            //创建selector复用器
            selector = Selector.open();
            //将监听事件注册到复用器上
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //等待系统返回已完成的事件
            //select()本身是会阻塞，等系统告诉用户空间那些事件已经准备就绪，返回结果表示已准备完成的事件个数

            while (selector.select() > 0) {
                //感兴趣事件集合（指的就是注册到selector中的事件）
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    //删除掉已经完成的事件
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        //当前是可接收事件已经准备就绪
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();

                        //接收客户端连接,返回一个SocketChannel实例表示是客户端的连接
                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        System.out.println("客户端连接上");

                        //设置SocketChannel实例为非阻塞
                        socketChannel.configureBlocking(false);

                        //将SocketChannel注册到复用器上，并关注读事件
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }

                    new NIOServerMsgChild(selectionKey);
                }
            }
            //来循环使事件完成
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

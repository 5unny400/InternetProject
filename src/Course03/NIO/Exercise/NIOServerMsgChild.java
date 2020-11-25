package Course03.NIO.Exercise;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServerMsgChild implements Runnable {
    private Selector selector;
    //SocketChannel socketChannel;

    public NIOServerMsgChild(SocketChannel socketChannel) throws IOException {
        //  this.socketChannel = socketChannel;

        socketChannel.configureBlocking(false);

        //创建selector复用器
        selector = Selector.open();
        //将SocketChannel注册到复用器上，并关注读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void run() {
        try {
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

                    if (selectionKey.isReadable()) {
                        //当前有可读事件发生
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                        //读数据 堆上创建指定大小的缓冲
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //往Buffer中写数据
                        int read = socketChannel.read(buffer);
                        if (read == -1) {
                            //客户端已经关闭
                            socketChannel.close();
                            //处理下一个客户端
                            continue;
                        }

                        buffer.flip();//为读取buffer转换
                        byte[] bytes = new byte[buffer.remaining()];
                        //读取buff数据
                        buffer.get(bytes);
                        //转为String类型
                        String msg = new String(bytes);

                        System.out.println("客户端：" + socketChannel.getRemoteAddress() + " 发送数据" + msg);

                        //返回信息，表示服务端收到
                        String recv = "[echo]:" + msg;
                        //先将Buffer清空
                        buffer.clear();
                        //往Buffer写数据
                        buffer.put(recv.getBytes());

                        buffer.flip();//为读取buffer转换
                        //将Buffer数据读到channel通道，
                        socketChannel.write(buffer);

                        //业务断开
                        if ("exit".equals(msg)) {
                            socketChannel.close();
                            System.out.println("断开连接了！");
                        }
                    }
                }
                //来循环使事件完成
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

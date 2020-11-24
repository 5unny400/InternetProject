package Course03.NIO.Exercise;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NIOServerMsgChild implements Runnable {
    SelectionKey selectionKey;

    public NIOServerMsgChild(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        try {
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
                    return;
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
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package Course03.NIO.Exercise;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NIOClient {
    private Selector selector = null;
    private SocketChannel socketChannel;

    void startClient(){
        try {
            //创建SocketChannel通道
            socketChannel = SocketChannel.open();

            //设置socketChannel为非阻塞
            socketChannel.configureBlocking(false);

            //实例化复用器
            selector = Selector.open();
            //连接服务端,该connect不会阻塞，会立即返回 boolean true:连接成功  false：表示还未连接成功
            if (!socketChannel.connect(new InetSocketAddress("127.0.0.1", 6666))){
                //表示连接不成功 当前正在连接,将当前的可连接事件交给内核帮助监听
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                //等待连接完成
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isConnectable()) socketChannel.finishConnect();
                }
            }

            //要么是连接成功
            //给服务端发送数据
            Scanner scanner = new Scanner(System.in);
            String msg;

            //注册读事件
            socketChannel.register(selector,SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while ((msg =scanner.nextLine()) != null) {
                //重复性读操作
                buffer.clear();
//                msg +="\n";
                //将数据写入到Buffer中
                buffer.put((msg+"\n").getBytes());


                buffer.flip();//为读取buffer转换
                //将数据从 Buffer中写入channel通道，对于Buffer而言，是读取数据
                socketChannel.write(buffer);

                //等内核数据准备完成（阻塞）
                selector.select();

                Iterator <SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    //事件无效的话
                    if(!selectionKey.isValid()) continue;
                    //如果是可读事件
                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();

                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        //将数据从通道写入Buffer
                        channel.read(allocate);

                        //读写模式切换
                        allocate.flip();//之后要从buffer读取
                        //remaining 实际读取的数据长度
                        byte[] bytes = new byte[allocate.remaining()];
                        //将Buffer数据读到byte数组中
                        allocate.get(bytes);
                        //转换信息为String
                        String recv = new String(bytes);

                        System.out.println(recv);
                    }
                }
                //判断业务是否结束
                if ("exit".equals(msg)) {
                    break;
                }
            }
            //关闭资源
            selector.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

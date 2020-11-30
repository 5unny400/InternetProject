package 聊天TCP;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
public class Server {
    //创建selector复用器
    Selector selector;
    private ServerSocketChannel listenChannel;

    private Server() throws IOException {
        //创建复用器
        selector = Selector.open();

        //得到 channel 通道
        listenChannel = ServerSocketChannel.open();
        //绑定端口
        listenChannel.bind(new InetSocketAddress(6666));
        //设置非阻塞模式
        listenChannel.configureBlocking(false);
        //将 channel 注册到 selector
        listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("等待连接...");
    }
    //监听消息
    private void listener() throws IOException {
        while(true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                //取出 selectionKey
                SelectionKey key = iterator.next();
                //如果监听到 accept
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = listenChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println(socketChannel.getRemoteAddress() + "上线了！");
                }
                if (key.isReadable()) {
                    //读取信息
                    readInfo(key);
                }
                //删除当前 key 防止 重复读
                iterator.remove();
            }
        }
    }

    private void readInfo(SelectionKey selectionKey){
        SocketChannel tmp = null;
        try {
            //获取通道
            tmp = (SocketChannel) selectionKey.channel();
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            int count = tmp.read(allocate);
            allocate.flip();
            if (count > 0) {
                //创建byte数组
                byte[] bytes = new byte[allocate.remaining()];
                //将Buffer数据读到byte数组中
                allocate.get(bytes);
                //转换信息为String
                String recv = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(tmp.getRemoteAddress() +"客户端:"+recv.trim());
                //向其他 客户端 转发消息
                sendToToClient(recv, tmp);
            }
        } catch (Exception e) {
            try {
                assert tmp != null;
                System.out.println(tmp.getRemoteAddress() + "离线了");
                //取消注册
                selectionKey.cancel();
                //关闭通道
                tmp.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void sendToToClient(String recv, SocketChannel tmp) throws IOException {
//        System.out.println(recv);
        // 排除selector中的自己
        for (SelectionKey selectionKey : selector.keys()) {
            Channel channel = selectionKey.channel();
            //排除自己
            if (channel instanceof SocketChannel && channel != tmp) {
                SocketChannel socketChannel = (SocketChannel)channel;
                ByteBuffer buffer = ByteBuffer.wrap(recv.getBytes());
                //将 buffer 写入 channel
                socketChannel.write(buffer);
            }
        }
        System.out.println("服务端已转发"+ tmp.getRemoteAddress()+"的消息！");
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.listener();
    }
}
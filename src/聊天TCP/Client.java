package 聊天TCP;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
public class Client {
    //复用器
    private Selector selector = null;
    //通讯通道
    private SocketChannel socketChannel;
    //客户端名称
    private String userName;
    //构造函数初始化
    private Client() {
        try {
            //实例化复用器
            selector = Selector.open();

            //创建SocketChannel通道
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 6666));
            System.out.println("正在连接会话...");

            //设置socketChannel为非阻塞
            socketChannel.configureBlocking(false);

            socketChannel.register(selector, SelectionKey.OP_READ);

            userName = socketChannel.getLocalAddress().toString();
            //连接成功
            System.out.println(userName + "会话连接成功！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //发送消息
    private void sendInfo(String str) throws UnsupportedEncodingException {
        String info = userName + "说:" + str;
        info = new String(info.getBytes(), StandardCharsets.UTF_8);
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取消息
    private void readInfo() {
        try {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            //有事件处理
            while (iterator.hasNext()) {

                SelectionKey selectionKey = iterator.next();
                //移除已完成的事件
                iterator.remove();

                //事件无效的话，处理下一个事件
                if (!selectionKey.isValid()) continue;

                if (selectionKey.isReadable()) {
                    SocketChannel tmp = (SocketChannel) selectionKey.channel();
                    //得到一个 buffer
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //get a buffer
                    tmp.read(buffer);
                    buffer.flip();
//remaining 实际读取的数据长度
                    byte[] bytes = new byte[buffer.remaining()];

                    //将Buffer数据读到byte数组中
                    buffer.get(bytes);

                    String recv = new String(bytes,StandardCharsets.UTF_8);
                    System.out.println(recv.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws UnsupportedEncodingException {
        Client client = new Client();

        new Thread(() -> {
            while (true) {
                client.readInfo();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        String s;
        do{
            s = scanner.nextLine();
            client.sendInfo(s);
        }while (!s.equals("exit"));
        System.exit(0);
    }
}


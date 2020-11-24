package Course03.NIO.Exercise;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class NIOServer {


    public static void main(String[] args) {
        //创建服务端ServerSocketChannel实例
        ServerSocketChannel serverSocketChannel;

        try {
            serverSocketChannel = ServerSocketChannel.open();

            //绑定端口
            serverSocketChannel.bind(new InetSocketAddress(6666));
            System.out.println("服务端启动啦");

            //监听

            //设置serverSocketChannel为非阻塞
            serverSocketChannel.configureBlocking(false);


            new Thread(new NIOServerMsg(serverSocketChannel)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

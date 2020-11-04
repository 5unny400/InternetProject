package Course01.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    /**
     * 一般是接收
     * 1、创建ServerSocket的实例
     * 2、绑定端口
     * 3、监听客户端的连接
     * 4、获取客户端连接的实例
     * 5、进行 IO操作
     */
    public static void main(String[] args) throws IOException {
        //创建ServerSocket实例
        final ServerSocket serverSocket = new ServerSocket();

        //绑定端口6666 - 65536
        serverSocket.bind(new InetSocketAddress(6666));
        System.out.println("服务器启动！");

        //监听客户端的连接accept
        final Socket accept = serverSocket.accept();
        System.out.println("有客户端连接！");


        //进行IO 操作
        //input 源-》当前程序         output当前程序到源
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        String msg = bufferedReader.readLine();

        System.out.println("IO 操作：" + msg);

        //关闭源
        serverSocket.close();
        accept.close();
    }
}

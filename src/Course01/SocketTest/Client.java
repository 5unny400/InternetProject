package Course01.SocketTest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    /*
     * 一般主动连接
     * 1、创建Socket实例
     * 2、连接服务端端口IP+端口，connect
     * 3、进行IO操作
     * 4、关闭资源
     */
    public static void main(String[] args) throws IOException {
        //c创建socket实例
        Socket socket = new Socket();
        System.out.println("客户端启动！");

        //连接服务器端口IP+端口
        socket.connect(new InetSocketAddress("127.0.0.1",6666));
        System.out.println("连接服务器成功！");

        String s = "你好！";
        //进行IO操作
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(s.getBytes());

        //断开连接关闭资源
        socket.close();
        System.out.println("客户端资源关闭！");
    }

}

package Course02.BIO.ExerciseStandard;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MutiThreadServer {
    public static void main(String[] args) {
        //1、创建实例ServerSocket
        //2、绑定端口
        //3、监听连接
        //4、IO操作
        //5、关闭资源
        try {
            ServerSocket serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress(9999));
            System.out.println("服务端启动了！");

            //接收客户端的连接
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("客户端："+socket.getRemoteSocketAddress()+"连接上拉！");

                //将socket交给子线程处理
                new Thread(new MutiThread(socket)).start();

            }

            //此时并没有将socket资源

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

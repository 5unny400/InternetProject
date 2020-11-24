package TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 聊天室服务端
 *
 * @author
 */
public class Server {
    /**
     * 运行在服务端的ServerSocket主要有两个作用：
     * 1：向系统申请服务端口，客户端就是通过这个端口与服务端建立连接的。
     * 2：监听服务端口，一旦客户端与服务端建立连接，就会
     * 自动创建一个Socket，并通过该Socket与建立连接的
     * 客户端进行交互。
     */
    private ServerSocket server;

    public Server() {
        try {
            /**
             * 实例化ServerSocket的同时要指定向系统申请的服务端口。注意，该端口
             * 不能与当前系统其它应用程序申请的端口号一致，
             * 否则会抛出端口被占用的异常。
             */
            System.out.println("正在启动服务端...");
            server = new ServerSocket(8088);
            System.out.println("服务端启动完毕！");
        } catch (Exception e) {

        }
    }

    public void start() {
        try {
            System.out.println("等待客户端连接");
            Socket socket = server.accept();
            /**
             * 准备接受客户端连接
             *
             * 这是一个阻塞方法。
             *一旦调用accpet()方法，服务端阻塞在这里，等着客户端连接了。
             *这时当我们启动客户端时，客户端实例化socket，通过IP找到应用程序。
             *这时accpet()马上就会有反应。accpet()方法执行完就返回一个socket。
             *通过这个socket就可以与刚建立连接的这个客户端进行通讯了。
             *客户端创建一个socket，服务端通过accept()接收一个socket。
             *这时候就可以进行沟通了。
             * 需要处理异常。
             */
            InputStream in = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String message = br.readLine();
            System.out.println("客户端说：" + message);

            System.out.println("一个客户端连接了");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}

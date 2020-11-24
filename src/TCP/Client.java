package TCP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 聊天室客户端
 *
 * @author
 */
public class Client {
    /**
     * 套接字
     * 封装了TCP协议的通信细节，让我们可以简单地使用它完成TCP通讯
     * Socket连接后提供了两条流，通过两条流的读写操作完成与远端计算机的数据连接
     * 完成与远端计算机的数据交换。
     */
    private Socket socket;

    /**
     * 用来初始化客户端
     */
    public Client() {
        try {
            /**
             * 实例化Socket时需要传入两个参数：
             * 1：服务端的IP地址
             * 2：服务端的端口号
             * 通过IP地址可以找到服务端所在的计算机
             * 通过端口可以找到运行在服务端计算机上的
             * 服务端应用程序
             * 注意，实例化Socket的过程就是连接的过程，若
             * 连接失败就会抛出异常。
             */
            System.out.println("正在连接服务端...");
            socket = new Socket("localhost", 8088);
            System.out.println("与服务端建立连接！");

            /**
             * 有两个参数，一个是IP地址，一个是端口号
             */
            /**
             * 所有应用程序在使用网络的时候都要和操作系统申请一个网络端口
             *
             */
            /**
             * 异常要我们自己进行处理，不要抛出
             */
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 写程序时不要过度依赖main方法
     * 客户端开始工作的方法
     */
    public void start() {
        try {
            //socket.getOutputStream();
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
            //pw.write("你好服务端");
            //使用上面这条语句是错的，要使用具有自动行刷新的方法
            //不要再下意识地写上面那条语句了
            pw.println("你好服务端！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        client.start();

    }
}
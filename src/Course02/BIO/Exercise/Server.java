package Course02.BIO.Exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9990));
        System.out.println("服务端创建成功！");

        Socket socket = null;
        int i =0;
        while(i++ <= 5) {
            System.out.println("客户端"+ i +"连接！");
            socket = serverSocket.accept();
            SocketAddress socketAddress = socket.getRemoteSocketAddress();
            System.out.println("客户端："+i+"连接成功："+socketAddress);
            new Thread(new Test(socket)).start();
            System.out.println("=======================================");
            System.out.println("=======================================");
        }

    }

}

class Test implements Runnable{
    Socket socket;

    public Test(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream();
            String msg = reader.readLine();
            while ((msg != null) && ! "exit".equals(msg)) {

                System.out.println("接收到客户端数据："+msg);
                //将数据回写给客户端
                String info = "服务端已收到:"+msg+"\n";
                outputStream.write(info.getBytes());

                //重复接收客户端消息
                msg = reader.readLine();
            }

            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





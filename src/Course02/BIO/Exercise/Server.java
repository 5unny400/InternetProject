package Course02.BIO.Exercise;

import java.io.IOException;
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
            Scanner scan = new Scanner(socket.getInputStream());
            OutputStream outputStream = socket.getOutputStream();
            while(scan.hasNext()){
                String s = scan.nextLine();
                System.out.println("接收到客户端信息："+s);
                outputStream.write(("服务端已收到收到"+s).getBytes());
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





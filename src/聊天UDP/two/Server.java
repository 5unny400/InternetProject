package 聊天UDP.two;

import java.net.*;
import java.io.*;
public class Server {
    public static void main(String[] args) throws Exception {
        System.err.print("在8000端口建立连接...");
        ServerSocket ss = new ServerSocket(6666);
        System.out.println("ok.");
        System.out.print("等待客户端的连接...");
        Socket s = ss.accept();
        System.out.println("ok.");

        Sender sender=new Sender(s);//调用sender
        sender.start();

        Receiver receiver=new Receiver(s);//调用receiver
        receiver.start();
    }
}
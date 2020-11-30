package 聊天UDP.two;


import java.net.*;
import java.io.*;
public class Client {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("127.0.0.1", 6666);
        System.out.println("ok.");
        System.err.print("客户端和127.0.0.1:8000建立连接...");


        Sender sender=new Sender(s);
        //调用snder类
        sender.start();

        Receiver receiver=new Receiver(s);
        //调用receiver类
        receiver.start();
    }
}
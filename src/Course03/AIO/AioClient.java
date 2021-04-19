package Course03.AIO;

import Course04Problems.ClientHandler;

import java.util.Scanner;

/**
 * ClassName:    AIOClient
 * Package:    Course03.AIO
 * Description:
 * Datetime:    2021/4/19 0019   19:29
 * Author: 沈新源
 */
public class AioClient {
    private static String DEFAULT_HOST = "127.0.0.1";
    private static int DEFAULT_PORT = 12345;
    private static ClientHandler clientHandle;
    public static void start(){
        start(DEFAULT_HOST,DEFAULT_PORT);
    }
    public static synchronized void start(String ip,int port){
        if(clientHandle!=null)
            return;
        clientHandle = new ClientHandler(ip,port);
        new Thread(clientHandle,"Client").start();
    }
    //向服务器发送消息
    public static boolean sendMsg(String msg) throws Exception{
        if(msg.equals("exit")) return false;
        clientHandle.sendMsg(msg);
        return true;
    }
    public static void main(String[] args) throws Exception{
        AioClient.start();
        System.out.println("请输入请求消息：");
        Scanner scanner = new Scanner(System.in);
        while(AioClient.sendMsg(scanner.nextLine()));
    }

}

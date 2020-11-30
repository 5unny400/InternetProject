package 聊天UDP;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server {
    //服务器地址
    private InetAddress address;
    //服务器名字
    private String serverName;

    //待发送消息对象对象
    private DatagramPacket datagramPacket;

    //发送接收消息对象
    private DatagramSocket datagramSocket;

    private Server() throws SocketException, UnknownHostException {
        //绑定端口
        datagramSocket = new DatagramSocket(8080);

        //获取服务器名称 服务器地址
        serverName = InetAddress.getLocalHost().getHostName();
        address = InetAddress.getByName(serverName);

        System.out.println("服务器名：" + serverName);
        System.out.println("等待消息...");
    }
    //检测消息
    private void receive() throws IOException {
        byte[] inBuf = new byte[256];
        //设置接收pkt对象的接收信息
        datagramPacket = new DatagramPacket(inBuf, inBuf.length);
        datagramSocket.receive(datagramPacket);

        //提取接收到的分组中的数据转换为字符串
        String str1 = new String(datagramPacket.getData());
        str1 = str1.trim();         //去掉字符串首尾空格
        if (str1.length() > 0) {
            int pot = datagramPacket.getPort();
            System.out.println("远端端口：" + pot);
            System.out.println("客户端发来消息：" + str1);
        }
    }
    //回复消息
    private void response(String reply) throws IOException {
        reply = "Server:" + reply;
        byte[] rep;
        rep = reply.getBytes();

        //下面是取得服务器地址
        datagramPacket = new DatagramPacket(rep, rep.length, address, 8081);
        datagramSocket.send(datagramPacket);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        server.receive();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        String s;
        do {
            s = scanner.nextLine();
            server.response(s);
        } while (!s.equals("exit"));
        scanner.close();
        System.exit(0);
    }
}


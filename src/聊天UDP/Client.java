package 聊天UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    String serverName = "SC-201905052136";          //服务器机器名
    //进行发送和接受操作的对象
    DatagramSocket datagramSocket;
    //要发送消息的对象
    DatagramPacket datagramPacket;
    //构造函数初始化
    private Client() throws IOException {
        datagramSocket = new DatagramSocket(8081);

        System.out.println("请发送消息给服务器：" + serverName);
    }
    //发送消息
    private void sendInfo(String str1) throws IOException {
        byte[] outBuf;
        outBuf = str1.getBytes();
        //获取服务器端地址
        InetAddress address = InetAddress.getByName(serverName);
        datagramPacket = new DatagramPacket(outBuf, outBuf.length, address, 8080);
        datagramSocket.send(datagramPacket);
    }
    //读取消息
    private void readInfo() throws IOException {
        //检测回复的消息
        byte[] inBuf = new byte[256];
        //下面是取得服务器地址
        //创建并设置接收pkt对象的接收信息
        datagramPacket = new DatagramPacket(inBuf, inBuf.length);
        datagramSocket.receive(datagramPacket);
        //提取接收到的分组中的数据转换为字符串
        String tmp;
        tmp = new String(datagramPacket.getData());
        tmp = tmp.trim();         //去掉字符串首尾空格
        if (tmp.length() > 0) {
            int pot = datagramPacket.getPort();
            System.out.println("远端端口：" + pot);
            System.out.println(tmp);
        }
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client();
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        client.readInfo();
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
            client.sendInfo(s);
        } while (!s.equals("exit"));
        scanner.close();
        System.exit(0);
    }
}

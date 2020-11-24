package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Server {
    public static void main(String[] str) {
        try {
//确定端口
            int port = 8080;
//创建DatagramSocket 对象，并指定该程序的通信端口为8080
            DatagramSocket receiveSocket = new DatagramSocket(port);
            while (true) {
//创建接收缓冲区
                byte[] by = new byte[1024];
//创建接收数据包
                DatagramPacket receivePacket = new DatagramPacket(by, by.length);
//接收数据
                receiveSocket.receive(receivePacket);
//解析消息并打印数据
                String string = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(string);
//通过数据包也可以解析ip、和端口,打印
                InetAddress ipAddress = receivePacket.getAddress();
                int receivePort = receivePacket.getPort();
                System.out.println("地址："+ipAddress);
                System.out.println("端口："+receivePort);
//发送数据
                String aaString = "服务端收到！";
                byte[] bb = aaString.getBytes();
//创建发送类型的数据包
                DatagramPacket sendPscket = new DatagramPacket(bb, bb.length, InetAddress.getByName("127.0.0.1"),
                        10000);
                receiveSocket.send(sendPscket);
            }

//receiveSocket.close();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

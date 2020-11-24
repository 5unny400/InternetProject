package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] str) {
        try {
//创建DatagramSocket 对象，并指定该程序的通信端口为10000
            DatagramSocket sendDatagramSocket = new DatagramSocket(10000);
//确定要发送的消息
            String string = "沈新源";
//转成字符数组类型
            byte[] by = string.getBytes();
//确定要发送的地址
//InetAddress ip = InetAddress.getLocalHost();//如果是自己电脑测试可以这样得到本机地址，也可以自己查自己的ip地址

//InetAddress ip = InetAddress.getByName("192.168.32.1");

//确定要发送的端口
            int port = 8080;
//创建发送类型的数据包，这个数据包包含了要发往的ip地址和端口
            DatagramPacket sendPscket = new DatagramPacket(by, by.length, InetAddress.getByName("127.0.0.1"), port);
//通过DatagramSocket 的send方法发送数据
            sendDatagramSocket.send(sendPscket);
//创建接收缓冲区
            byte[] bt = new byte[1024];
//创建接收类型的数据包

//DatagramPacket(byte[] buf, int length)构造 DatagramPacket，用来接收长度为 length 的数据包

            DatagramPacket receivePacket = new DatagramPacket(bt, bt.length);
//通过DatagramSocket 的receive方法发送数据
            sendDatagramSocket.receive(receivePacket);
//打印---数据包
            String daString = new String(receivePacket.getData(), 0, receivePacket.getLength());//getData()方法是返回数据缓冲区
            System.out.println(daString);
//关闭接收
            sendDatagramSocket.close();
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
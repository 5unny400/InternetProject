package Course02.BIO.ExerciseStandard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        System.out.println("客户端已创建！");

        socket.connect(new InetSocketAddress("127.0.0.1", 9999));
        System.out.println("连接服务器成功！");

        OutputStream outputStream = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("客户端已启动!");

        String msg;
        System.out.print("请输入消息：");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {

            msg = scan.nextLine();

            System.out.println("发送的消息：" + msg);
            outputStream.write((msg + "\n").getBytes());

            String recv = reader.readLine();
            System.out.println(recv);

            //如果是exit 则退出
            if ("exit".equals(msg)) break;
        }

        //关闭资源
        socket.close();

    }


}
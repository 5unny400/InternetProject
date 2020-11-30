package 聊天UDP.two;

import java.io.*;
import java.net.*;
public class Receiver  extends Thread{
    //新建个线程
    Socket socket;
    public Receiver(Socket s) throws Exception{
        if(s!=null){
            socket=s;
        }
        else {
            throw new Exception("error!");
        }
    }

    public void run(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            do {
                String t=br.readLine();
                System.out.println(t);
                if(t.equals("exit"))
                    System.exit(0);
            } while (true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
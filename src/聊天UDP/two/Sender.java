package 聊天UDP.two;

import java.io.*;
import java.net.*;
public class Sender extends Thread {
    //新建个线程
    Socket socket;
    BufferedWriter bw;
    public Sender(Socket s)  throws Exception{
        if(s!=null){
            socket=s;
            bw = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
        }
        else {
            throw new Exception("Socket对象不能为空!");
        }
    }
    public void run(){
        try {
            BufferedReader kr = new BufferedReader(new InputStreamReader(
                    System.in));
            while(true){
                String t=kr.readLine();
                bw.write(t+"\n");
                bw.flush();
                if(t.equals("exit"))
                    System.exit(0);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
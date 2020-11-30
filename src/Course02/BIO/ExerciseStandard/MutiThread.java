package Course02.BIO.ExerciseStandard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MutiThread implements Runnable{
    private Socket socket;

    public MutiThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        //读写操作
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream();

            String msg = null;

            while((msg = bufferedReader.readLine())!= null){
                System.out.println("线程："+   Thread.currentThread()+"接收客户端"+socket.getRemoteSocketAddress()+"消息:"+msg);

                String rec = "[echo]"+msg+"\n";
                outputStream.write(rec.getBytes());

                if(msg.equals("exit")) {
                    System.out.println("++++++++++++++++++++");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

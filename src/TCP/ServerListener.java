package TCP;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerListener extends Thread {

    ServerSocket server;
    volatile boolean serverFlag;
    public ServerListener(ServerSocket server) {
        this.server = server;
        serverFlag = true;
    }
    //停止监听
    public void stopListener(){
        serverFlag = false;
    }
    public void run() {
        while(serverFlag){
            Socket s = null;
            try {
                s = server.accept();
                new SocketChat(s).start();
                SocketMG.getsocketMG().setLog(s+"已登录");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

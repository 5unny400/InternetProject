package Course03.AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * ClassName:    AIOServer
 * Package:    Course03.AIO
 * Description:
 * Datetime:    2021/4/19 0019   19:30
 * Author: 沈新源
 */
public class AioServer {

    private static int DEFAULT_PORT = 12345;
    private static ServerHandler serverHandle;
    public volatile static long clientCount = 0;
    public static void start(){
        start(DEFAULT_PORT);
    }
    public static synchronized void start(int port){
        if(serverHandle!=null)
            return;
        serverHandle = new ServerHandler(port);
        new Thread(serverHandle,"Server").start();
    }
    public static void main(String[] args) {
        AioServer.start();
    }
}

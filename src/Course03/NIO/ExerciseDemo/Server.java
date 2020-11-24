package Course03.NIO.ExerciseDemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Selector selector;
    private ExecutorService tp = Executors.newCachedThreadPool();
    public static Map<Socket,Long> time_stat = new HashMap<Socket,Long>(10240);


    private void startServer() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel ssc = ServerSocketChannel.open();                          // 服务端SocketChannel
        ssc.configureBlocking(false);                                                  // 设置为非阻塞模式
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(),8000);// 使用8000端口
        ssc.socket().bind(isa);
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);   // 将ServerSocketChannel绑定到Selector上，感兴趣的时间为Accept
        for(;;){                     // 主要任务是等待-分发网络消息
            this.selector.select(); // 阻塞方法，如果当前没有准备好的的数据，就会等待，如果有的话返回已经准备好的SelectionKey数量
            Set<SelectionKey> readyKeys = this.selector.selectedKeys(); // 获取准备好的SelectionKey
            Iterator<SelectionKey> i = readyKeys.iterator();
            long e = 0;
            while(i.hasNext()){
                SelectionKey sk = i.next();
                i.remove();// 处理一个删除一个，不然可能重复处理
                if(sk.isAcceptable()){
                    doAccept(sk);
                }else if(sk.isValid() && sk.isReadable()){// 判断是否可以读
                    if(!time_stat.containsKey(((SocketChannel) sk.channel()).socket())){
                        time_stat.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
                    }
                    doRead(sk);
                }else if(sk.isValid() && sk.isWritable()){ // 判断是否可以写
                    doWrite(sk);
                    e = System.currentTimeMillis();
                    long b = time_stat.remove(((SocketChannel) sk.channel()).socket());
                    System.out.println("spend: "+(b-e)+"ms");
                }
            }
        }
    }

    private void doWrite(SelectionKey sk) {
    }

    private void doAccept(SelectionKey sk) {
        ServerSocketChannel server = (ServerSocketChannel) sk.channel();
        SocketChannel clientChannel;
        try {
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);// 非阻塞
            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);//将Channel注册到Selector上，并告诉Selector对读感兴趣，Channel准备好读时给线程一个通知
            EchoClient ec = new EchoClient();
            clientKey.attach(ec);// 客户端实例作为附件，附加到表示这个连接的SelectionKey上，可以在整个连接过程共享ec
            InetAddress clientAddress  = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from "+clientAddress.getHostAddress());
        } catch (Exception e) {}
    }


    public class EchoClient {
        private LinkedList<ByteBuffer> outq;
        public EchoClient() {
            this.outq = new LinkedList<ByteBuffer>();
        }
        public LinkedList<ByteBuffer> getOutq() {
            return outq;
        }
        public void enqueue(ByteBuffer bb) {
            this.outq.addFirst(bb);
        }
    }

    private void doRead(SelectionKey sk) {
        SocketChannel c = (SocketChannel) sk.channel();
        ByteBuffer bb = ByteBuffer.allocate(8192);
        int len;
        try {
            len = c.read(bb);// 存放读取的数据
            if(len<0){
                disconnect(sk);
                return;
            }
        } catch (Exception e) {
            System.out.println("Failed to read from client!");
            e.printStackTrace();
            disconnect(sk);
            return;
        }
        bb.flip();
        tp.execute(new HandleMsg(sk,bb)); // 线程池处理数据
    }

    private void disconnect(SelectionKey sk) {
    }

}

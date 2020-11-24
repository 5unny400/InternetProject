package Course03.NIO.ExerciseDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class Client {

    private Selector selector;
    public void init(String ip,int port) throws IOException {
        SocketChannel s = SocketChannel.open();
        s.configureBlocking(false);
        this.selector = SelectorProvider.provider().openSelector();
        s.connect(new InetSocketAddress(ip,port));// 并不定连接成功，需要finishConnect()确认
        s.register(selector, SelectionKey.OP_CONNECT);
    }

    public void working() throws IOException{
        while(true){
            if(!this.selector.isOpen()){
                break;
            }
            this.selector.select();
            Iterator<SelectionKey> i = this.selector.selectedKeys().iterator();
            while(i.hasNext()){
                SelectionKey key = i.next();
                i.remove();
                if(key.isConnectable()){
                    connect(key);// 判断有没有完成连接，没有的话使用finishConnect()方法完成连接，并向Channel中写入数据及感兴趣的事情
                }else if(key.isReadable()){
                    read(key);
                }
            }
        }
    }

    private void connect(SelectionKey key) {
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel c = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        c.read(buffer);
        byte[] bs = buffer.array();
        String msg = new String(bs).trim();
        System.out.println("客户端收到信息："+msg);
        c.close();
        key.selector().close();
    }
}

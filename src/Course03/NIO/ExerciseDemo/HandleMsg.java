package Course03.NIO.ExerciseDemo;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class HandleMsg implements Runnable{

    SelectionKey sk;
    ByteBuffer bb;
    Selector selector;

    public HandleMsg(SelectionKey sk, ByteBuffer bb){
        this.sk = sk;
        this.bb = bb;
    }
    @Override
    public void run() {
        Server.EchoClient ec = (Server.EchoClient) sk.attachment();
        ec.enqueue(bb);// 将收到的数据压入队列，业务逻辑也可以在这个地方处理了
        sk.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        selector.wakeup();// 强迫Selector立即返回
    }

    private void doWrite(SelectionKey sk) {
        SocketChannel c = (SocketChannel) sk.channel();
        Server.EchoClient ec = (Server.EchoClient) sk.attachment();
        LinkedList<ByteBuffer> outq = ec.getOutq();
        ByteBuffer bb = outq.getLast();// 列表顶部元素，写回客户端
        try {
            int len = c.write(bb);
            if(len == -1){
                disconnect(sk);
                return;
            }
            if(bb.remaining()== 0){
                outq.removeLast();// 缓冲区已经完成写，删除它
            }
        } catch (Exception e) {
            System.out.println("Failed to write to client.");
            e.printStackTrace();
            disconnect(sk);
            return;
        }
        if(outq.size()==0){
            sk.interestOps(SelectionKey.OP_READ);
        }
    }

    private void disconnect(SelectionKey sk) {
    }
}
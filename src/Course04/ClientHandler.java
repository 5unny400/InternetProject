package Course04;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class ClientHandler implements Runnable{
    private AsynchronousSocketChannel clientChannel;
    private String host;
    private int port;
    private CountDownLatch latch;
    public ClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            //创建异步的客户端通道
            clientChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //创建CountDownLatch等待
//        latch = new CountDownLatch(1);
        //发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法
        clientChannel.connect(new InetSocketAddress(host, port), this, new AcceptHandler());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        try {
//            latch.await();
//        } catch (InterruptedException e1) {
//            e1.printStackTrace();
//        }
//        try {
//            clientChannel.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //向服务器发送消息
    public void sendMsg(String msg){
        byte[] req = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        System.out.println(">>>>>>msg:"+msg);
        writeBuffer.put(req);
        writeBuffer.flip();
        //异步写
        clientChannel.write(writeBuffer, writeBuffer,new WriteHandler(clientChannel));
    }


    /**
     * 接收类
     */
    class AcceptHandler implements CompletionHandler<Void, ClientHandler> {

        public AcceptHandler() {}

        @Override
        public void completed(Void result, ClientHandler attachment) {
            System.out.println("连接服务器成功");
        }

        @Override
        public void failed(Throwable exc, ClientHandler attachment) {
            exc.printStackTrace();
            try {
                attachment.clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class WriteHandler implements CompletionHandler<Integer, ByteBuffer> {
        private AsynchronousSocketChannel channel;

        public WriteHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            //完成全部数据的写入
            if (attachment.hasRemaining()) {
                //数据没有写完，继续写
                System.out.println("WriteHandler.hasRemaining>>>>>");
                clientChannel.write(attachment, attachment, this);
            } else {
                //读取数据
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                clientChannel.read(readBuffer, readBuffer, new ReadHandler(clientChannel));
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            exc.printStackTrace();
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private AsynchronousSocketChannel clientChannel;
        public ReadHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }
        @Override
        public void completed(Integer result,ByteBuffer buffer) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String body;
            try {
                body = new String(bytes,"UTF-8");
                System.out.println("客户端收到结果:"+ body);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void failed(Throwable exc,ByteBuffer attachment) {
            System.err.println("数据读取失败...");
            try {
                clientChannel.close();
            } catch (IOException e) {
            }
        }
    }
}
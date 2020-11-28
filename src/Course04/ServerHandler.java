package Course04;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerHandler implements Runnable{
    private AsynchronousServerSocketChannel channel;

    public ServerHandler(int port) {
        try {
            //创建服务端通道
            channel = AsynchronousServerSocketChannel.open();
            //绑定端口
            channel.bind(new InetSocketAddress(port));
            System.out.println("服务端已启动，端口号:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        channel.accept(this, new AcceptHandler());
//        Future <AsynchronousSocketChannel> accept = channel.accept();
        //该步操作是异步操作 防止当前线程直接执行结束
        //方案1： while(true)+sleep
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        //方案2 CountDownLatch 作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞 此处，让现场在此阻塞，防止服务端执行完成后退出
//
//        CountDownLatch count = new CountDownLatch(1);
//        channel.accept(this, new AcceptHandler());
//        try {
//            count.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    // CompletionHandler<V,A>
    // V-IO操作的结果，这里是成功建立的连接,AsynchronousSocketChannel
    // A-IO操作附件，这里传入AsynchronousServerSocketChannel便于继续接收请求建立新连接

    class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, ServerHandler> {

        @Override
        public void completed(AsynchronousSocketChannel channel, ServerHandler serverHandler) {
            //创建新的Buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //异步读  第三个参数为接收消息回调的业务Handler
//            channel.read(buffer, buffer, new ReadHandler(channel));
            //继续接受其他客户端请求
            serverHandler.channel.accept(null, this);
        }

        @Override
        public void failed(Throwable exc, ServerHandler serverHandler) {
            exc.printStackTrace();
        }
    }

    class ReadHandler implements CompletionHandler<ByteBuffer, ByteBuffer> {
        //用户读取或者发送消息的channel
        private AsynchronousSocketChannel channel;

        public ReadHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(ByteBuffer result, ByteBuffer attachment) {
            result.flip();
            byte[] msg = new byte[result.remaining()];
            result.get(msg);

            try {
                String expression = new String(msg, "UTF-8");
                System.out.println("服务器收到消息: " + expression);
//                String result1 = "服务端收到消息\n";
                result.clear();

                //向客户端发送消息
                doWrite(expression);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        //发送消息
        private void doWrite(String msg) {
            byte[] bytes = msg.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(bytes);
            buffer.flip();
            //异步写数据
            channel.write(buffer, buffer, new CompletionHandler <Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    //如果没有发送完，继续发送
                    if (attachment.hasRemaining()) {
                        channel.write(attachment, attachment, this);
                    } else {
                        //创建新的Buffer
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        //异步读 第三个参数为接收消息回调的业务Handler
//                        channel.read(allocate, attachment, new ReadHandler(channel));
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
            });
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
}
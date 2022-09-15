package com.dream.netty.demo;


import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {


    private ChannelHandlerContext ctx;
    private final ExecutorService fixedThreadPool;

    public SimpleClientHandler() {
        fixedThreadPool = Executors.newFixedThreadPool(1);
    }

    /**
     *
     * 本方法用于接收服务端发送过来的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleClientHandler.channelRead");
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);

        System.out.println("Server said:" + new String(result1));
        result.release();
    }

    /**
     * 本方法用于处理异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(System.currentTimeMillis() + "    client exceptionCaught");
        fixedThreadPool.shutdownNow();

        TimeUnit.SECONDS.sleep(2);
        // 当出现异常就关闭连接
//        cause.printStackTrace();
        ctx.close();
    }

    private static final String msg = "hello Server!";
    private static final byte[] msgBytes = msg.getBytes();


    /**
     * 本方法用于向服务端发送信息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channelActive");
        this.ctx = ctx;
        fixedThreadPool.submit(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if(i % 100000 == 0){
                    System.out.println("Sender count : " + i);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                    ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
                    encoded.writeBytes(msgBytes);
                    ctx.write(encoded);
                    ctx.flush();
                } catch (Throwable e) {
                    System.out.println("client sender exception: " + e);
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channelInactive");
        fixedThreadPool.shutdownNow();
        super.channelInactive(ctx);
    }
}

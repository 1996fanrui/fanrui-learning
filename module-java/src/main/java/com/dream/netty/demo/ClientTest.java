package com.dream.netty.demo;

import org.apache.flink.shaded.netty4.io.netty.bootstrap.Bootstrap;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelFuture;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInitializer;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelOption;
import org.apache.flink.shaded.netty4.io.netty.channel.EventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioSocketChannel;

// java -Xmx8589934592 -Xms8589934592 -cp ./daf6a9e58ddd4892bef72115b362102b com.dream.netty.demo.ClientTest
public class ClientTest {
    public void connect(String host, int port) throws Exception {
        EventLoopGroup worker = new NioEventLoopGroup(2);
        try{
            Bootstrap b = new Bootstrap();
            /**
             *EventLoop的组
             */
            b.group(worker);
            b.channel(NioSocketChannel.class);

            // Add the server port number to the name in order to distinguish
            // multiple clients running on the same host.
//            EpollEventLoopGroup epollGroup =
//                    new EpollEventLoopGroup(5);
//            b.group(epollGroup).channel(EpollSocketChannel.class);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120_000);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000);
            /**
             * 自定义客户端Handle（客户端在这里搞事情）
             */
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    System.out.println(System.currentTimeMillis() + "    register handler");
                    ch.pipeline().addLast(new SimpleClientHandler());
                }
            });
            /** 开启客户端监听*/
            System.out.println(System.currentTimeMillis() + "    client start sync");
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println(System.currentTimeMillis() + "    client sync completed");
            /**等待数据直到客户端关闭*/
            f.channel().closeFuture().sync();
        } catch (Throwable e) {
            System.out.println("connect exception" + e);
            throw e;
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        ClientTest client=new ClientTest();
        client.connect("ip-10-128-136-134", 56478);
//        client.connect("78.25.26.1", 9999);

    }
}

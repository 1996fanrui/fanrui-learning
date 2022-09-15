package com.dream.netty.demo;

import org.apache.flink.shaded.netty4.io.netty.bootstrap.ServerBootstrap;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelFuture;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelInitializer;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelOption;
import org.apache.flink.shaded.netty4.io.netty.channel.EventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.SocketChannel;
import org.apache.flink.shaded.netty4.io.netty.channel.socket.nio.NioServerSocketChannel;


public class ServerTest {
    /**
     * 服务端口
     */
    private int port = 56478;
    /**
     * 开启服务的方法
     */
    public void StartNetty(){

        /**创建两个EventLoop的组，EventLoop 这个相当于一个处理线程，
         是Netty接收请求和处理IO请求的线程。不理解的话可以百度NIO图解*/
        /*
        相关资料：NioEventLoopGroup是一个处理I/O操作的多线程事件循环。
        Netty为不同类型的传输提供了各种EventLoopGroup实现。
        在本例中，我们正在实现一个服务器端应用程序，因此将使用两个NioEventLoopGroup。
        第一个，通常称为“boss”，接受传入的连接。
        第二个，通常称为“worker”，当boss接受连接并注册被接受的连接到worker时，处理被接受连接的流量。
        使用了多少线程以及如何将它们映射到创建的通道取决于EventLoopGroup实现，甚至可以通过构造函数进行配置。
        */
        EventLoopGroup acceptor = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(2);
        try {
            //1、创建启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //2、配置启动参数等
            /**设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO(server使用两个参数这个)
             *public ServerBootstrap group(EventLoopGroup group)
             *public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup)
             */
            bootstrap.group(acceptor, worker);
            /**设置选项
             * 参数：Socket的标准参数（key，value），可自行百度
             * eg:
             * bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
             *bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
             * */
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

            //用于构造socketchannel工厂
            bootstrap.channel(NioServerSocketChannel.class);
            /**
             * 传入自定义客户端Handle（服务端在这里搞事情）
             */
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 注册handler
                    ch.pipeline().addLast(new SimpleServerHandler());
                }
            });

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = bootstrap.bind(port).sync();


            // 等待服务器 socket 关闭 。
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }



    public static void main(String[] args) {
        new ServerTest().StartNetty();
    }

}

package com.nubettix.omnilink.server.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NettyServer {

  static final int PORT = 8007;

  public static void main(String[] args) throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup) // Set boss & worker groups
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              p.addLast(new ByteArrayDecoder());
              p.addLast(new ByteArrayEncoder());
              p.addLast(new ServerHandler());
            }
          });
      // Start the server.
      ChannelFuture f = b.bind(PORT).sync();
      System.out.println("Netty Server started.");
      // Wait until the server socket is closed.
      f.channel().closeFuture().sync();
    } finally {
      // Shut down all event loops to terminate all threads.
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

@Sharable
class ServerHandler extends SimpleChannelInboundHandler<byte[]> {

  static final List<Channel> channels = new ArrayList<Channel>();

  @Override
  public void channelActive(final ChannelHandlerContext ctx) {
    System.out.println("Client joined - " + ctx);
    channels.add(ctx.channel());
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
    System.out.println(
        "[" + new Date() + "] Message received from " + ctx.channel().remoteAddress() + " : "
            + new String(msg));
    System.out.println("Sending message to " + ctx.channel().remoteAddress() + " : "
        + new String(msg));
    ctx.writeAndFlush(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    System.out.println("Closing connection for client - " + ctx);
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    System.out.println("Client left - " + ctx);
    channels.remove(ctx.channel());
  }
}

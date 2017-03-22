package kr.jarvisk.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ChatServer {

    private int port;

    private ServerBootstrap bootstrap;

    private ChatHandler chatHandler = new ChatHandler();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public ChatServer(int port) {
        this.port = port;
        init();
    }

    private void init() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel
                                .pipeline()
                                .addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8))
                                .addLast(new ChatMessageCodec())
                                .addLast(chatHandler);

                    }
                });

    }

    public void start() {
        Channel channel = null;
        try {
            channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        String portArg1 = "9000";
        if ( args.length > 1 ) {
            portArg1 = args[ 0 ];
        }

        try {
            int port = Integer.parseInt(portArg1);
            new ChatServer(port).start();
        } catch (NumberFormatException e) {
            throw new RuntimeException("포트 번호가 올바르지 않습니다.", e);
        }
    }
}

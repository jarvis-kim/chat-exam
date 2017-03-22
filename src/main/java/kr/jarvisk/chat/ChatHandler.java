package kr.jarvisk.chat;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import kr.jarvisk.chat.message.Message;
import kr.jarvisk.chat.message.MessageHeader;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<Message> {

    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final AttributeKey<Integer> idKey = AttributeKey.newInstance("id");

    private final AtomicInteger connectionCount = new AtomicInteger(0);

    private final AtomicInteger userId = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connectionCount.incrementAndGet();
        int id = userId.incrementAndGet();

        ctx.channel().attr(idKey).set(id);
        ctx.writeAndFlush("user id : " + id + "\n");

        channels.writeAndFlush(id + " join \n");
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectionCount.decrementAndGet();
        ctx.channel().attr(idKey).remove();

        channels.remove(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        if ( MessageHeader.ALL.equals(message.getHeader().getCommand()) ) {
            channels.writeAndFlush(ctx.channel().attr(idKey).get() + " : " + message.getContent());
        } else if ( MessageHeader.TARGET.equals(message.getHeader().getCommand()) ) {
            int idOfPosition = message.getContent().indexOf(" ");
            String targetId = message.getContent().substring(0, idOfPosition);
            String content = message.getContent().substring(idOfPosition, message.getContent().length());

            channels.stream().filter(i -> targetId.equals(String.valueOf(i.attr(idKey).get())))
                    .forEach(c -> c.writeAndFlush(c.attr(idKey).get() + " : " + content));
        } else if ( MessageHeader.EXIT.equals(message.getHeader().getCommand()) ) {
            ctx.disconnect();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

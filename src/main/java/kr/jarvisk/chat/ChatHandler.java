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

    private final AttributeKey<String> idKey = AttributeKey.newInstance("id");

    private final AtomicInteger connectionCount = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connectionCount.incrementAndGet();
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
        if ( !channels.contains(ctx.channel()) ) {
            if ( MessageHeader.JOIN.equals(message.getHeader().getCommand()) ) {
                String userId = message.getContent().replace("\r\n\n","");
                boolean hasUserId = channels.stream().filter(channel -> channel.attr(idKey).get().equals(userId)).findFirst().isPresent();
                if ( hasUserId ) {
                    ctx.writeAndFlush("exist user id : " + userId + "\n");
                    return;
                }

                ctx.channel().attr(idKey).set(userId);
                channels.writeAndFlush(userId + " join \n");
                channels.add(ctx.channel());
            }
        } else {
            if ( MessageHeader.ALL.equals(message.getHeader().getCommand()) ) {
                channels.writeAndFlush(ctx.channel().attr(idKey).get() + " : " + message.getContent());
            } else if ( MessageHeader.TARGET.equals(message.getHeader().getCommand()) ) {
                int idOfPosition = message.getContent().indexOf(" ");
                String targetId = message.getContent().substring(0, idOfPosition);
                String content = message.getContent().substring(idOfPosition, message.getContent().length());

                channels.stream().filter(i -> targetId.equals(String.valueOf(i.attr(idKey).get())))
                        .forEach(c -> c.writeAndFlush(ctx.channel().attr(idKey) + " -> " + c.attr(idKey).get() + " : " + content));
            } else if ( MessageHeader.EXIT.equals(message.getHeader().getCommand()) ) {
                ctx.disconnect();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

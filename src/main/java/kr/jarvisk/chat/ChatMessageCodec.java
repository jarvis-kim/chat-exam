package kr.jarvisk.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import kr.jarvisk.chat.message.Message;
import kr.jarvisk.chat.message.MessageHeader;

import java.util.List;

public class ChatMessageCodec extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext ctx, String text, List<Object> out) throws Exception {
        String command = text.substring(0, 1);
        String content = text.length() > 2
                ? text.substring(2, text.length())
                : "";

        MessageHeader header = new MessageHeader(command);
        Message message = Message.builder()
                .header(header)
                .content(content)
                .build();

        out.add(message);
    }
}

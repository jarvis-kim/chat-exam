package kr.jarvisk.chat.message;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Message {

    @Getter
    private MessageHeader header;

    private String content;

    public String getContent() {
        return content + "\n";
    }

}

package kr.jarvisk.chat.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class MessageHeader {

    public static final String JOIN = "J";

    public static final String ALL = "A";

    public static final String TARGET = "T";

    public static final String EXIT = "E";

    private String command;

}

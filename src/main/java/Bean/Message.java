package Bean;

import lombok.Data;

@Data
public class Message {

    private String messageId;
    private String sender;
    private String receiver;
    private String time;
    private String content;
    private String type;

    public boolean equals(Message message){
        if (!messageId.equals(message.getMessageId())){return false;}
        if (!sender.equals(message.getSender())){return false;}
        if (!receiver.equals(message.getReceiver())){return false;}
        return true;
    }
}

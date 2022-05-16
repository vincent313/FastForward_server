package Bean;

import lombok.Data;
//对客户端请求的返回
@Data
public class ReplayMessage {
    private String MessageId;
    private String Content;
}

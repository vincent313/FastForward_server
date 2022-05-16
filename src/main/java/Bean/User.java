package Bean;
import lombok.Data;

@Data
public class User {
    private String userName;
    private String hashValue;
    private String emailAddress;
    private String nickName;

    private String profilePicture;
    private String location;
    private String personalDescription;
    private String rsaPublicKey;

}

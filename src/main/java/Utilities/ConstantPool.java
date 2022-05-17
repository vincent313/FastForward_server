package Utilities;

public class ConstantPool {
   public static final int WEBSOCKET_PORT=1102;
   public static final String REDIS_ADDRESS="localhost";
   public static final int REDIS_PORT=6379;
   public static final String HIBERNATE_CONFIG_PATH="hibernate.cfg.xml";
   public static final String TYPE="TYPE";
   public static final String MESSAGE_ID="MID";
   public static final String CONTENT="CONTENT";

   public static final String TYPE_SIGNUP="1";
   public static final String TYPE_LOGIN="2";
   public static final String TYPE_GETUSERINFO="3";
   public static final String TYPE_ACK="5";
   public static final String TYPE_CONTACT="6";
   public static final String TYPE_MESSAGE="4";

   public static final String CONTACT_REQUEST="1";
   public static final String CONTACT_APPROVE="2";
   public static final String CONTACT_DECLINE="3";
   public static final String CONTACT_DELETE="4";

   public static final String SIGNUP_FAIL="SF";//User name already exists
   public static final String SIGNUP_SUCCESS="SS";//Registration Success
   /*
   * Sign in
   * {"type":"singup","user":"zhiyong","pas":"cist321"}
   * */
}

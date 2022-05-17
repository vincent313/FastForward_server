package MyConnection;

import Bean.ReplayMessage;
import Service.*;
import lombok.Data;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import Utilities.*;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

@Data
public class MyConnection extends WebSocketServer{
    private String username=null;
    private String AESKEY=null;
    private WebSocket wb=null;
    private String RsaPubKey=null;
    private Long LstMsgTime=null;
    private boolean login=false;


    public MyConnection() {
       super(new InetSocketAddress(ConstantPool.WEBSOCKET_PORT));
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        this.wb=conn;
        this.LstMsgTime=new Date().getTime();
        Online_UserManagement_Service.addConnectedUser(this);
        System.out.println("Current Connection number:"+ Online_UserManagement_Service.getConnectedUserNumber());

    }


    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Online_UserManagement_Service.removeConnection(this);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        this.LstMsgTime=new Date().getTime();//on message update last msg time
        System.out.println(message);
        if(AESKEY==null){
            //如果还没收到AES key,则第一条消息存入AESkey.如果第一条消息不是AESkey,则回复未收到aes key
        }
         //客户端发来的注册登录请求用户资料消息格式为 <TYPE:> <MID:><CON:>
        //客户端发来需要转发的消息格式为<TYPE:><CON:message>
        //服务器响应,返回客户端的消息都为<TYPE:XXX><CONTENT:>




        Map<String,String> map=JsonTool.stringToMap(message);
        String type=map.get(ConstantPool.TYPE);

        switch (type){
            /*?对所有消息验证签名?*/
            /**登录和注册不需要验证签名,其他都需要验证签名**/

            /**发来消息是<TYPE:1> <MID:XXXXXXXXX><CONTENT:USER_IN_JSON>
            回复消息 -->  <TYPE:RE_SIGNUP><CONTENT:ReplayMessage><SIGNATURE> **/
            case ConstantPool.TYPE_SIGNUP:

               Map map1=SignUp_Service.SignUp(map);
               sendMessage(map1);
               return;


            /**发来消息是<TYPE:2> <MID:XXXXXXXXX><CONTENT:MAP<USER,XXXX> <PASS,XXXX>><SIGNATURE>
             回复消息 -->  <TYPE:RE_LOGIN><CONTENT:ReplayMessage><SIGNATURE>
             登录成功或者失败,在Login_Service内处理
             **/
            case ConstantPool.TYPE_LOGIN:
                LogIn_Service.LogIn(map,MyConnection.this);
                return;



            /**发来消息是<TYPE:3> <MID:XXXXXXXXX><CONTENT:USERNAME_STRING><SIGNATURE>
             回复消息 -->  <TYPE:RE_GETUSER_FAILED><CONTENT:ReplayMessage><SIGNATURE>
                          OR<TYPE:RE_GETUSER_SUC><CONTENT:ReplayMessage><SIGNATURE>   **/
            case ConstantPool.TYPE_GETUSERINFO:
                if (username==null){
                    return;
                }
                Map map2= GetUserInfo_Service.getUserInfo(map);
                sendMessage(map2);
                return;



             /**发来消息是<TYPE:4><CONTENT:MESSAGE><SIGNATURE>
              * 回复发送方消息是<TYPE:RE_MESSAGE><CONTENT:ReplayMessage><SIGNATURE>
              * 转发给接收方消息是 <TYPE:4><CONTENT:Message><SIGNATURE>   **/
            case ConstantPool.TYPE_MESSAGE:
                Message_Service.process_message(MyConnection.this,map);
                return;

            /**发来消息是<TYPE:5><CONTENT:ReplayMessage><SIGNATURE>
             不需要回复  **/
            case ConstantPool.TYPE_ACK:
                ReplayMessage replayMessage=JsonTool.stringToReplayMessage(map.get("CONTENT"));
                ACK_Service.removeMessage(replayMessage.getMessageId());
                return;

            /**发来消息是<TYPE:6><CONTENT:Message><SIGNATURE>*
             * * 回复发送方消息是<TYPE:RE_FRI_REQ><CONTENT:ReplayMessage><SIGNATURE>
             *   转发给接收方消息是 <TYPE:6><CONTENT:Message><SIGNATURE>
             *     **/
            case ConstantPool.TYPE_CONTACT:
                if (username==null){return;}
                Contacts_Manage_Service.contactManagement(MyConnection.this,map);
        }
            return;


    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println(ex.getMessage());
    }

    public void sendMessage(Map m){
        String s=JsonTool.objectToString(m);
        System.out.println("服务器发送消息:"+s);
        wb.send(s);
        /*?发送的时候全部加密?*/
        /*?发送的时候全部加签名?*/
    }
    public boolean getLogin(){
        return login;
    }
    public void vetifySignature(){

    }
}

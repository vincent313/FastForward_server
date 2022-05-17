package Service;

import Bean.Message;
import Bean.ReplayMessage;
import Utilities.ConstantPool;
import Utilities.JsonTool;
import org.java_websocket.WebSocket;
import MyConnection.MyConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogIn_Service {

    public static void LogIn(Map<String,String> map,MyConnection myconnection ){
        /**
         * 发来消息是<TYPE:2> <MID:XXXXXXXXX><CONTENT:MAP<USERNAME,PASSWORD>>
            this str -->  <TYPE:RE_SIGNUP><CONTENT:ReplayMessage> Replaymessage.Content="LS" or "LF" loginsuccess or login fail
         * 1.取得message id,取得content
         * 2.取得username 和 password in string
         * 3.验证失败,生成replaymessage,回复失败,return
         * 3.验证成功
         *  (1)生成replaymessage,回复成功
         *  (2)加入在线用户池
         *  (3)调用方法将历史信息推送给用户
         **/

        String messageId=map.get(ConstantPool.MESSAGE_ID);
        String s=map.get(ConstantPool.CONTENT);
        Map<String,String> loginInfo= JsonTool.stringToMap(s);
        String username=loginInfo.get("USER");
        String password=loginInfo.get("PASS");
        boolean flag=Database_Service.vetifyUser(username,password);

        //新建回复的Map
        Map<String,String> replayMap=new HashMap<String,String>();
        //标明回复类型
        replayMap.put("TYPE","RE_SIGNUP");
        //新建回复消息,根据登录结果不同,放入成功或失败,使用myconnection.send(),加上签名后加密发送
        ReplayMessage replayMessage=new ReplayMessage();
        replayMessage.setMessageId(messageId);

        if(flag){
            myconnection.setUsername(username);//设置好myconnection中的username
            myconnection.setLogin(true);
            Online_UserManagement_Service.addOnLineUser(username,myconnection);//添加在线用户到几个表内
            replayMessage.setContent("LS");
            replayMap.put("CONTENT",JsonTool.objectToString(replayMessage));
            myconnection.sendMessage(replayMap);//回复登录成功
            myconnection.setRsaPubKey(Database_Service.getUserRsaKey(username));//设置好RSAkey 用于验证
            //回复客户端登录成功了以后,拉取消息发送给客户,到message_service里面处理(转map格式,及加入等待ack池)
            List<Message> list=Database_Service.loadOffLineMsg(username);//拉取对应客户的消息列表
            for(Message message:list){          //列表内消息推送给用户
                if(message.getType()==null){Message_Service.sendMsgToOnlineUser(message, ConstantPool.TYPE_MESSAGE);}
            else{Message_Service.sendMsgToOnlineUser(message, ConstantPool.TYPE_CONTACT);}
            }

        }else{
            replayMessage.setContent("LF");
            replayMap.put("CONTENT",JsonTool.objectToString(replayMessage));
            myconnection.sendMessage(replayMap);
        }
    }
}

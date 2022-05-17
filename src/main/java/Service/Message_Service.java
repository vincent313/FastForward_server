package Service;

import Bean.Message;
import Bean.ReplayMessage;
import MyConnection.MyConnection;
import Utilities.ConstantPool;
import Utilities.JsonTool;
import org.java_websocket.WebSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message_Service {

    public static void process_message(MyConnection myConnection, Map<String,String> map){

        /**发来消息是<TYPE:4><CONTENT:MESSAGE><SIGNATURE>
         * 回复发送方消息是<TYPE:RE_MESSAGE><CONTENT:ReplayMessage><SIGNATURE>
         * 转发接收方消息是 <TYPE:4><CONTENT:Message><SIGNATURE>   **/

        /**
         * 1.转成message 格式
         * 3.判断是否互相为好友 (否,消息丢弃,返回失败消息)
         * 4.加入时间戳
         * 5.判断对方是否在线
         * 6.在线发送
         * 7.不在线丢入历史记录表,用户上线后拉取推送
         * **/

        //Message m= JsonTool.stringToMessage(s);
        Message m=JsonTool.stringToMessage(map.get("CONTENT"));
         m.setTime(String.valueOf(new Date().getTime()));


        /***判断好友并回复消息
        * 回复发送方消息是<TYPE:RE_MESSAGE><CONTENT:ReplayMessage><SIGNATURE>
        *成功 ReplayMessage.content="MS"
        * 失败 ReplayMessage.content="MF" 并返回
        */

        boolean flag=Contacts_Manage_Service.verifyContact(m.getSender(),m.getReceiver());
        ReplayMessage rm=new ReplayMessage();
        rm.setMessageId(m.getMessageId());

        if (flag){
            rm.setContent("MS");

        }else{
            rm.setContent("MF");
        }
        //回复成功或者失败消息
        Map<String,String> m1=new HashMap<String,String>();
        m1.put("TYPE","RE_MESSAGE");
        m1.put("CONTENT",JsonTool.objectToString(rm));
        System.out.println(JsonTool.objectToString(rm));
        myConnection.sendMessage(m1);

        //如果发件人不是收件人好友,发送信息错误后,返回
        if (!flag){return;}

        sendMsgToOnlineUser(m, ConstantPool.TYPE_MESSAGE);
        }

        //有两种消息类型,一种是普通消息,一种是添加删除好友消息.
        public static void sendMsgToOnlineUser(Message message,String messagetype){
                /****
                 * 1.get receiver
                 * 2.if receiver off line,save to database (off line message)
                 * 3.if receiver online,new map,put type content .... then send message through MyConnection
                 * ***/
                String receiver=message.getReceiver();
                if (Online_UserManagement_Service.userIsOnline(receiver)){
                    ACK_Service.addMessage(message);
                    Map<String,String> map1=new HashMap<String,String>();//新建map
                    map1.put("TYPE",messagetype);//放入type
                    map1.put("CONTENT",JsonTool.objectToString(message));//放入转换为string的Message对象
                    Online_UserManagement_Service.getUserConnection(message.getReceiver()).sendMessage(map1);//找到收件人的connection并发送,发送端加上签名
                }else{
                    //如果不在线则放入数据库,等用户上线拉取
                    //!!如果消息在等待ack表内!!移除消息,不在则返回
                    ACK_Service.removeMessage(message.getMessageId());
                    Database_Service.saveOfflineMsg(message);

                }



        }

}

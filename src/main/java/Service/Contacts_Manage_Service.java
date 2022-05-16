package Service;

import Bean.Message;
import Bean.ReplayMessage;
import MyConnection.MyConnection;
import Utilities.ConstantPool;
import Utilities.JsonTool;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Contacts_Manage_Service {
   private static Jedis jedis = new Jedis(ConstantPool.REDIS_ADDRESS,ConstantPool.REDIS_PORT);

    private static void addContanct(String user,String contact){
        jedis.connect();
        jedis.sadd(user,contact);
        jedis.sadd(contact,user);
        jedis.close();
        }

//验证发信人是否在收信人的好友列表里
    public static boolean verifyContact(String messageSender,String messageReceiver){
    return jedis.sismember(messageReceiver,messageSender);
    }


    /**发来消息是<TYPE:6><CONTENT:Message><SIGNATURE>*
     * * 回复发送方消息是<TYPE:RE_FRI_REQ><CONTENT:ReplayMessage><SIGNATURE>
     *   转发给接收方消息是 <TYPE:6><CONTENT:Message><SIGNATURE>
     *     **/

    public static void contactManagement(MyConnection myConnection ,Map<String,String> map){
        Message message= JsonTool.stringToMessage(map.get("CONTENT"));
        generateFriendRequestReplayMessage(myConnection,message);//先对发送方回复已收到消息,消除发送方ack
        String type=message.getType();

        switch (type){
            /*** 根据Message.type 来决定如何处理,
             * CONTACT_REQUEST=1  添加好友请求
             * CONTACT_APPROVE="2" 同意好友请求
             * CONTACT_DECLINE="3" 拒绝好友请求
             * CONTACT_DELETE="4" 删除好友
             * ***/
            /***回复全是 <TYPE:RE_FRI_REQ><CONTENT:ReplayMessage>,只是用于客户端消除ack***/
            case ConstantPool.CONTACT_REQUEST: //1
                friendsRequest(message);
                return;
            case ConstantPool.CONTACT_APPROVE: //2
                friendsRespond(message,true);

            case ConstantPool.CONTACT_DECLINE: //3
                friendsRespond(message,false);

            case ConstantPool.CONTACT_DELETE: //4
                deleteFriends(message);
        }
    }

    private static void friendsRequest(Message message){

        //放入验证消息列表,用于验证该好友请求是存在的, 放入redis数据库,key为message id,value为message in string
        jedis.connect();
        jedis.set(message.getMessageId(),JsonTool.objectToString(message));
        jedis.close();
        //转发给被请求方 好友请求消息 格式为<TYPE:6><CONTENT:Message><SIGNATURE>
        Message_Service.sendMsgToOnlineUser(message,ConstantPool.TYPE_CONTACT); //message service处理.-->转换格式,加入ack,取得收件人发送消息
    }

    private static void friendsRespond(Message message,boolean flag){
        jedis.connect();
        //如果请求不存在(message id),或者好友请求的发送方接收方与记录不相符,返回
        if(!jedis.exists(message.getMessageId())){return;}
        if(!message.equals(jedis.get(message.getMessageId()))){return;}
        jedis.del(message.getMessageId());
        jedis.close();
        String s1=message.getSender();
        String s2=message.getReceiver();

       //如果是通过好友,添加到好友列表
        if (flag){addContanct(s1,s2);}

        //将发送人(请求方)  收件人(接收方)调换顺序后,发送消息通知给添加好友的请求方
        message.setSender(s2);
        message.setReceiver(s1);
        Message_Service.sendMsgToOnlineUser(message,ConstantPool.TYPE_CONTACT);//message service处理.-->转换格式,加入ack,取得收件人发送消息
    }

    private static void deleteFriends(Message message){
        //sender 想要删除 receiver,查看receiver是否在好友列表里,是的话删除.
        jedis.connect();
        if(jedis.sismember(message.getSender(),message.getReceiver())){
            jedis.srem(message.getSender(),message.getReceiver());
        }

        jedis.close();
    }

    private static void generateFriendRequestReplayMessage(MyConnection myConnection ,Message message){
        /***回复全是 <TYPE:RE_FRI_REQ><CONTENT:ReplayMessage>,只是用于客户端消除ack***/
        message.setTime(String.valueOf(new Date().getTime())); //设置请求收到时间(服务器时间)
        Map<String,String> replayMap=new HashMap<String,String>();//准备replay map
        replayMap.put("TYPE","RE_FRI_REQ");//放入回复type
        ReplayMessage replayMessage=new ReplayMessage();
        replayMessage.setMessageId(message.getMessageId());//设置回复replaymessage 的message id
        replayMessage.setContent(String.valueOf(new Date().getTime()));//放入时间,无实际作用,当成生成签名的随机值
        replayMap.put("CONTENT",JsonTool.objectToString(replayMessage));
        myConnection.sendMessage(replayMap); //回复 好友请求的发起方(告知请求已收到)
    }
}

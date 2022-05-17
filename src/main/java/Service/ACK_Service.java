package Service;

import Bean.Message;
import Utilities.ConstantPool;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ACK_Service implements Runnable{

volatile private static Map<String, Message> waitForAckList=new HashMap<String, Message>();

public static void addMessage(Message m){
    waitForAckList.put(m.getMessageId(),m);
}

public static boolean contianMessageID(String messageid){
    return waitForAckList.containsKey(messageid);
}

public static void removeMessage(String messageid){ //当收到客户端发来的ack信息的时候,根据message id来删除等待ack列表内的message
    if(contianMessageID(messageid)){
        waitForAckList.remove(messageid);
    }
}

//如果用户还没有发送ack message确认,并且用户下线,那么将没有收到ack的消息从列表移除,并放入数据库,等待用户再次上线时推送
public static void moveMessageToDatabase(String user){
    if(!waitForAckList.isEmpty()){
        for(String key:waitForAckList.keySet()){
            System.out.println(waitForAckList.get(key).getReceiver());
            System.out.println(user);
            if (waitForAckList.get(key).getReceiver().equals(user)){
                Message mg=waitForAckList.get(key);
                Database_Service.saveOfflineMsg(mg);
                waitForAckList.remove(key);
            }
        }

    }
}

    @SneakyThrows
    @Override
    public void run() {
        while(true){
            Thread.sleep(15000);//每15秒轮询一次,如果没有收到ack则重发
            System.out.println("Current wait for ACK size="+waitForAckList.size());
            if(!waitForAckList.isEmpty()){
                for(String key:waitForAckList.keySet()){
                    Message_Service.sendMsgToOnlineUser(waitForAckList.get(key), ConstantPool.TYPE_MESSAGE);
                }

            }
        }
        }

    public static void startACK_service(){
    ACK_Service ack=new ACK_Service();
    Thread thread=new Thread(ack);
    thread.start();
    }
}



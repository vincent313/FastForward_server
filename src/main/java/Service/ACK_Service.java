package Service;

import Bean.Message;
import Utilities.ConstantPool;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ACK_Service implements Runnable{

private static Map<String, Message> waitForAckList=new HashMap<String, Message>();

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

    @SneakyThrows
    @Override
    public void run() {
        while(true){
            Thread.sleep(15000);//每十秒轮询一次,如果没有收到ack则重发
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



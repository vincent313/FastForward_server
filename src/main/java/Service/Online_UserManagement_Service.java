package Service;


import MyConnection.MyConnection;
import lombok.SneakyThrows;
import org.java_websocket.WebSocket;

import java.util.*;

public class Online_UserManagement_Service implements Runnable {

    private Online_UserManagement_Service(){}
    //total connection number
    private static long connectedUserNumber=0;

    //list of online user
    private static Map<String,String> onLineUserMap=new HashMap<String,String>();//username,RSAKEY
    private static Map<String, MyConnection> onLineUserConnection=new HashMap<String,MyConnection>();//userName,Connection
    private static Map<MyConnection, String> onLineConnectionUser=new HashMap<MyConnection,String>();//Connectionuser,username
    private static Map<MyConnection,Long> Connection_Last_MSG_Time=new HashMap<MyConnection,Long>();//last message time
    public static long getConnectedUserNumber() {
        return connectedUserNumber;
    }

    public static void addConnectedUser(){
        connectedUserNumber++;
    }

    public static void decConnectedUser(){
        connectedUserNumber--;
    }

    public static void addOnLineUser(String userName,MyConnection myConnection){
        onLineUserMap.put(userName,Database_Service.getUserRsaKey(userName));//username,RSAKEY对应表
        onLineUserConnection.put(userName,myConnection);//userName,Connection 对应表
        onLineConnectionUser.put(myConnection,userName);//Connectionuser,Name 对应表
    }
//
    public static void removeOnLineUser(String userName){
        onLineUserMap.remove(userName);//username,RSAKEY
        onLineUserConnection.remove(userName);//userName,Connection
    }

    public static String getUserRsaPublicKey(String userName){
        String key=onLineUserMap.get(userName);
        if (key==null){
            key=Database_Service.getUserRsaKey(userName);
        }
        return key;
    }

    public static MyConnection getUserConnection(String userName){
        return onLineUserConnection.get(userName);
    }

    public static boolean userIsOnline(String userName){
        return onLineUserMap.containsKey(userName);
    }

    public synchronized static void updateConnectionLastMsgTime(MyConnection connection){
        Connection_Last_MSG_Time.put(connection, new Date().getTime());
    }

    @SneakyThrows
    @Override
    public void run() {
        while(true){
            Thread.sleep(5000);

            ArrayList<MyConnection> myConnectionslist=new ArrayList<MyConnection>();
            if(!Connection_Last_MSG_Time.isEmpty()){
                    for(MyConnection key:Connection_Last_MSG_Time.keySet()){
                        long l=(new Date().getTime()-Connection_Last_MSG_Time.get(key));//计算连接最后一次发送消息的时间
                        if(l>10000){
                        myConnectionslist.add(key);

                        }
                    }
            }
            for(MyConnection m:myConnectionslist){
                //removeConnection(m);
                m.getWb().close();//关闭websocket连接
            }
            }
        }


    public static void startOnlineUserManage(){
        Online_UserManagement_Service m=new Online_UserManagement_Service();
        Thread thread=new Thread(m);
        thread.start();
    }

    public static void removeConnection(MyConnection connect){

        Connection_Last_MSG_Time.remove(connect);//移除

        boolean flag=onLineConnectionUser.containsKey(connect);
        if (flag){
            String username=onLineConnectionUser.get(connect);//if connection login as user,get username
            onLineUserConnection.remove(username);//remove (key)user<->(connection)connection
            onLineUserMap.remove(username);//remove (key)user<->(value)RSAkey
        }
         onLineConnectionUser.remove(connect);
        decConnectedUser();
        System.out.println("Current Connection number:"+ getConnectedUserNumber());
    }
}



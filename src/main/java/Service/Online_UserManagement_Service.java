package Service;


import MyConnection.MyConnection;
import lombok.Lombok;
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
   private static ArrayList <MyConnection> connectionList=new ArrayList<MyConnection>();//last message time
    public static long getConnectedUserNumber() {
        return connectedUserNumber;
    }

    public static void addConnectedUser(MyConnection connection){
        connectedUserNumber++;
        connectionList.add(connection);
    }

    public static void decConnectedUser(){
        connectedUserNumber--;
    }

    public static void addOnLineUser(String userName,MyConnection myConnection){
        onLineUserMap.put(userName,Database_Service.getUserRsaKey(userName));//username,RSAKEY对应表
        onLineUserConnection.put(userName,myConnection);//userName,Connection 对应表

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

    @SneakyThrows
    @Override
    public void run() {
        while(true){
            Thread.sleep(5000);
            for(MyConnection m:connectionList){
                if ((new Date().getTime()-m.getLstMsgTime())>120000)
                    m.getWb().close();
            }
            }
        }


    public static void startOnlineUserManage(){
        Online_UserManagement_Service m=new Online_UserManagement_Service();
        Thread thread=new Thread(m);
        thread.start();
    }

    public static void removeConnection(MyConnection connect){
        boolean flag=connect.getLogin();
        if (flag){
            String username=connect.getUsername();
            connect.setLogin(false);
            ACK_Service.moveMessageToDatabase(username);
            onLineUserConnection.remove(username);//remove (key)user<->(connection)connection
            onLineUserMap.remove(username);//remove (key)user<->(value)RSAkey
        }
        connectionList.remove(connect);
        decConnectedUser();
        connect.setUsername(null);
        connect.setAESKEY(null);
        connect.setLstMsgTime(null);
        connect.setRsaPubKey(null);
        System.out.println("Current Connection number:"+ getConnectedUserNumber());
    }
}



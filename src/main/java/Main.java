import MyConnection.MyConnection;
import Service.ACK_Service;
import Service.Online_UserManagement_Service;
import org.java_websocket.WebSocketImpl;
import Utilities.ConstantPool;

public class Main {
    public static void main(String[] args) {
        System.out.println("server on line, port "+ConstantPool.WEBSOCKET_PORT);
        System.out.println("Client generate and using new AES everytime when client login");
        WebSocketImpl.DEBUG = false;
        MyConnection c=new MyConnection();
        c.start();

        Online_UserManagement_Service.startOnlineUserManage();
        ACK_Service.startACK_service();

    }
}

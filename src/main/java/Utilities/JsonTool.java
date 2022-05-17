package Utilities;
import Bean.Message;
import Bean.ReplayMessage;
import Bean.User;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class JsonTool {

    private JsonTool() {
    }
    @SneakyThrows
    public static Map<String,String> stringToMap(String s){
        Gson gson=new Gson();
        Map<String,String> messageMap=gson.fromJson(s,Map.class);
        return messageMap;
}
    public static String objectToString(Object o){
        Gson gson=new Gson();
        String string =gson.toJson(o);
        return string;
    }
    @SneakyThrows
    public static User getUser(String s){
        Gson gson=new Gson();
        User user=gson.fromJson(s,User.class);
        return user;
    }
    @SneakyThrows
    public static Message stringToMessage(String s){
        Gson gson=new Gson();
        Message m=gson.fromJson(s,Message.class);
        return m;
    }
    @SneakyThrows
    public static ReplayMessage stringToReplayMessage(String s){
        Gson gson=new Gson();
        ReplayMessage rm=gson.fromJson(s,ReplayMessage.class);
        return rm;
    }
}

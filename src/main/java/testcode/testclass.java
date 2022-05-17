package testcode;
import Bean.*;
import Bean.User;
import Utilities.ConstantPool;
import Utilities.JsonTool;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.google.gson.Gson;
import org.junit.Test;
import Service.*;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class testclass {


    public void beforework(){
    }
@Test
public void test_re(){
        test_redis t=new test_redis();
        t.connect_redis();
}

@Test
//使用GSON
public void testuser(){
    User a=new User();
    a.setUserName("vincent313");
    a.setEmailAddress("vincent313@foxmail.com");
    a.setNickName("zhiyong");
    a.setRsaPublicKey("24sdfqwrwer");
    a.setLocation("longyan");
    a.setPersonalDescription("cooking man");
    //Gson转换和setter getter 构造器无关
    //准备gson 对象
    Gson gson = new Gson();
    //对象转成string
    String content=gson.toJson(a);
    //新建一个map
    Map<String,String> map=new HashMap<>();
    //type 写入类型
    map.put("TYPE",ConstantPool.TYPE_SIGNUP);
    //content 写入对象转的string
    map.put("CONTENT",content);
    String fi=gson.toJson(map);
    System.out.println(fi);

    //收到string,先转map
    Map<String,String> messageMap=gson.fromJson(fi,Map.class);
    //取出对象string
    String objectJson=messageMap.get("CONTENT");
    //根据type类型,将content string直接转成对象
    User zhiyong=gson.fromJson(objectJson,User.class);
    System.out.println(zhiyong.getUserName());
}


@Test
    public void signupTest(){
    User a=new User();
    a.setUserName("vincent313");
    a.setEmailAddress("vincent313@foxmail.com");
    a.setNickName("zhiyong");
    a.setRsaPublicKey("24sdfqwrwer");
    a.setLocation("longyan");
    a.setPersonalDescription("cooking man");
    a.setHashValue("thisisapasswordhash");
    a.setProfilePicture("personalfilepicture.com/link");

   Database_Service.SignUpToDB(a);


    }
@Test
    public void usertojson(){
        System.out.println(Database_Service.getUserInfo("zhiyongjian"));
//{"userName":"Brian","hashValue":"asdfafasdfasdfasdf",emailAddress":"vincent313@foxmail.com","nickName":"baba","location":"zhiyongjian","rsaPublicKey":"asjdfhakjsdfhlasdhfljhfa1231dfgkjasfkl"}
    }

@Test
    public void testMessage(){
       Message m=new Message();
        m.setMessageId("12333313");
        m.setReceiver("brian");
        m.setType("message");
        m.setSender("zhiyong");
        m.setTime("1331-32-3434");
        m.setContent("somesilly message");

    Database_Service.saveOfflineMsg(m);

    m.setMessageId("12341123334313");
    m.setReceiver("brian");
    m.setType("friendRequest");
    m.setSender("zhiyong");
    m.setTime("1331-32-3434");
    m.setContent("somesilly message");
    Database_Service.saveOfflineMsg(m);
}

@Test
    public void testOfflinemessage(){
        List<Message> list=Database_Service.loadOffLineMsg("brian");
        for(Message m:list){
            System.out.println(m.getMessageId());
        }

}
@Test
public void redisttest(){
    Jedis jedis = new Jedis(ConstantPool.REDIS_ADDRESS,ConstantPool.REDIS_PORT);
    jedis.connect(); //连接
    jedis.connect();
    jedis.sadd("zhiyong","43");//添加key
    jedis.sadd("zhiyong","bria4n");
    jedis.sadd("zhiyong","th4444st");
    jedis.srem("zhiyong", "bria4n");//删除某个值
    Set<String> s=jedis.smembers("zhiyong");//获得key下的值
    System.out.println(s);
    boolean flag=jedis.sismember("zhiyong", "bria4n");
    System.out.println(flag);
    //jedis.flushAll(); //清空所有的key
    jedis.disconnect(); //断开连接

    System.out.println(jedis.ping());
}
@Test
    public void testequals(){
        Message m1=new Message();
        m1.setMessageId("13");
        m1.setSender("123");
        m1.setReceiver("123");

        Message m=new Message();
        m.setMessageId("123");
        m.setSender("123");
        m.setReceiver("123");
        System.out.println(m1.equals(m));
    }

    @SneakyThrows
    @Test
    public void signintest(){
       User a=new User();
        a.setUserName("vincent313");
        a.setEmailAddress("vincent313@foxmail.com");
        a.setNickName("zhiyong");
        a.setRsaPublicKey("24sdfqwrwer");
        a.setLocation("longyan");
        a.setPersonalDescription("cooking man");
        a.setHashValue("thisisapasswordhash");
        a.setProfilePicture("personalfilepicture.com/link");
        Map<String,String> map1=new HashMap<String,String>();
        map1.put("TYPE","1");
        map1.put("MID","signupmessageID001");
        map1.put("CONTENT",JsonTool.objectToString(a));

        String result=JsonTool.objectToString(map1);
        File file=new File("./result.txt");
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw=new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw=new BufferedWriter(fw);
        bw.write(result);
        bw.close();
    }
    @Test
public void loginintest(){
        Map<String,String> map=new HashMap<String,String>();
        Map<String,String> map1=new HashMap<String,String>();
        map1.put("USER","thisisausername");
        map1.put("PASS","thisisapasswordhash");
        map.put("TYPE","2");
        map.put("MID","loginmessage0001");
        map.put("CONTENT",JsonTool.objectToString(map1));
        String s=JsonTool.objectToString(map);
        System.out.println(s);
}
@Test
public void getuserinfotest(){
    Map<String,String> map=new HashMap<String,String>();
    map.put("TYPE","6");
    Message message=new Message();
    message.setMessageId("friendsrequest001");
    message.setType("1");
    message.setSender("zhiyong313");
    message.setReceiver("vincent313");
    message.setContent("tianjiahaoyoushenqing");
    map.put("CONTENT",JsonTool.objectToString(message));
    System.out.println(JsonTool.objectToString(map));

}
@Test
public void generateAckmessage(){
        ReplayMessage rm=new ReplayMessage();
        rm.setMessageId("friendsrequest001");
         Map<String,String> map=new HashMap<String,String>();
     map.put("TYPE","5");
    map.put("CONTENT", JsonTool.objectToString(rm));
    System.out.println(JsonTool.objectToString(map));
}

@Test
    public void generateMSG(){
    Message message=new Message();
    message.setMessageId("normalmessage001");
    message.setSender("zhiyong313");
    message.setReceiver("vincent313");
    message.setContent("first message test 000001");
    Map<String,String> map=new HashMap<String,String>();
    map.put("TYPE","4");
    map.put("CONTENT",JsonTool.objectToString(message));
    System.out.println(JsonTool.objectToString(map));
}
}



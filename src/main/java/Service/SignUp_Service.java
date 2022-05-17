package Service;
import Bean.User;
import Utilities.*;
import Bean.*;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Service {
    private SignUp_Service() {
    }

    //注册,输入是原始的map数据-><TYPE:> <MID:><CONTENT:>
    //获得con,转成user类型,放入database service里面去注册.
    // 返回的是成功或者失败的代码,通过这个代码生成ReplayMessage,设置好messageid 和 内容,转为string.
    // 再转成<TYPE:RE_SIGNUP><CONTENT:ReplayMessage>
    // 返回给connection
    public static Map SignUp(Map<String,String> map){
        String userString=map.get(ConstantPool.CONTENT);
        User user=JsonTool.getUser(userString);
        String s=Database_Service.SignUpToDB(user);// SF(fail) or SS(success)

        ReplayMessage rm=new ReplayMessage();
        rm.setContent(s);//Content:SS
        rm.setMessageId(map.get(ConstantPool.MESSAGE_ID));//MessageId:xxxxxxx
        String string=JsonTool.objectToString(rm);
        Map<String,String> m=new HashMap<>();
        m.put("TYPE","RE_SIGNUP");
        m.put("CONTENT",string);
        return m;
    }
}

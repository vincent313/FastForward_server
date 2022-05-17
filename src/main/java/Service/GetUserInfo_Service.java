package Service;

import Bean.ReplayMessage;
import Bean.User;
import Utilities.ConstantPool;
import Utilities.JsonTool;

import java.util.HashMap;
import java.util.Map;

public class GetUserInfo_Service {

    public static Map getUserInfo(Map<String,String> m){
        //输入格式 <TYPE:3> <MID:XXXXXXXXX><CONTENT:USERNAME_STRING>
        String messageid=m.get(ConstantPool.MESSAGE_ID);
        String username=m.get(ConstantPool.CONTENT);

        //从数据库查询,成功返回user的json格式,失败返回null
        String string=Database_Service.getUserInfo(username);

        Map<String,String> map=new HashMap<>();

        /**Get user Fail <TYPE:RE_GETUSER_FAILED><CONTENT:ReplayMessage>
         * Success <TYPE:RE_GETUSER_SUC><CONTENT:ReplayMessage>
         * 如果成功,ReplayMessage 的content封装user信息,如果失败,ReplayMessage里放失败提示
         * Replaymessage 转String 放入map
         * 将map转String返回
         * **/

        if (string==null){
            string = "User could not be found";
            map.put("TYPE","RE_GETUSER_FAILED");
        }else{
            map.put("TYPE","RE_GETUSER_SUC");
        }
        ReplayMessage rm=new ReplayMessage();
        rm.setMessageId(messageid);
        rm.setContent(string);

        map.put("CONTENT", JsonTool.objectToString(rm));
        return map;
    }
}

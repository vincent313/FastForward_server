package Service;

import Bean.Message;
import Bean.User;
import Utilities.ConstantPool;
import Utilities.JsonTool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class Database_Service {
   private static Configuration config=new Configuration().configure(ConstantPool.HIBERNATE_CONFIG_PATH);
   private static SessionFactory sessionFactory = config.buildSessionFactory();

    private Database_Service() {
    }

    //获得数据库session,先查找用户名是否存在,如果存在返回登录失败,如果不存在,注册并且返回注册成功.关闭session
    public synchronized static String SignUpToDB(User user){
        Session session=sessionFactory.openSession();
        User u=(User)session.get(User.class, user.getUserName());

        if(u!=null){
            return ConstantPool.SIGNUP_FAIL;
        }else{
            session.save(user);
            session.beginTransaction().commit();
            return ConstantPool.SIGNUP_SUCCESS;
        }
    }
    //通过用户名查找用户信息,
    public static String getUserInfo(String username){
        Session session=sessionFactory.openSession();
        User u=(User)session.get(User.class, username);
        session.close();
        if(u==null){
            return null;
        }
        else{
            u.setHashValue(null);
            return JsonTool.objectToString(u);
        }

    }

    public static boolean vetifyUser(String username,String password){
        /**
         * 1.get user from database
         * 2. if user exist and password correct return true
         * 3. else return false
        * */
        Session session=sessionFactory.openSession();
        User u=(User)session.get(User.class,username);
        session.close();
        if (u!=null&&u.getHashValue().equals(password)){
        return true;
        }
        return false;
    }

    public static String getUserRsaKey(String username){
        Session session=sessionFactory.openSession();
        User u=(User)session.get(User.class, username);
        session.close();
        return u.getRsaPublicKey();

    }
    /**
     * 用户离线的话,将需要发送给用户的信息存入数据库,等待用户上线后推送
     * **/
    public static void saveOfflineMsg(Message m){
        Session session=sessionFactory.openSession();
        session.save(m);
        session.beginTransaction().commit();
        session.close();
    }

    /**
     * 先根据用户名查找离线消息
     * 然后导出消息到list
     * 然后删除数据库的消息
    * **/
    public synchronized static List<Message> loadOffLineMsg(String username){
        Session session=sessionFactory.openSession();
        String hql="FROM Message m WHERE m.receiver= "+"\'"+username+"\'";
        Query query=session.createQuery(hql);
        List<Message> list=query.list();
        for(Message m:list){
            session.delete(m);
        }
        session.beginTransaction().commit();
        session.close();
        return list;
    }

//check user exists
    public static boolean userExists(String username){
        Session session=sessionFactory.openSession();
        User u=(User)session.get(User.class, username);
        session.close();
        if(u==null){
            return false;
        }else{
            return true;
        }
    }
}

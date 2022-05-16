package testcode;
import redis.clients.jedis.Jedis;
import Utilities.ConstantPool;
public class test_redis {

    void connect_redis(){
        Jedis jedis=new Jedis(ConstantPool.REDIS_ADDRESS,ConstantPool.REDIS_PORT);
        jedis.connect();
        jedis.disconnect();
        System.out.println(jedis.ping());

    }


}

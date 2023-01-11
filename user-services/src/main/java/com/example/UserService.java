package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String ,String> kafkaTemplate;

    private final  String REDIS_PREFIX_USER ="user::";

    private final String KAFKA_TOPIC ="wattle_create";

    public void createUser(UserRequest userRequest){
        User user=User.builder()
                .userName(userRequest.getUserName())
                .age(userRequest.getAge())
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .build();

        userRepository.save(user);

        //Redis
        saveInCache(user);

        //kafka
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userName",user.getUserName());
        String message=jsonObject.toString();
        kafkaTemplate.send("create_wallet",message);

    }

    //save inRedis for 12 hours
    public void saveInCache(User user){
        Map map=objectMapper.convertValue(user,Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_USER+user.getUserName(),map);
        redisTemplate.expire(REDIS_PREFIX_USER+user.getUserName(), Duration.ofHours(12));
    }

    public User getUserByUserName(String userName) throws Exception{


        Map map=redisTemplate.opsForHash().entries(REDIS_PREFIX_USER+userName);
        if(map==null || map.size()==0){
            User user=userRepository.findByUserName(userName);
            try{
                if(user==null) {
                    throw new UserNotFoundException();
                }
                saveInCache(user);
                return objectMapper.convertValue(map,User.class);
            }
            catch (Exception e){
                throw new UserNotFoundException();
            }
        }else{
            return objectMapper.convertValue(map,User.class);
        }
    }
    public List<User> getAllUsersByAge(int age) throws Exception{
        Map map=redisTemplate.opsForHash().entries(REDIS_PREFIX_USER+age);
        if(map==null || map.size()==0){
            List<User> list= userRepository.findAllByAge(age);
            try{
                if(list.size()==0){
                    throw new UserNotFoundException();
                }
                for(User user : list){
                    saveInCache(user);
                }
                List<User> result=new ArrayList<>();
                for(User user : list){
                    result.add(objectMapper.convertValue(map,User.class));
                }
                return result;
            }
            catch (Exception e){
                throw new UserNotFoundException();
            }
        }else{
            List<User> list= userRepository.findAllByAge(age);
            List<User> result=new ArrayList<>();
            for(User user : list){
                result.add(objectMapper.convertValue(map,User.class));
            }
            return result;
        }
    }
}

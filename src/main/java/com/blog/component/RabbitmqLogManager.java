package com.blog.component;

import com.blog.entity.User;
import com.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//ramq日志处理器
@Component
@Slf4j
public class RabbitmqLogManager {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserService userService;
    @Resource
    private AmqpAdmin amqpAdmin;

    //日志路径
    private final String LOG_PATH = "src/main/resources/ramq_log.txt";

    //更新日志
    public void updateLog(){
        log.info("正在进行ramq日志的更新");
        multiAddLog(readAndDelLog());
    }

    //添加日志内容
    public void writeLog(boolean isAdd,String w){
        log.info("正在进行ramq日志的内容添加：是否为消息添加={} 消息名称={}",isAdd,w);
        //文件不存在则直接创造新文件
        File f = new File(LOG_PATH);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //添加文件和数据(使用try-with-resource)
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(f,true))){
            //数据处理
            bw.write((isAdd ? "+" : "-") + " " + w + "\n");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //ramq崩溃修复
    public void fixRamq(){
        log.info("正在进行ramq的崩溃修复");
        //读取日志
        Map<String,String> map = readAndDelLog();
        //处理数据
        for(String uid : map.keySet()){
            //读取解封所剩时间
            String untilTime= map.get(uid);
            Long remainTime = Long.parseLong(untilTime) - System.currentTimeMillis();
            //如果是负数了直接解封
            if(remainTime < 0){
                User user = userService.getById(Long.parseLong(uid));
                user.setIsLock(0L);
                userService.updateById(user);
                continue;
            }
            //清空队列
            amqpAdmin.purgeQueue("UserForbidQueue");
            //如果还没有到解封时间则加入到消息队列
            rabbitTemplate.convertAndSend("ForbidExchange", "UserForbidRouting", uid + "::" + untilTime, message -> {
                message.getMessageProperties().setExpiration(String.valueOf(remainTime));
                message.getMessageProperties().setMessageId(uid.toString() + untilTime);
                return message;
            });
        }
        //将结果返回给日志
        multiAddLog(map);
    }

    //读取、删除日志，返回处理统计后的日志信息
    private Map<String,String> readAndDelLog(){
        Map<String,String> map = new HashMap<>();
        //文件不存在则直接跳过
        File f = new File(LOG_PATH);
        if(!f.exists()) return map;
        //读取文件和数据(使用try-with-resource)
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            //数据处理
            String s = null;
            while((s = br.readLine()) != null){
                String[] sp = s.split(" ");
                if(sp.length < 2 )continue;
                if(sp[0].equals("+")){
                    map.put(sp[1],sp[2]);
                }else{
                    map.remove(sp[1]);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //删除原文件
        if(f.exists())f.delete();
        //返回结果
        return map;
    }

    //批量添加日志信息
    private void multiAddLog(Map<String,String> msg){
        //文件不存在则直接创造新文件
        File f = new File(LOG_PATH);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //添加文件和数据(使用try-with-resource)
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
            //数据处理
            for(String s : msg.keySet()){
                bw.write("+ " + s + " " + msg.get(s) + "\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

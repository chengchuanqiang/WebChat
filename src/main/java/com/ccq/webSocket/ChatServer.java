package com.ccq.webSocket;

import com.ccq.pojo.User;
import com.ccq.utils.CommonDate;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ccq
 * @Description webSocket服务
 * @date 2017/12/16 17:31
 */
@ServerEndpoint(value="/chatServer/{userid}", configurator = HttpSessionConfigurator.class)
public class ChatServer {

    private static Logger logger = Logger.getLogger(ChatServer.class);
    private static int onlineCount = 0; // 记录连接数目
    // Map<用户id，用户信息>
    private static Map<String, OnlineUser> onlineUserMap = new ConcurrentHashMap<String, OnlineUser>(); // 在线用户

    /**
     * 连接成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("userid") String userid , Session session, EndpointConfig config){

        logger.info("[ChatServer] connection : userid = " + userid + " , sessionId = " + session.getId());

        // 增加用户数量
        addOnlineCount();

        // 获取当前用户的session
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user  = (User) httpSession.getAttribute("user"); // 获得当前用户信息

        // 将当前用户存到在线用户列表中
        OnlineUser onlineUser = new OnlineUser(user.getUserid(),user.getNickname(),session);
        onlineUserMap.put(user.getUserid(),onlineUser);

        // 通知所有在线用户，当前用户上线
        String content = "[" + CommonDate.getTime24() + " : " + user.getNickname() + "加入聊天室，当前在线人数为 " + getOnlineCount() + "位" + "]";
        JSONObject msg = new JSONObject();
        msg.put("content",content);
        String message = Message.getMessage(msg.toString(),Message.NOTICE,onlineUserMap.values());
        Message.broadcast(message,onlineUserMap.values());

    }

    /**
     * 连接关闭方法
     */
    @OnClose
    public void onClose(@PathParam("userid") String userid,Session session,CloseReason closeReason){

        logger.info("[ChatServer] close : userid = " + userid + " , sessionId = " + session.getId() +
                " , closeCode = " + closeReason.getCloseCode().getCode() + " , closeReason = " +closeReason.getReasonPhrase());

        // 减少当前用户
        subOnlienCount();

        // 移除的用户信息
        OnlineUser removeUser = onlineUserMap.remove(userid);
        onlineUserMap.remove(userid);

        // 通知所有在线用户，当前用户下线
        String content = "["+ CommonDate.getTime24() + " : " + removeUser.getNickname() + " 离开聊天室，当前在线人数为 " + getOnlineCount() + "位" + "]";
        JSONObject msg = new JSONObject();
        msg.put("content",content);
        if(onlineUserMap.size() > 0){
            String message = Message.getMessage(msg.toString(), Message.NOTICE, onlineUserMap.values());
            Message.broadcast(message,onlineUserMap.values());
        }else{
            logger.info("content : ["+ CommonDate.getTime24() + " : " + removeUser.getNickname() + " 离开聊天室，当前在线人数为 " + getOnlineCount() + "位" + "]");
        }

    }

    /**
     * 接收客户端的message，判断是否有接收人而选择进行广播还是指定发送
     * @param data 客户端发来的消息
     */
    @OnMessage
    public void onMessage(@PathParam("userid") String userid,String data){
        logger.info("[ChatServer] onMessage : userid = " + userid + " , data = " + data);

        JSONObject messageJson = JSONObject.fromObject(data);
        JSONObject message = messageJson.optJSONObject("message");
        String to = message.optString("to");
        String from = message.optString("from");
        // 将用户id转换为名称
        to = this.userIdCastNickName(to);

        OnlineUser fromUser = onlineUserMap.get(from);
        String sendMessage = Message.getContent(fromUser,to,message.optString("content"),message.optString("time"));
        String returnData = Message.getMessage(sendMessage, messageJson.optString("type"),null);

        if(to == null || to.equals("")){ // 进行广播
            Message.broadcast(returnData.toString(),onlineUserMap.values());
        }else{
            Message.singleSend(returnData.toString(), onlineUserMap.get(from));   // 发送给自己
            String[] useridList = message.optString("to").split(",");
            for(String id : useridList){
                if(!id.equals(from)){
                    Message.singleSend(returnData.toString(), onlineUserMap.get(id)); // 分别发送给指定的用户
                }
            }
        }
    }

    /**
     * 发生错误
     * @param throwable
     */
    @OnError
    public void onError(@PathParam("userid") String userid,Session session,Throwable throwable){
        logger.info("[ChatServer] close : userid = " + userid + " , sessionId = " + session.getId() +" , throwable = " + throwable.getMessage() );
    }


    public static int getOnlineCount() {
        return onlineCount;
    }

    public synchronized void addOnlineCount(){
        onlineCount++;
    }

    public synchronized void subOnlienCount(){
        onlineCount--;
    }

    /**
     * 将用户id转换为名称
     * @param userIds
     * @return
     */
    private String userIdCastNickName(String userIds){
        String niceNames = "";
        if(userIds != null && !userIds.equals("")){
            String[] useridList = userIds.split(",");
            String toName = "";
            for (String id : useridList){
                toName = toName + onlineUserMap.get(id).getNickname() + ",";
            }
            niceNames = toName.substring(0,toName.length() - 1);
        }
        return niceNames;
    }
}

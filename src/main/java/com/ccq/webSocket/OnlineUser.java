package com.ccq.webSocket;

import javax.websocket.Session;

/**
 * @author ccq
 * @Description
 * @date 2017/12/10 22:44
 */
public class OnlineUser {
    private String userid;
    private String nickname;
    private Session session;

    public OnlineUser() {
    }

    public OnlineUser(String userid, String nickname, Session session) {
        this.userid = userid;
        this.nickname = nickname;
        this.session = session;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}

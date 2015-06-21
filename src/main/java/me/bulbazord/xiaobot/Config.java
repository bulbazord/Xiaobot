package me.bulbazord.xiaobot;

public class Config {

    public String network;
    public int port;

    public String realname;
    public String nickname;

    public String handler;

    public Config(String network, int port, String realname, String nickname, String handler) {
        this.network = network;
        this.port = port;
        this.realname = realname;
        this.nickname = nickname;
        this.handler = handler;
    }
}

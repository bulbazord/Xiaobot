package me.bulbazord.xiaobot;

import java.net.*;
import java.io.*;

public class Xiaobot{

    private Socket socket;

    public Xiaobot() {
    }

    public boolean connect(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
            return true;
        } catch (UnknownHostException e) {
            System.err.println("The IP of the host could not be determined.");
        } catch (IOException e) {
            System.err.println("Could not open socket");
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Could not close socket");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Xiaobot xiaobot = new Xiaobot();

        System.out.println("Connecting...");
        xiaobot.connect(hostname, port);
        System.out.println("Connected!");

        System.out.println("Closing connection...");
        xiaobot.close();
        System.out.println("Closed!");
    }
}

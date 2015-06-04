package me.bulbazord.xiaobot;

import java.io.*;
import java.net.*;

public class Xiaobot{

    private Socket socket;
    private BufferedReader buffread;
    private BufferedWriter buffwrite;

    public Socket getSocket() {
        return this.socket;
    }

    public boolean connect(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
            InputStreamReader isr = new InputStreamReader(this.socket.getInputStream(), "UTF-8");
            OutputStreamWriter osw = new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8");

            this.buffread = new BufferedReader(isr);
            this.buffwrite = new BufferedWriter(osw);
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

    public boolean readReady() {
        try {
            return this.buffread.ready();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String readLine() {
        try {
            return this.buffread.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendLine(String line) {
        try {
            buffwrite.write(line + "\r\n");
            buffwrite.flush();
            System.out.println(System.currentTimeMillis() + " ! " + line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Xiaobot xiaobot = new Xiaobot();

        // Connect

        xiaobot.connect(hostname, port);

        xiaobot.sendLine("NICK xiaobot");
        xiaobot.sendLine("USER xiaobot 8 * :xiaobot");
        while (true) {
            while (xiaobot.readReady()) {
                System.out.println(System.currentTimeMillis() + " - " + xiaobot.readLine());
            }
        }
    }
}

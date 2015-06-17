package me.bulbazord.xiaobot;

import java.io.*;
import java.net.*;

public class Xiaobot{

    // Network Connection and I/O stuff
    private Socket socket;
    private BufferedReader buffread;
    private BufferedWriter buffwrite;

    private boolean running;

    public Xiaobot() {
        this.running = true;
    }

    public Socket getSocket() {
        return this.socket;
    }
    
    public boolean isRunning() {
        return this.running;
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

    public void parseLine(String line) {
        try {
            //System.out.println(System.currentTimeMillis() + " - " + line);
            String[] messageComponents = line.split("\\s", 3);
            for (int i = 0; i < messageComponents.length; i++) {
                System.out.print(messageComponents[i] + " | ");
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Please provide server and port number only");
            System.exit(0);
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Xiaobot xiaobot = new Xiaobot();
        xiaobot.connect(hostname, port);

        xiaobot.sendLine("NICK xiaobot");
        xiaobot.sendLine("USER xiaobot 8 * :xiaobot");
        while (xiaobot.isRunning()) {
            while (xiaobot.readReady()) {
                String incomingLine = xiaobot.readLine();
                xiaobot.parseLine(incomingLine);
            }
        }
    }
}

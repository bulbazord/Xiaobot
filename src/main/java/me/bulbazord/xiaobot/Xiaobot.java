package me.bulbazord.xiaobot;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.ArrayList;

public class Xiaobot{

    // Network Connection and I/O stuff
    private Socket socket;
    private BufferedReader buffread;
    private BufferedWriter buffwrite;

    private boolean running;

    /**
     * Default constructor, sets up default configurations.
     */
    public Xiaobot() {
        this.running = true;
    }

    /**
     * Returns the bot's socket.
     * 
     * @return The bot's socket.
     */
    public Socket getSocket() {
        return this.socket;
    }
    
    /**
     * Method to see if the bot is currently running.
     *
     * @return  A boolean representing whether or not the bot is 
     *          currently running.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Attempt to connect to an IRC network.
     * 
     * @param hostname The hostname of the network you wish to connect to.
     * @param port The port number
     * @return True if connection successful, false otherwise.
     */
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
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Could not open socket");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Attempt to close connection.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Could not close socket");
            e.printStackTrace();
        }
    }

    /**
     * Check if there is anything to read in the input stream.
     *
     * @return True if there is something to read. False otherwise.
     */
    public boolean readReady() {
        try {
            return this.buffread.ready();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Read a line from the input stream.
     *
     * @return The line being read.
     */
    public String readLine() {
        try {
            return this.buffread.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method to send a message/line to a server.
     *
     * @param line The line to be sent.
     */
    public void sendLine(String line) {
        try {
            buffwrite.write(line + "\r\n");
            buffwrite.flush();
            System.out.println(System.currentTimeMillis() + " ! " + line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to parse incoming line.
     *
     * @param line The line to parse.
     */
    public void parseLine(String line) {
        System.out.println(System.currentTimeMillis() + " - " + line);
        try {
            String[] messageComponents = line.split("\\s", 3);
            String sender;
            // Not all commands have a sender -- See PING
            if (messageComponents[0].startsWith(":")) {
                sender = messageComponents[0];
                messageComponents = Arrays.copyOfRange(messageComponents, 1, messageComponents.length);
            }
            //TODO switch statement, handle commands
            switch(messageComponents[0]) {

            case "PING":
                sendLine("PONG " + messageComponents[1]);
                break;

            default:
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method of xiaobot. 
     * Handles connecting and parsing lines.
     *
     * @param args Command line args fed into program.
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Please provide server and port number only");
            System.exit(0);
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Xiaobot xiaobot = new Xiaobot();
        boolean connected = xiaobot.connect(hostname, port);

        if (connected) {
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
}

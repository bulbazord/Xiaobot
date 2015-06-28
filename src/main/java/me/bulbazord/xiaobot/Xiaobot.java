package me.bulbazord.xiaobot;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A personal IRC bot for fun and non-profit.
 *
 * @version 0.1
 * @author Alexander Langford
 */
public class Xiaobot{

    // Network Connection and I/O stuff
    private Socket socket;
    private BufferedReader buffread;
    private BufferedWriter buffwrite;

    private Config config;
    private Parser parser;
    private BlockingQueue<String> toParse;

    private boolean running;

    /**
     * Default constructor, sets up default configurations.
     */
    public Xiaobot(Config config, Parser parser) {
        this.config = config;
        this.parser = parser;
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
     * Returns the bot's configuration information.
     * 
     * @return The bot's configuration.
     */
    public Config getConfig() {
        return this.config;
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
     * Method to pass given line to Parser.
     * This is achieved by inserting the line
     * into the toParse queue. The Parser will
     * handle it in another thread.
     *
     * @param line The line to parse.
     */
    public void insertToParse(String line) {
        synchronized(this.toParse) {
            try {
                this.toParse.put(line);
                this.toParse.notify();
            } catch (InterruptedException e) {
                // Ignore for now
            }
        }
    }

    /**
     * Main method of xiaobot. 
     * Handles connecting and parsing lines.
     *
     * @param args Command line args fed into program.
     */
    public static void main(String args[]) {

        // Get authentication password
        Console cons = System.console();
        System.out.print("Please enter the bot's NickServ password before connecting: ");
        char[] pass = cons.readPassword();
        String password = new String(pass);
        Arrays.fill(pass, '\0');


        // Create xiaobot
        Config xiaobotConfig = new Config();
        xiaobotConfig.loadConfig();
        xiaobotConfig.setPassword(password);
        Xiaobot xiaobot = new Xiaobot(xiaobotConfig, parser);
        boolean connected = xiaobot.connect(xiaobot.getConfig().network, xiaobot.getConfig().port);

        // Set up the parser and its thread
        /* TODO - You were still setting this shit up,
         * concerns are as follows:
         * Create the parser correctly, start it in its own thread,
         * and then finish the parser. Not hard.
         */
        BlockingQueue<String> toParse = new LinkedBlockingQueue<String>();
        Parser parser = new Parser(xiaobot, toParse);

        if (connected) {
            xiaobot.sendLine("NICK " + xiaobot.getConfig().nickname);
            xiaobot.sendLine("USER " + xiaobot.getConfig().nickname + " 8  * :" + xiaobot.getConfig().realname);
            while (xiaobot.isRunning()) {
                while (xiaobot.readReady()) {
                    String incomingLine = xiaobot.readLine();
                    xiaobot.insertToParse(incomingLine);
                }
            }
        }
    }
}

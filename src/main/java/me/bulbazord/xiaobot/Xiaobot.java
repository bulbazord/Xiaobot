package me.bulbazord.xiaobot;

import java.io.*;
import java.net.*;
import java.util.Arrays;

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

    private boolean running;

    /**
     * Default constructor, sets up default configurations.
     */
    public Xiaobot(Config config) {
        this.config = config;
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
     * Method to parse incoming line.
     *
     * @param line The line to parse.
     */
    public void parseLine(String line) {
        System.out.println(System.currentTimeMillis() + " - " + line);

        // Parsing stuffs
        String[] messageComponents = line.split("\\s", 4);
        String sender = null;
        String senderUsername = null;
        String senderHostname = null;
        String receiver = null;

        // Not all commands have a sender
        if (messageComponents[0].startsWith(":")) {
            sender = messageComponents[0];
            messageComponents = Arrays.copyOfRange(messageComponents, 1, messageComponents.length);
            if (sender.contains("!")) {
                String[] temp = sender.split("!");
                sender = temp[0];
                sender = sender.substring(1); // remove leading colon
                temp = temp[1].split("@");
                senderUsername = temp[0];
                senderUsername = senderUsername.substring(1); // remove leading tilde
                senderHostname = temp[1];
            } 
        }

        // Switch statement, handle commands
        switch(messageComponents[0]) {

        case "PING":
            sendLine("PONG " + messageComponents[1]);
            break;

        /* If we try to do it everytime we receive a notice
         * we'll get a back and forth between nickserv and xiaobot.
         * 376 is end of MOTD.
         * 
         * TODO: Do this not like shit.
         */
        case "376":
            sendLine("PRIVMSG NickServ :IDENTIFY " + getConfig().nickname + " " + getConfig().password);
            break;

        //TODO handle messages and commands not like shit
        case "PRIVMSG":
            receiver = messageComponents[1];
            if (sender.equals(getConfig().handler) && receiver.equals(getConfig().nickname)) {
                if (messageComponents[2].startsWith(":!join ")) {
                    //TODO add error checking
                    String[] temp = messageComponents[2].split(" ");
                    sendLine("JOIN " + temp[1]);
                } else if (messageComponents[2].startsWith(":!nick")) {
                    //TODO add error checking
                    String[] temp = messageComponents[2].split(" ");
                    sendLine("NICK " + temp[1]);
                    getConfig().nickname = temp[1];
                } else if (messageComponents[2].startsWith(":!part ")) {
                    //TODO add error checking
                    String[] temp = messageComponents[2].split(" ");
                    sendLine("PART " + temp[1]);
                }
            }
            break;

        default:
            break;
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

        Config xiaobotConfig = new Config();
        xiaobotConfig.loadConfig();
        xiaobotConfig.setPassword(password);
        Xiaobot xiaobot = new Xiaobot(xiaobotConfig);
        boolean connected = xiaobot.connect(xiaobot.getConfig().network, xiaobot.getConfig().port);

        if (connected) {
            xiaobot.sendLine("NICK " + xiaobot.getConfig().nickname);
            xiaobot.sendLine("USER " + xiaobot.getConfig().nickname + " 8  * :" + xiaobot.getConfig().realname);
            while (xiaobot.isRunning()) {
                while (xiaobot.readReady()) {
                    String incomingLine = xiaobot.readLine();
                    xiaobot.parseLine(incomingLine);
                }
            }
        }
    }
}

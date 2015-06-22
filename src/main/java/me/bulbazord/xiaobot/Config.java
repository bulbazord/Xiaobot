package me.bulbazord.xiaobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Config {

    public String network;
    public int port;

    public String realname;
    public String nickname;
    public String password;

    public String handler;

    /**
     * Config constructor that allows you to insert all info at creation.
     *
     * @param network The hostname of the network you want to connect to.
     * @param port The port you want to connect on. Typically 6667 for non-SSL connections.
     * @param realname The realname you want to use on the IRC network.
     * @param nickname The nickname you want to use on the IRC network.
     * @param password The password you use to authenticate your nickname on the IRC network.
     * @param handler The nickname of the person the bot will take commands from.
     */
    public Config(String network, int port, String realname, String nickname, String password, String handler) {
        this.network = network;
        this.port = port;
        this.realname = realname;
        this.nickname = nickname;
        this.password = password;
        this.handler = handler;
    }

    /**
     * Null args constructor.
     * Most often used when loading the configuration file.
     */
    public Config() {

    }

    /**
     * Loads configuration file.
     * Expected to be .xbot in user's home directory.
     *
     * @return True if the configuration file loaded properly. False otherwise.
     */
    public boolean loadConfig() {
        boolean success = false;
        String userHome = System.getProperty("user.home");
        String configFileLocation = userHome + File.separator + ".xbot";

        BufferedReader configFile = null;
        try {
            configFile = new BufferedReader(new FileReader(configFileLocation));
            this.network = configFile.readLine();
            this.port = Integer.parseInt(configFile.readLine());
            this.realname = configFile.readLine();
            this.nickname = configFile.readLine();
            this.handler = configFile.readLine();
            success = true;
        } catch (FileNotFoundException e) {
            System.out.println("The configuration file .xbot does not exist in your home directory.");
            success = false;
        } catch (IOException e) {
            System.out.println("There was an error reading the file. Did you format it correctly?");
            success = false;
        } finally {
            try {
                if (configFile != null) {
                    configFile.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close file");
                success = false;
            }
        }

        return success;
    }

    /**
     * Method to set password after loading resources file.
     *
     * @param password The password you want to use when connecting.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

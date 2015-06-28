package me.bulbazord.xiaobot;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Parser implements Runnable {

    public Xiaobot bot;
    public BlockingQueue<String> toParse;

    public Parser(Xiaobot bot, BlockingQueue toParse) {
        this.toParse = toParse;
        this.bot = bot;
    }

    /**
     * This code is meant to handle parsing messages.
     *
     * Outline:
     *  - Wait until there is something to parse.
     *  - If there is something to parse, parse it.
     *
     */
    public void run() {
        String message = null;
        while (this.bot.isRunning()) {
            synchronized(this.toParse) {
                while (this.toParse.isEmpty()) {
                    try {
                        this.toParse.wait();
                    } catch (InterruptedException e) {
                        // Ignore for now
                    }
                }
                message = toParse.poll();
            }

            parseLine(message);
        }
    }

    /**
     * Method to handle parsing a line.
     * Does the bare minimum for now.
     *
     * TODO - Handle commands and triggers properly
     *
     * @param message The message to parse.
     */
    public void parseLine(String message) {
        System.out.println(System.currenttimeMillis() + " - " + line);

        // Break the message up
        String[] messageComponents = line.split("\\s", 4);
        String sender = null;
        String senderRealname = null;
        String senderHostname = null;
        String receiver = null;

        // Not all commands have a sender
        if (messageComponents[0].startsWith(":")) {
            sender = messageComponents[0];
            messageComponents = Arrays.copyOfRange(messageComponents, 1, messageComponents.length);
            if (sender.contains("!")) {
                String[] temp = sender.split("!");
                sender = temp[0];
                sender = sender.substring(1); // Remove the leading colon
                temp = temp[1].split("@");
                senderRealname = temp[0];
                senderRealname = senderRealname.substring(1); // remove leading tilde
                senderHostname = temp[1];
            }
        }

        switch (messageComponents[0]) {

            default:
                break;
        }
    }
}

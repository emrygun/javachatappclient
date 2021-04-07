package com.emrygun;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ReadThread extends Thread {
    //Buffer and Socket initializations
    private BufferedReader reader;
    private Socket socket;
    private ChatAppClient instance;

    //Some status integers
    public static final int S_MESSAGE   = 1;
    public static final int U_MESSAGE   = 2;
    public static final int USERNAME    = 3;
    public static final int USERLIST    = 4;
    public static final int CONNECT     = 5;
    public static final int DISCONNECT  = 6;

    public static final int TEXTSIZE_OFFSET = 20;
    //Constructor
    public ReadThread(Socket socket, ChatAppClient instance) {
        this.socket = socket;
        this.instance = instance;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Reader thread
    public void run() {
        String ServerMessage;
        while (true) {
            try {
                do {
                    ServerMessage = reader.readLine();
                    ServerMessageHandler(ServerMessage);
                } while (socket.isConnected());

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //If server closed
            catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(instance, socket.getInetAddress() + ":" + socket.getPort() + " is closed.");
                System.exit(0);
                break;
            }
        }
    }

    public void ServerMessageHandler(String serverMessage) throws NumberFormatException {
        int messageType = Integer.parseInt(serverMessage.substring(0,1));
        String message;
        Color messageColor;
        int messageSize;
        switch (messageType) {
            //Get Server Message
            case S_MESSAGE:
                message = serverMessage.substring(1);
                appendToPane(instance.GroupChatTextArea, message + "\n", Color.RED, 15, true);
                //new Thread(() -> { playSoundInternal(new File("sounds/serverMessage.mid")); }).start();
                break;
            //Get User Message
            case U_MESSAGE:
                //Parse messag into components
                 messageColor = new Color(Integer.parseInt(serverMessage.substring(1,4)),
                        Integer.parseInt(serverMessage.substring(4,7)),
                        Integer.parseInt(serverMessage.substring(7,10)));
                 messageSize = Integer.parseInt(serverMessage.substring(10,12));
                message = serverMessage.substring(12);
                appendToPane(instance.GroupChatTextArea, message + "\n", messageColor, messageSize, false);

                //Play sound in new thread
                if(instance.getUserMessageNotificationCheckBoxValue())
                    new Thread(() -> { playSoundInternal(new File("sounds/userMessage.mid")); }).start();
                break;
            //Get User List
            case USERLIST:
                String[] userTokens = serverMessage.substring(1).split("/1/");
                for (String t : userTokens)
                    instance.UsersListModel.addElement(t);
                break;
            //Get Connection Info
            case CONNECT:
                instance.UsersListModel.addElement(serverMessage.substring(1));
                if(instance.getUserJoinedNotificationCheckBoxValue())
                    new Thread(() -> { playSoundInternal(new File("sounds/userConnected.mid")); }).start();
                break;
            //Get Disconnection info
            case DISCONNECT:
                instance.UsersListModel.removeElement(serverMessage.substring(1));
                if (instance.getUserLeftNotificationCheckBoxValue())
                    new Thread(() -> { playSoundInternal(new File("sounds/userDisconnected.mid")); }).start();
                break;
            //Return in an exceptional situation
            default:
                break;
        }
    }

    //Text appender with styles
    private void appendToPane(JTextPane tp, String msg, Color c, int fontSize, boolean isBold) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
        aset = sc.addAttribute(aset, StyleConstants.Bold, isBold);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    void playSoundInternal(File f) {
        try {
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f)) {
                Clip clip = AudioSystem.getClip();
                try (clip) {
                    clip.open(audioInputStream);
                    clip.start();
                    try { Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); }
                    clip.drain();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
}
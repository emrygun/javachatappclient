package com.emrygun;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        switch (Integer.parseInt(serverMessage.substring(0,1))) {
            //Get Server Message
            case S_MESSAGE:
                instance.GroupChatTextArea.append(serverMessage.substring(1) + "\n");
                break;
            //Get User Message
            case U_MESSAGE:
                instance.GroupChatTextArea.append(serverMessage.substring(1) + "\n");
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
                break;
            //Get Disconnection info
            case DISCONNECT:
                instance.UsersListModel.removeElement(serverMessage.substring(1));
                break;
            //Return in an exceptional situation
            default:
                break;
        }
    }
}
package com.emrygun;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        //Check for args size
        if (args.length < 2){
            System.out.println("Syntax: ChatAppClient <hostname> <port>");
            return;
        }

        //Hostname, Port and Window Text
        String hostname = new String(args[0]);
        int port = Integer.parseInt(args[1]);
        Socket clientSocket = null;
        String WindowText = new String("Connected to: " + hostname + ":" + port);

        //Bind socket
        try {
            clientSocket = new Socket(hostname, port);
        } catch(ConnectException ex) {
            JOptionPane.showMessageDialog(null, "Could not connect " + hostname + ":" + port);
            System.exit(0);
        }

        //Initialize the JFrame
        JFrame ChatAppClient = new ChatAppClient(WindowText, clientSocket);
    }
}

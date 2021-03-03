package com.emrygun;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatAppClient<Users> extends JFrame implements ChatAppInterfaceController {
    //JFrame objects
    private JPanel MainPanel;
    private JPanel ChatSpace;
    public JPanel UserSpace;
    private JTabbedPane TabbedPanel;
    private JPanel GroupChat;
    public JTextField MessageInputField;
    private JButton SendButton;
    private JPanel InputSpace;
    private JScrollPane GroupChatScreen;
    private JPanel SettingsPanel;
    private JSlider slider1;
    public JTextArea GroupChatTextArea;
    private JList UserList;
    private JScrollPane UserListScrollPane;

    //Client Stuff
    public String Username = new String();
    public DefaultListModel UsersListModel = new DefaultListModel();

    Socket clientSocket;
    OutputStream clientOutput;
    OutputStreamWriter clientOutputWriter;
    PrintWriter clientSocketWriter;
    ReadThread clientReadThread;


    //Constructor
    ChatAppClient(String WindowText, Socket clientSocket) throws IOException {
        //Base Initializations
        super(WindowText);
        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(640, 480);
        setLocationRelativeTo(null);

        //Socket, Writer and Reader initializations
        this.clientSocket = clientSocket;
        this.clientOutput = clientSocket.getOutputStream();
        this.clientSocketWriter = new PrintWriter(new OutputStreamWriter(clientOutput, StandardCharsets.UTF_8), true);
        this.clientReadThread = new ReadThread(clientSocket, this);
        clientReadThread.start();

        //Add Tabs
        TabbedPanel.addTab("Group Chat", GroupChat);
        TabbedPanel.addTab("Settings", SettingsPanel);

        //Bind UserList to Users model
        UserList.setModel(UsersListModel);

        //Action and Key Listeners
        addKeyListener(ChatAppClientKeys);
        SendButton.addActionListener(ChatAppClientActions);
        MessageInputField.addActionListener(ChatAppClientActions);

        setVisible(true);
        setUsername(this);
    }

    //General "Key Listener" for Client"
    KeyListener ChatAppClientKeys = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    clientSwitchUserSpace(ChatAppClient.this);
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    //General "Action Listener" for Client
    ActionListener ChatAppClientActions = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Send Button and Enter Message
            if ((e.getSource() == SendButton) | (e.getSource() == MessageInputField)) {
                clientSendMessage(ChatAppClient.this);
                clientSwitchUserSpace(ChatAppClient.this);
            }
        }
    };
}

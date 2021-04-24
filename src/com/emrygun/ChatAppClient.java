package com.emrygun;

import javax.swing.*;
import java.awt.*;
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
    private JPanel UserSpace;
    private JTabbedPane TabbedPanel;
    private JPanel GroupChat;
    private JTextField MessageInputField;
    private JButton SendButton;
    private JPanel InputSpace;
    private JScrollPane GroupChatScreen;
    private JPanel SettingsPanel;
    private JTextPane GroupChatTextArea;
    private JList UserList;
    private JScrollPane UserListScrollPane;
    private JSlider redColorSlider;
    private JSlider greenColorSlider;
    private JSlider blueColorSlider;
    private JPanel ColorSettingPanel;
    private JPanel colorBox;
    private JPanel FontSizePanel;
    private JSlider textSizeSlider;
    private JCheckBox userMessageNotificationCheckBox;
    private JCheckBox userJoinedNotificationCheckBox;
    private JCheckBox userLeftNotificationCheckBox;
    private JPanel NotificationPanel;

    //Client Stuff
    private String Username = new String();
    private DefaultListModel UsersListModel = new DefaultListModel();

    Socket clientSocket;
    OutputStream clientOutput;
    OutputStreamWriter clientOutputWriter;
    PrintWriter clientSocketWriter;
    ReadThread clientReadThread;

    //Message text style
    Color textColor;
    int textSize = 10;

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
        TabbedPanel.addTab("Mesajlar", GroupChat);
        TabbedPanel.addTab("Ayarlar", SettingsPanel);
        //Paint the colorBox
        colorBox.setBackground(new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue()));
        textColor = Color.BLACK;
        //Bind UserList to Users model
        UserList.setModel(UsersListModel);

        //Action and Key Listeners
        MessageInputField.addKeyListener(ChatAppClientKeys);
        GroupChatTextArea.addKeyListener(ChatAppClientKeys);

        SendButton.addActionListener(ChatAppClientActions);
        MessageInputField.addActionListener(ChatAppClientActions);

        setVisible(true);
        setUsername(this);

        //Update Color
        redColorSlider.addChangeListener(e -> {
            colorBox.setBackground(new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue()));
            textColor = new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue());
        });

        greenColorSlider.addChangeListener(e -> {
            colorBox.setBackground(new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue()));
            textColor = new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue());
        });

        blueColorSlider.addChangeListener(e -> {
            colorBox.setBackground(new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue()));
            textColor = new Color(redColorSlider.getValue(), greenColorSlider.getValue(), blueColorSlider.getValue());
        });

        textSizeSlider.addChangeListener(e -> {
            textSize = textSizeSlider.getValue();
        });
    }

    //General "Key Listener" for Client"
    KeyListener ChatAppClientKeys = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F2)
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
            }
        }
    };

    //Checkbox
    public boolean getUserMessageNotificationCheckBoxValue() {
        return userMessageNotificationCheckBox.isSelected();
    }

    public boolean getUserJoinedNotificationCheckBoxValue() {
        return userJoinedNotificationCheckBox.isSelected();
    }

    public boolean getUserLeftNotificationCheckBoxValue() {
        return userLeftNotificationCheckBox.isSelected();
    }

    public JPanel getUserSpace() { return UserSpace; }
    public JTextField getMessageInputField() { return MessageInputField; }
    public JTextPane getGroupChatTextArea() { return GroupChatTextArea; }
    public String getUsername() { return Username; }
    public void setUsername(String Username) { this.Username = Username; }
    public DefaultListModel getUsersListModel() { return UsersListModel; }


}

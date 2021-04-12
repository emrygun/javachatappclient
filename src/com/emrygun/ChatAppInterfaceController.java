package com.emrygun;

import javax.swing.*;

public interface ChatAppInterfaceController {
    //Ask for username and set it
    //If user press cancel terminate
    default void setUsername(ChatAppClient instance) {
        //Check for username is not empty and username list not including the username
        while ((instance.getUsername().isEmpty()) | (instance.getUsersListModel().contains(instance.getUsername()))) {
            instance.setUsername(JOptionPane.showInputDialog(instance, "Enter Username"));
            //If you press cancel
            if (instance.getUsername() == null) {
                System.exit(0);
            }
            else if (instance.getUsersListModel().contains(instance.getUsername())) {
                JOptionPane.showMessageDialog(null, instance.getUsername() + " is taken.");
            }
        }

        instance.clientSocketWriter.println(instance.getUsername());
    }

    //Client Send Message
    default void clientSendMessage(ChatAppClient instance) {
        //Check if messagebox empty or not
        if (instance.getMessageInputField().getText().equals("")) return;
        else {
            //instance.clientSocketWriter.println(instance.MessageInputField.getText());
            instance.clientSocketWriter.println(String.format("%03d%03d%03d%02d%s",
                    instance.textColor.getRed(), instance.textColor.getGreen(), instance.textColor.getBlue(), instance.textSize, instance.getMessageInputField().getText()));
            instance.getMessageInputField().setText("");
        }
    }

    //Switch "User Space" on and off
    default void clientSwitchUserSpace(ChatAppClient instance){
        instance.getUserSpace().setVisible(!instance.getUserSpace().isVisible());
    }
}

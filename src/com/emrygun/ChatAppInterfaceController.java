package com.emrygun;

import javax.swing.*;

public interface ChatAppInterfaceController {
    //Ask for username and set it
    //If user press cancel terminate
    default void setUsername(ChatAppClient instance) {
        //Check for username is not empty and username list not including the username
        while ((instance.Username.isEmpty()) | (instance.UsersListModel.contains(instance.Username))) {
            instance.Username = JOptionPane.showInputDialog(instance, "Enter Username");
            //If you press cancel
            if (instance.Username == null) {
                System.exit(0);
            }
            else if (instance.UsersListModel.contains(instance.Username)) {
                JOptionPane.showMessageDialog(null, instance.Username + " is taken.");
            }
        }

        instance.clientSocketWriter.println(instance.Username);
    }

    //Client Send Message
    default void clientSendMessage(ChatAppClient instance) {
        //Check if messagebox empty or not
        if (instance.MessageInputField.getText().equals("")) return;
        else {
            instance.clientSocketWriter.println(instance.MessageInputField.getText());
            instance.MessageInputField.setText("");
        }
    }

    //Switch "User Space" on and off
    default void clientSwitchUserSpace(ChatAppClient instance){
        instance.UserSpace.setVisible(!instance.UserSpace.isVisible());
    }
}

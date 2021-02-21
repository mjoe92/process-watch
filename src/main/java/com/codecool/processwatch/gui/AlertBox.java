package com.codecool.processwatch.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AlertBox {

    private static String helpMessage = "   Process data\n" +
            "\n" +
            "   The application gathers the current processes from the computer and displays them in the table.     \n   " +
            "   The displayed processes are the following :\n" +
            "   Process Id, Parent Process Id, Owner, Names, Arguments\n" +
            "\n" +
            "\n" +
            "   Refresh\n" +
            "\n" +
            "   Refreshes the process list.\n" +
            "   When the button is pressed, the list of active processes \n" +
            "   are re-requested and displayed\n" +
            "\n" +
            "   Hide restricted\n" +
            "\n" +
            "   With the help of 'Hide restricted' the user can hide those processes from the table\n" +
            "   which are not visible when Windows operating system.\n" +
            "\n" +
            "   Process kill\n" +
            "\n" +
            "   With the help of the mouse (ctrl+click for multiple selection) \n" +
            "   the user can select from the table and switch down the selected\n" +
            "   processes. \n" +
            "   With ctrl+click the user can deselect a process.\n" +
            "\n" +
            "   About\n" +
            "\n" +
            "   The user can gain a quick insight into the application's operation.\n" +
            "\n" +
            "   Process filter\n" +
            "\n" +
            "   The user can filter the displayed processes.\n" +
            "   For instance: the app can filter the processes of a given user, or\n" +
            "   all the processes that have the same parent process can be displayed.\n";


    public static void displayStage (String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);

        Label label = new Label();
        label.setText(message);
        label.setAlignment(Pos.TOP_CENTER);
        Button closeButton = new Button("Close the window");
        closeButton.setOnAction(e -> window.close());
        closeButton.setAlignment(Pos.BOTTOM_CENTER);


        VBox layout = new VBox();
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }


    public static String getHelpMessage() {
        return helpMessage;
    }
}

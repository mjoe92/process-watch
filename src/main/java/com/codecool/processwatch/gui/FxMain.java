package com.codecool.processwatch.gui;

import com.codecool.processwatch.domain.User;
import com.codecool.processwatch.queries.SelectAll;
import com.codecool.processwatch.queries.SelectFiltered;
import com.codecool.processwatch.queries.SelectNotRestricted;
import com.codecool.processwatch.domain.Process;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * The JavaFX application Window.
 */
public class FxMain extends Application {
    private static final String TITLE = "Process Watch";
    private static final String SELECT_ALL = "*SELECT ALL";
    private int selectedRowIndex = -1;

    private App app;

    /**
     * Entrypoint for the javafx:run maven task.
     *
     * @param args an array of the command line parameters.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Build the application window and set up event handling.
     *
     * @param primaryStage a stage created by the JavaFX runtime.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TITLE);

        ObservableList<ProcessView> displayList = observableArrayList();
        app = new App(displayList);
        var textFieldParentPId = new TextField();
        // TODO: Factor out the repetitive code
        String[] columns = {"Process ID", "Parent Process ID", "Owner", "Names", "Arguments"};
        var tableView = new TableView<ProcessView>(displayList);
        var pidColumn = new TableColumn<ProcessView, Long>(columns[0]);
        pidColumn.setCellValueFactory(new PropertyValueFactory<ProcessView, Long>("pid"));
        var parentPidColumn = new TableColumn<ProcessView, Long>(columns[1]);
        parentPidColumn.setCellValueFactory(new PropertyValueFactory<ProcessView, Long>("parentPid"));
        var userNameColumn = new TableColumn<ProcessView, String>(columns[2]);
        userNameColumn.setCellValueFactory(new PropertyValueFactory<ProcessView, String>("userName"));
        var processNameColumn = new TableColumn<ProcessView, String>(columns[3]);
        processNameColumn.setCellValueFactory(new PropertyValueFactory<ProcessView, String>("processName"));
        var argsColumn = new TableColumn<ProcessView, String>(columns[4]);
        argsColumn.setCellValueFactory(new PropertyValueFactory<ProcessView, String>("args"));
        tableView.getColumns().add(pidColumn);
        tableView.getColumns().add(parentPidColumn);
        tableView.getColumns().add(userNameColumn);
        tableView.getColumns().add(processNameColumn);
        tableView.getColumns().add(argsColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableView.setOnMouseClicked(e -> {
            if (selectedRowIndex == tableView.getSelectionModel().getSelectedIndex()) {
                tableView.getSelectionModel().clearSelection();
                selectedRowIndex = -1;
            }
            selectedRowIndex = tableView.getSelectionModel().getSelectedIndex();
        });

        var refreshButton = new Button("Refresh");
        refreshButton.setOnAction(click -> app.refresh());

        var killProcessButton = new Button("Kill process");
        killProcessButton.setOnAction(e -> killSelectedProcesses(tableView.getSelectionModel().getSelectedItems(), app));


        var aboutButton = new Button("About");
        aboutButton.setOnAction(click -> {
                    //TODO pretty print text to About window
                    Text aboutText = new Text(" This program shows your current processes. \r\n " +
                            "You can refresh, filter and kill processes.\r\n For additional informations please see ? icons.");

                    var aboutBox = new VBox(aboutText);
                    var aboutScene = new Scene(aboutBox, 300, 150);
                    var aboutElements = aboutBox.getChildren();
                    Stage secondStage = new Stage();
                    secondStage.setScene(aboutScene); // set the scene
                    secondStage.setTitle("About");
                    secondStage.initModality(Modality.APPLICATION_MODAL);
                    secondStage.showAndWait();
                }
        );

        var checkBoxRestrictedProcesses = new CheckBox("Hide restricted");
        checkBoxRestrictedProcesses.setSelected(false);
        checkBoxRestrictedProcesses.setPadding(new Insets(5, 20, 0, 20));
        checkBoxRestrictedProcesses.setOnAction(e -> handleCheckbox(checkBoxRestrictedProcesses, app));

        var filteredItems = new ComboBox();
        filteredItems.setEditable(false);
        var columnFilter = new ComboBox();
        columnFilter.getItems().add(SELECT_ALL);
        columnFilter.getItems().addAll(columns);
        columnFilter.getItems().remove(5);
        final ObservableList<ProcessView> firstDisplayList = displayList;

        columnFilter.setOnAction(click -> {
            filteredItems.getSelectionModel().select(0);
            List<String> list = filterItems(columnFilter, firstDisplayList);
            filteredItems.getItems().clear();
            filteredItems.getItems().addAll(list.stream().sorted().distinct().collect(Collectors.toList()));
            });

        filteredItems.setOnAction(click -> {
            String selectionInStr = String.valueOf(filteredItems.getSelectionModel().getSelectedItem());
            if (!selectionInStr.equals(filteredItems.getItems().get(0))) {
                Process process = generateFakeProcess(columnFilter, filteredItems);
                app.setQuery(new SelectFiltered(process));
            } else {
                handleCheckbox(checkBoxRestrictedProcesses, app);
            }
        });

        var helpButton = new Button("?");
        helpButton.setOnAction(e -> AlertBox.displayStage("Help",
                AlertBox.getHelpMessage()));

        HBox hBox = new HBox(15);
        hBox.setPadding(new Insets(20, 20,20,20));
        var elementsHBox = hBox.getChildren();
        elementsHBox.addAll(refreshButton,
                checkBoxRestrictedProcesses,
                killProcessButton,
                aboutButton,
                helpButton);

        var box = new VBox();
        var scene = new Scene(box, 640, 480);
        var elements = box.getChildren();

        var filterBoxes = new HBox(15);
        filterBoxes.setPadding(new Insets(10, 10,10,10));
        filteredItems.setPrefWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                - filterBoxes.getPadding().getLeft()
                - filterBoxes.getPadding().getRight());
        var filterElement = filterBoxes.getChildren();
        columnFilter.getSelectionModel().select(0);

        columnFilter.setMinWidth(140);
        filterElement.addAll(columnFilter, filteredItems);
        tableView.setPrefHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight()
            - hBox.getHeight()
            - filterBoxes.getHeight());

        elements.addAll(hBox,
                tableView,
                filterBoxes);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private static void handleCheckbox(CheckBox checkBox, App app) {
        if (checkBox.isSelected()) {
            app.setQuery(new SelectNotRestricted());
        }
        else {
            app.setQuery(new SelectAll());
        }
    }

    private static void killSelectedProcesses(ObservableList<ProcessView> pv, App app) {
        if (pv.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "There is no selected process.\nPlease select the process(es) to be killed first.", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure about to kill the selected process(es)?\nIt may cause unwanted effects (e.g. unstable OS)", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                System.out.println("KILL!!!");
                for (ProcessView pView : pv) {
                    Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pView.getPid());
                    optionalProcessHandle.ifPresent(processHandle -> processHandle.destroy());
                }
                app.refresh();
            }
        }
    }

    private static List filterItems(ComboBox filters, ObservableList<ProcessView> display) {
        String selectedItem = (String) filters.getSelectionModel().getSelectedItem();
        List<String> list = new ArrayList();
        list.add(SELECT_ALL);
        for (ProcessView pr : display) {
            if (selectedItem.equals(filters.getItems().get(1))) {
                list.add(String.valueOf(pr.getPid()));
            } else if (selectedItem.equals(filters.getItems().get(2))) {
                list.add(String.valueOf(pr.getParentPid()));
            } else if (selectedItem.equals(filters.getItems().get(3))) {
                list.add(pr.getUserName());
            } else if (selectedItem.equals(filters.getItems().get(4))) {
                list.add(pr.getProcessName());
            }
        }
        return list;
    }

    private static Process generateFakeProcess(ComboBox filters, ComboBox filteredItems) {
        Object filterCol = filters.getSelectionModel().getSelectedItem();
        Object filterItem = filteredItems.getSelectionModel().getSelectedItem();
        String selectionInStr = String.valueOf(filterItem);
        User user = new User(selectionInStr);

        if (filterCol.equals(filters.getItems().get(1))) {
            return new Process(Long.parseLong(selectionInStr), -1, null, null, null);
        } else if (filterCol.equals(filters.getItems().get(2))) {
            return new Process(-1, (Long.parseLong(selectionInStr)), null, null, null);
        } else if (filterCol.equals(filters.getItems().get(3))) {
            return new Process(-1, -1, user, null, null);
        } else if (filterCol.equals(filters.getItems().get(4))) {
            return new Process(-1, -1, null, String.valueOf(filterItem), null);
        }
        return new Process(-1,-1, null,null,null);
    }
}

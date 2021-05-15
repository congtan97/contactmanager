package controller;


import dao.GroupDAO;
import entity.Group;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class GroupController {
    @FXML
    private Button btnSearch, btnAdd, btnDelete, btnUpdate, btnClose;
    @FXML
    private ListView<Group> tblGroup;
    @FXML
    private TextField search, groupName;

    private final String GROUP = "data/group.txt";

    private GroupDAO groupDAO = new GroupDAO();
    private List<Group> groups;
    private ContactController contactController;

    public void setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    @FXML
    void initialize() {
        try {
            //load all group
            groups = groupDAO.loadGroup(GROUP);
            //output groups to list view
            showGroup(groups);

            tblGroup.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            tblGroup.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Group>() {
                @Override
                public void changed(ObservableValue<? extends Group> observable, Group oldValue, Group newValue) {
                    //output selected group name to text field
                    if (tblGroup.getSelectionModel().getSelectedItem() != null) {
                        groupName.setText(tblGroup.getSelectionModel().getSelectedItem().getName());
                    }
                }
            });
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("" + e);
            alert.showAndWait();
        }
    }

    //search action
    public  void searchAction() {
        List<Group> g = groupDAO.search(groups, search.getText());
        showGroup(g);
    }

    //add new group action
    public  void addAction() throws Exception {
        String name = groupName.getText().trim();
        if (name.isEmpty() || name.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Group cannot be empty");
            alert.showAndWait();
        } else {
            Group g = new Group(name);
            int i = groupDAO.indexOf(groups, g);
            if (i != -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Group name exists already, choose another name");
                alert.showAndWait();
            } else {
                groupDAO.saveGroupToList(groups, g);
                groupDAO.saveGroupToFile(groups, GROUP);
                showGroup(groups);
                contactController.showGroup(groups);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("A new group has been added");
                alert.showAndWait();
            }
        }
    }

    //update group name
    public  void updateAction() throws Exception {
        int i = tblGroup.getSelectionModel().getSelectedIndex();
        if (i >= tblGroup.getItems().size() || i < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Select a group to delete");
            alert.showAndWait();
            return;
        }
        //get updated group
        String oldGroup = tblGroup.getItems().get(i).getName();
        String newGroup = groupName.getText().trim();
        int index = groupDAO.indexOf(groups, new Group(oldGroup));
        if (groupDAO.updateGroup(groups, index, oldGroup, newGroup) == false) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Please select another name for group");
            alert.showAndWait();
        } else {
            //update group name for all contacts which is in updated group
            contactController.updateContactGroup(oldGroup, newGroup);

            showGroup(groups);
            contactController.showGroup(groups);
            groupDAO.saveGroupToFile(groups, GROUP);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Group has been updated");
            alert.showAndWait();
        }
    }

    //delete a group, delete failed if there are some contact is in deleted one
    public  void deleteAction()throws Exception {
        int i = tblGroup.getSelectionModel().getSelectedIndex();
        if (i<0 || i>= tblGroup.getItems().size()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Select a group to delete");
            alert.showAndWait();
            return;
        }
        //get number of contacts of selected delete
        int size = ((Group) tblGroup.getItems().get(i)).contacts().size();
        if (size > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Group has some contacts, cannot delete this one");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Do you wanna delete selected group?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                groups.remove(i);
                showGroup(groups);
                groupDAO.saveGroupToFile(groups, GROUP);
                contactController.showGroup(groups);
            }
        }
    }

    //operations on each button on window
    public  void groupAction(ActionEvent evt)throws Exception {
        if (evt.getSource() == btnSearch) {
            searchAction();
        } else if (evt.getSource() == btnAdd) {
            addAction();
        } else if (evt.getSource() == btnUpdate) {
            updateAction();
        } else if (evt.getSource() == btnDelete) {
            deleteAction();
        } else if (evt.getSource() == btnClose) {
            final Node source = (Node) evt.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    //output all groups to table view
    public  void showGroup(List<Group> groups) {
        //clear old data
        tblGroup.getItems().clear();
        if (tblGroup.getItems() != null) {
            for (Group g : groups) {
                tblGroup.getItems().add(g);
            }
        }
    }
}

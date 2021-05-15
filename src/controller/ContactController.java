package controller;

import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;


public class ContactController {
    @FXML
    private TextField search;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private TableView tblContact;
    @FXML
    private Button btnSearch, btnAdd, btnDelete, btnUpdate, btnGroup;

    //list of contacts
    List<Contact> contacts;
    ContactDAO contactDAO = new ContactDAO();
    //data source for contact and group
    private final String GROUP = "data/group.txt";
    private final String CONTACT = "data/contact.txt";

    @FXML
    void initialize() {
        try {
            //load all contacts
            contacts = contactDAO.loadContact(CONTACT);

            //create table columns
            TableColumn<String, Contact> fname = new TableColumn<>("First Name");
            fname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tblContact.getColumns().add(fname);

            TableColumn<String, Contact> lname = new TableColumn<>("Last Name");
            lname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tblContact.getColumns().add(lname);

            TableColumn<String, Contact> phone = new TableColumn<>("Phone");
            phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            tblContact.getColumns().add(phone);

            TableColumn<String, Contact> email = new TableColumn<>("Email");
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tblContact.getColumns().add(email);

            TableColumn<String, Contact> dob = new TableColumn<>("Birth Date");
            dob.setCellValueFactory(new PropertyValueFactory<>("dob"));
            tblContact.getColumns().add(dob);

            TableColumn<String, Contact> group = new TableColumn<>("Group Name");
            group.setCellValueFactory(new PropertyValueFactory<>("group"));
            tblContact.getColumns().add(group);

            TableColumn<String, Contact> address = new TableColumn<>("Address");
            address.setCellValueFactory(new PropertyValueFactory<>("address"));
            tblContact.getColumns().add(address);

            //get all groups
            showGroup(new GroupDAO().loadGroup(GROUP));
            //output contacts to tblContact
            showContact(contacts);
            tblContact.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } catch (Exception e) {
            System.out.println("Exception in initialize of ContactController");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("" + e);
        }
    }

    //output all contact to tblContact
    public  void showContact(List<Contact> c) {
        //clear old data
        tblContact.getItems().clear();

        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        //output contact in "c" to table view
        if (group.equals("All")) {
            for (Contact x : c) {
                tblContact.getItems().add(x);
            }
        } else {
            for (Contact x : c) {
                if (x.getGroup().equalsIgnoreCase(group))
                    tblContact.getItems().add(x);
            }
        }
    }
    //output all groups to dropdownlist
    public  void showGroup(List<Group> g) {
        cbGroup.getItems().add(new Group("All"));
        for (Group x : g) {
            cbGroup.getItems().add(x);
        }

        cbGroup.getSelectionModel().select(0);
    }
    //do corresponding actions for search, delete, update and add contact
    public void searchContact(ActionEvent evt) throws Exception{
        if (evt.getSource() == btnSearch) {
            String group = cbGroup.getSelectionModel().getSelectedItem().getName();
            List<Contact> c = contactDAO.search(contacts, group, search.getText());
            if (c.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Information");
                alert.setContentText("No contact found.");
                alert.showAndWait();
            } else {
                showContact(c);
            }
        } else if (evt.getSource() == btnAdd) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/addContact.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add a new Contact");
            stage.show();
            //past list of current contact to addContactController
            AddContactController c = loader.getController();
            c.setContacts(contacts);
            c.setAddContactController(this);
        } else if (evt.getSource() == btnDelete) {
            deleteContact();
        } else if (evt.getSource() == btnUpdate) {
            updateContact();
        } else if (evt.getSource() == btnGroup) {
            groupPanel();
        }
    }
    //manage the groups
    public void groupPanel() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/group.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Group Management");
        stage.show();
        //pass list of current contact to AddContactController
        GroupController c = loader.getController();
        c.setContactController(this);
    }

    //update a contact
    public  void updateContact() throws Exception {
        int i = tblContact.getSelectionModel().getSelectedIndex();
        if (i >= tblContact.getItems().size() || i < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Please select a contact to update");
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/updateContact.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Contact");
            stage.show();
            //pass list of current contact to AddContactController
            UpdateContactController c = loader.getController();
            c.setContacts(contacts);
            c.setContactController(this);
            c.setUpdatedContact((Contact) tblContact.getItems().get(i));
        }
    }
    //delete a selected contact
    public  void deleteContact() {
        int i = tblContact.getSelectionModel().getSelectedIndex();
        if (i >= tblContact.getItems().size() || i < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Please select a contact to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Do you want to delete this selected contact?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                contacts.remove(i);

                //update contact list
                showContact(contacts);
                try {
                    contactDAO.saveToFile(contacts, CONTACT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateContactGroup(String oldName, String newName) {
        for (Contact contact : contacts) {
            if (contact.getGroup().equalsIgnoreCase(oldName)) {
                contact.setGroup(newName);
            }
        }
    }
}

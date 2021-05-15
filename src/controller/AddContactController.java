package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddContactController {
    @FXML
    private TextField firstName, lastName, phone, email;
    @FXML
    private DatePicker dob;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private Label lblFirstName, lblLastName, lblPhone, lblEmail, lbldob;
    @FXML
    private Button btnAdd, btnClose;

    private ContactController contactController;
    private List<Contact> contacts;

    public void  setAddContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    public  void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @FXML
    void initialize() throws Exception {
        lblFirstName.setText("");
        lblLastName.setText("");
        lblPhone.setText("");
        lblEmail.setText("");
        lbldob.setText("");
        cbGroup.getItems().clear();
        for (Group x : new GroupDAO().loadGroup("data/group.txt"))
            cbGroup.getItems().add(x);
        cbGroup.getSelectionModel().select(0);
        dob.setValue(LocalDate.now());
    }

    public  void saveContact() throws Exception {
        String fname = firstName.getText().trim();
        if (fname.isEmpty()) {
            lblFirstName.setText("First name cannot be empty");
            return;
        }
        lblFirstName.setText("");

        String lname = lastName.getText().trim();
        if (lname.isEmpty()) {
            lblLastName.setText("Last name cannot be empty");
            return;
        }
        lblLastName.setText("");

        String mobile = phone.getText().trim();
        if (mobile.isEmpty() || !mobile.matches("\\d+")) {
            lblPhone.setText("Phone contains digit only");
            return;
        }
        lblPhone.setText("");

        String mail = email.getText().trim();
        Pattern emailNamePtrn = Pattern.compile("^[A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mtch = emailNamePtrn.matcher(mail);
        if (!mtch.matches()) {
            lblEmail.setText("Email is invalid");
            return;
        }
        lblEmail.setText("");

        String birthdate = dob.getValue().toString();
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        Contact c = new Contact(fname, lname, mobile, mail, birthdate, group);
        if (contactController.contactDAO.indexOf(contacts, c) == -1) {
            contactController.contactDAO.saveToList(contacts, c);
            contactController.showContact(contacts);

            //update data source
            contactController.contactDAO.saveToFile(contacts, "data/contact.txt");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("New contact has been added");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Information of contact is existed");
            alert.showAndWait();
        }
    }
    public  void saveContact(ActionEvent evt) throws Exception {
        if (evt.getSource() == btnAdd) {
            saveContact();
        } else if (evt.getSource() == btnClose) {
            final Node source = (Node) evt.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }
}

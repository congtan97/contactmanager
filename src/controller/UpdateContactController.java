package controller;

import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateContactController {
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
    private Contact updatedContact;

    public void  setContactController(ContactController contactController) {
        this.contactController = contactController;
    }

    public  void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public  void setUpdatedContact(Contact updatedContact) throws Exception {
        this.updatedContact = updatedContact;

        //output current information of updated contact
        firstName.setText(updatedContact.getFirstName());
        lastName.setText(updatedContact.getLastName());
        email.setText(updatedContact.getEmail());
        phone.setText(updatedContact.getPhone());

        //fill all groups to dropdown list
        cbGroup.getItems().clear();
        for (Group x: new GroupDAO().loadGroup("data/group.txt"))
            cbGroup.getItems().add(x);

        cbGroup.getSelectionModel().select(new Group(updatedContact.getGroup()));
        Date date = new SimpleDateFormat("MM-dd-yyyy").parse(updatedContact.getDob());
        dob.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @FXML
    void initialize() throws Exception {
        lblFirstName.setText("");
        lblLastName.setText("");
        lblPhone.setText("");
        lblEmail.setText("");
        lbldob.setText("");
    }

    public  void updateContact(ActionEvent evt) throws Exception {
        if (evt.getSource() == btnAdd) {
            saveContact();
        } else if (evt.getSource() == btnClose) {
            final Node source = (Node) evt.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    public  void saveContact() throws Exception{
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

        int i = contactController.contactDAO.indexOf(contacts, updatedContact);
        int j = contactController.contactDAO.indexOf(contacts, c);
        if (i == j) {
            contactController.contactDAO.updateContact(contacts, c, i);
            contactController.showContact(contacts);
            contactController.contactDAO.saveToFile(contacts, "data/contact.txt");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Contact has been updated");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText("Information of contact is existed");
            alert.showAndWait();
        }
    }

}

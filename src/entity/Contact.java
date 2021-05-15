package entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Contact {

    private String firstName, lastName;
    private String phone, email;
    private String dob;
    private String group;
    private String address;

    public Contact(String fName, String lName, String phone, String email, String dob, String group, String address) {
        this.dob = dob;
        this.email = email;
        this.firstName = fName;
        this.lastName = lName;
        this.phone = phone;
        this.group = group;
        this.address = address;
    }

    public Contact(String fName, String lName, String phone, String email, String dob, String group) {
        this.dob = dob;
        this.email = email;
        this.firstName = fName;
        this.lastName = lName;
        this.phone = phone;
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGroup() {
        return group;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String fName) {
        this.firstName= fName;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setLastName(String lName) {
        this.lastName = lName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {

        return String.format("%-15s:%-15s:%-10s:%-25s:%-10s:%-10s:%-10s\n",firstName,lastName,phone,email,dob,group, address);
    }

}


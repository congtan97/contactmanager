package dao;

import entity.Contact;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Vector;


public class ContactDAO {

    //load all Contacts from the file Contact in to a list
    public List<Contact> loadContact(String fname) throws Exception {
        //create an empty contact list
        List<Contact> c = new Vector<>();
        //open fname for reading
        LineNumberReader lnr = new LineNumberReader(new FileReader(fname));
        String line = "";
        while ((line = lnr.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] st = line.split(":");
                c.add(new Contact(st[0].trim(), st[1].trim(), st[2].trim(), st[3].trim(), st[4].trim(), st[5].trim(), st[6].trim()));
            }
        }
        lnr.close();
        return c;
    }

    //save all Contacts from a given list to a text file
    public  void saveToFile(List<Contact> g, String fname) throws Exception {
        //open fname for writing
        BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
        for (Contact x : g) {
            bw.write(x.toString());
        }
        bw.close();
    }

    //return the first position of a given contact g in the list
    //otherwise return -1
    public int indexOf(List<Contact> list, Contact g) {
        for (int i = 0; i < list.size(); i++) {
            Contact x = list.get(i);
            if (x.getFirstName().equalsIgnoreCase(g.getFirstName()) && x.getLastName().equalsIgnoreCase(g.getLastName()))
                return i;
        }
        return -1;
    }
    //save a Contact to a current list
    public  void saveToList(List<Contact> list, Contact g) {
        list.add(g);
    }
    //update information of a contact c at position i in the list
    public  void updateContact(List<Contact> list, Contact c, int i) {
        Contact x = list.get(i);
        x.setFirstName(c.getFirstName());
        x.setLastName(c.getLastName());
        x.setDob(c.getDob());
        x.setEmail(c.getEmail());
        x.setPhone(c.getPhone());
        x.setGroup(c.getGroup());
    }
    //return a list of Contact who information matched given search word
    public  List<Contact> search(List<Contact> c, String group, String search) {
        if (group.equals("All"))
            group = "";
        List<Contact> ct = new Vector<>();
        for (Contact x : c) {
            String s = x.toString().toLowerCase();
            if (s.contains(search.toLowerCase()) && x.getGroup().contains(group))
                ct.add(x);
        }
        return ct;
    }
    //return a list of Contact who is in a given group
    public  List<Contact> contactByGroup(List<Contact> c, String group) {
        if (group.equals("All"))
                return c;
        List<Contact> ct = new Vector<>();
        for (Contact x : c) {
            String s = x.getGroup().toLowerCase();
            if (s.contains(group.toLowerCase()))
                ct.add(x);
        }
        return ct;
    }
}

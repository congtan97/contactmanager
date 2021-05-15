package dao;


import entity.Group;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Vector;


public class GroupDAO {

    //load all groups from the file group in to a list
    public List<Group> loadGroup(String fname) throws Exception {
        //create an empty group list
        List<Group> g = new Vector<>();
        //open fname for reading
        LineNumberReader lnr = new LineNumberReader(new FileReader(fname));
        String line = "";
        while ((line = lnr.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty())
                g.add(new Group(line));
        }
        lnr.close();
        return g;
    }

    //save all groups from a given list to a text file
    public  void saveGroupToFile(List<Group> g, String fname) throws Exception {
        //open fname for writing
        BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
        for (Group x : g) {
            bw.write(x.toString());
        }
        bw.close();
    }

    //return the first position of a given contact group in the list
    //otherwise return -1
    public int indexOf(List<Group> list, Group g) {
        for (int i = 0; i <list.size(); i++) {
            Group x = list.get(i);
            if (x.getName().equalsIgnoreCase(g.getName()))
                return i;
        }
        return -1;
    }
    //save a group to a current list
    public  void saveGroupToList(List<Group> list, Group g) {
        list.add(g);
    }

    //return a list of Contact who information matched given search word
    public  List<Group> search(List<Group> c, String search) {
        List<Group> ct = new Vector<>();
        for (Group x : c) {
            String s = x.toString().toLowerCase();
            if (s.contains(search.toLowerCase()))
                ct.add(x);
        }
        return ct;
    }

    //update a group in groups by a newGroup
    public  boolean updateGroup(List<Group> groups, int i, String oldGroup, String newGroup) {
        groups.get(i).setName(newGroup);
        //check if there is duplicate name
        int c = 0;
        for (Group x : groups) {
            if (x.getName().equalsIgnoreCase(newGroup))
                c++;
        }
        if (c >= 2) {
            //reset to old name
            groups.get(i).setName(oldGroup);
            return false;
        }
        return true;
    }
}

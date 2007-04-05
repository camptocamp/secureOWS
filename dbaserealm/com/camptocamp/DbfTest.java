package com.camptocamp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

/**
 * Sample used to see javadbf usage 
 *
 */
public class DbfTest {

    private Map userToPassword = new HashMap();

    private Map userToGroups = new HashMap();

    private void readUsers() {
        try {

            String dbfPath = "/tmp/test_dbf.dbf";

            // create a DBFReader object
            //
            InputStream inputStream = new FileInputStream(dbfPath); // take dbf
                                                                    // file as
                                                                    // program
                                                                    // argument
            DBFReader reader = new DBFReader(inputStream);

            Object[] rowObjects;

            while ((rowObjects = reader.nextRecord()) != null) {

                String user = (String) rowObjects[0];
                String password = (String) rowObjects[1];

                userToPassword.put(user, password);

                String groupsString = (String) rowObjects[2];

                String[] groups = groupsString.split(",");
                userToGroups.put(user, groups);

                System.out.println("user: " + user + " pw: " + password
                        + " groups: " + groups);

            }

            inputStream.close();

        } catch (Exception e) {
            System.out.println("Error while reading user files");
        }

    }

    public static void main(String args[]) {

        DbfTest t = new DbfTest();
        t.readUsers();
    }
}
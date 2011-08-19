package com.jivesoftware.toolbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NavigableSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/***
 * Various static methods for writing to the hbase
 * 
 * @author justin.kikuchi
 */
public class HbaseTools {

    /***
     * This method adds a row to the table, if one exits it overwrites it. It's
     * parameters are updated information
     * 
     * @param table
     *            HTable to put to
     * @param email
     *            Unique identifier for the row
     * @param lastCommit
     *            Most recent commit
     * @param badgesWeek
     *            Number of badges in the current week
     * @param numBugs
     *            Number of total bugs
     * @param numCommits
     *            Total number of bugs
     * @param consecCommits
     *            Number of consecutive commits by day
     * @param newBadges
     *            Newly acquired badges
     * @param badges
     *            already acquired badges
     */
    public static void addRow(HTable table, String email, String lastCommit,
            int numBugs, int numCommits, int consecCommits, String newBadges,
            String[] badges, String pushTime) {

        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("lastCommit"),
                Bytes.toBytes(lastCommit));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("numBugs"),
                Bytes.toBytes(numBugs));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("numCommits"),
                Bytes.toBytes(numCommits));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("consecCommits"),
                Bytes.toBytes(consecCommits));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"),
                Bytes.toBytes(newBadges));
        if (badges != null) {
            for (int i = 0; i < badges.length; i++) {
                row.add(Bytes.toBytes("Badge"), Bytes.toBytes(badges[i]),
                        Bytes.toBytes(pushTime));
            }
        }
        try {
            table.put(row);
        } catch (Exception e) {
            System.err.println();
        }
    }

    /**
     * EXPECTS a hbase configuration file to be in tomcat home conf/
     * 
     * @return a configuration for the HBase our Jive App uses
     */
    public static Configuration getHBaseConfiguration() {
        Configuration config = HBaseConfiguration.create();
        Path file = new Path("/home/interns/apache-tomcat-7.0.19/conf/hbase_conf.xml");
        config.addResource(file);
        return config;
    }

    /***
     * This method sets up the hbase configuration.
     * 
     * @return Object array consisting of a HTable(1) and the config object(0)
     * @throws Exception
     */
    public static HTable getTable(Configuration config) throws Exception {

        // Create a table
        HBaseAdmin admin = new HBaseAdmin(config);
        if (!admin.tableExists("EmpBadges")) {
            admin.createTable(new HTableDescriptor("EmpBadges"));
            admin.disableTable("EmpBadges");
            admin.addColumn("EmpBadges", new HColumnDescriptor("Info"));
            admin.addColumn("EmpBadges", new HColumnDescriptor("Badge"));
            admin.enableTable("EmpBadges");
        }
        HTable table = new HTable(config, "EmpBadges");
        return table;
    }

    /***
     * Deletes row from the HBase
     * 
     * @param table
     *            HTable to delete row from
     * @param email
     *            Identifies row to delete
     */
    public static void deleteRow(HTable table, String email) {
        Delete d = new Delete(Bytes.toBytes(email));

        try {
            table.delete(d);
        } catch (Exception e) {
            System.err.println();
        }
    }

    /**
     * Updates the user's boss's email if the user has a new boss
     * 
     * @param table
     *            The Employee table in the HBase
     * @param email
     *            the user's email
     * @param bossEmail
     *            the user's boss's email
     */
    public static void addUserOrUpdateBoss(HTable table, String email,
            String bossEmail, String name, String id) {
        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("bossEmail"),
                Bytes.toBytes(bossEmail));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("name"),
                Bytes.toBytes(name));
        row.add(Bytes.toBytes("Info"), Bytes.toBytes("id"), Bytes.toBytes(id));

        try {
            table.put(row);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /***
     * This method updates the boss on the specified row
     * 
     * @param table
     *            HTable to alter
     * @param email
     *            Row Identifier
     * @param bossEmail
     *            New boss email
     */
    public static void updateBoss(HTable table, String email, String bossEmail) {
        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("bossEmail"),
                Bytes.toBytes(bossEmail));

        try {
            table.put(row);
        } catch (Exception e) {
            System.err.println();
        }
    }

    /***
     * This method retrieves the last commit from the HBase
     * 
     * @param data
     *            The data from the hbase
     * 
     * @return The String of the last commit date
     */
    public static String getLastCommit(Result data, Calendar newDate) {

        String lastCommit = "";
        try {
            lastCommit = new String(data.getValue(Bytes.toBytes("Info"),
                    Bytes.toBytes("lastCommit")));
        } catch (java.lang.NullPointerException e) {
            return String.valueOf((newDate.getTime().getTime()) / 1000) + " "
                    + newDate.getTimeZone().getID();
        }
        if (lastCommit.isEmpty()) {
            return String.valueOf(newDate.getTime().getTime() / 1000) + " "
                    + newDate.getTimeZone().getID();
        }
        return lastCommit;
    }

    /***
     * Gets the user id from the hbase, to use from external activity stream
     * post
     * 
     * @param data
     *            The data form the hbase for the specific email
     * @return String of the id
     */
    public static String getUserId(Result data) {
        String id = "";
        try {
            id = new String(data.getValue(Bytes.toBytes("Info"),
                    Bytes.toBytes("id")));
        } catch (java.lang.NullPointerException e) {
            return "";
        }
        if (id.isEmpty()) {
            return "";
        }
        return id;

    }

    /***
     * This method extracts the user name from the data from the hbase
     * 
     * @param data
     *            The data from the row in the hbase
     * @return the name of the user
     */
    public static String getName(Result data) {
        String name = "";
        try {
            name = new String(data.getValue(Bytes.toBytes("Info"),
                    Bytes.toBytes("name")));
        } catch (java.lang.NullPointerException e) {
            return "";
        }
        if (name.isEmpty()) {
            return "";
        }
        return name;

    }

    /***
     * This method retrieves the specified integer fields from the HBase.
     * 
     * @param data
     *            The data from the hbase
     * @param fields
     *            Column names to retrieve
     * @return integer array of the fields requested
     */
    public static int[] getFields(Result data, String[] fields) {
        int[] results = new int[fields.length];

        for (int i = 0; i < fields.length; i++) {
            results[i] = byteArrayToInt(data.getValue(Bytes.toBytes("Info"),
                    Bytes.toBytes(fields[i])));
        }
        return results;
    }

    /***
     * This method retrieves the data from the hbase
     * 
     * @param table
     *            appropriate table to grab data from
     * @param email
     *            row identifier
     * @return
     */
    public static Result getRowData(HTable table, String email) {
        Get get = new Get(Bytes.toBytes(email));
        Result data = null;

        try {
            data = table.get(get);
        } catch (Exception e) {
            System.err.println();
        }

        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    /**
     * Empties the value in the new Badges column for a specific Employee
     * 
     * @param table
     *            Employee HBase table
     * @param email
     *            the user's email
     */
    public static void resetNewBadges(HTable table, String email) {
        Put row = new Put(Bytes.toBytes(email));

        row.add(Bytes.toBytes("Info"), Bytes.toBytes("newBadges"),
                Bytes.toBytes(""));

        try {
            table.put(row);
        } catch (Exception e) {
            System.err.println();
        }

    }

    /***
     * This testing method prints the specified row in the HBase
     * 
     * @param data
     *            The data from the hbase
     */
    public static void test(Result data) {
        System.out
                .println("Printing data........................................................");
        @SuppressWarnings("unchecked")
        ArrayList<String> badges_awarded = (ArrayList<String>) (getBadges(data))[0];
        String newBadges = (String) (getBadges(data))[1];
        System.out.println("newBadges: " + newBadges);
        for (int i = 0; i < badges_awarded.size(); i++) {
            System.out.println("Badges=\t" + badges_awarded.get(i));
        }
        String[] fields = { "badgesWeek", "numBugs", "numCommits",
                "consecCommits" };
        int[] results = getFields(data, fields);
        System.out.println("1numbugs=\t" + results[1]);
        System.out.println("1numCommits=\t" + results[2]);
        System.out.println("1consecCommits=\t" + results[3]);
        System.out
                .println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    /**
     * Gets Badges from the specified row in the hbase, used for the Appservlet
     * 
     * @param table
     *            The Employee table in the HBase
     * @param email
     *            the row key of the Employee HBase table
     * @return Object[0] = array list of obtained badges Object[1] = String of
     *         Badge ID's delimited by spaces of newly obtained Badges
     */
    public static Object[] getBadges(HTable table, String email) {
        Get get = new Get(Bytes.toBytes(email));
        Result data = null;
        Object[] output = new Object[2];
        ArrayList<String> resultingBadges = new ArrayList<String>();
        String newBadges = "";
        output[0] = resultingBadges;
        output[1] = newBadges;
        try {
            data = table.get(get);
        } catch (Exception e) {
            System.err.println();
        }

        if (data == null) {
            System.out.println("Not found");
            return output;
        }
        if (data.isEmpty()) {
            return output;
        }

        byte[] badges = Bytes.toBytes("Badge");

        NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges)
                .descendingKeySet();

        for (byte[] badge : badges_awarded) {
            resultingBadges.add(new String(badge));
        }

        try {
            newBadges = new String(data.getValue(Bytes.toBytes("Info"),
                    Bytes.toBytes("newBadges")));
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }

        // String[] result = new String[resultingBadges.size()];
        // result = resultingBadges.toArray(result);
        output[0] = resultingBadges;
        output[1] = newBadges;
        return output;
    }

    /***
     * Returns the acquired and new badges, used in the backend servlet
     * 
     * @param data
     *            The data from the hbase
     * 
     * @return Object[0] is an ArrayList of acquired badges, Object[1] is newly
     *         acquired badges
     */
    public static Object[] getBadges(Result data) {
        Object[] output = new Object[2];
        ArrayList<String> resultingBadges = new ArrayList<String>();
        String newBadges = "";
        output[0] = resultingBadges;
        output[1] = newBadges;

        if (data == null) {
            System.out.println("Not found");
            return output;
        }
        if (data.isEmpty()) {
            return output;
        }

        byte[] badges = Bytes.toBytes("Badge");

        NavigableSet<byte[]> badges_awarded = data.getFamilyMap(badges)
                .descendingKeySet();
        for (byte[] badge : badges_awarded) {
            resultingBadges.add(new String(badge));
        }
        newBadges = new String(data.getValue(Bytes.toBytes("Info"),
                Bytes.toBytes("newBadges")));

        output[0] = resultingBadges;
        output[1] = newBadges;
        return output;
    }

    /***
     * This method gets the date of each achieved badge
     * 
     * @param data
     *            The data from the row in the hbase
     * @return An arraylist with string representations of the date
     */
    public static ArrayList<String> getBadgeDates(Result data) {
        ArrayList<String> dates = new ArrayList<String>();
        if (data == null) {
            System.out.println("Not found");
            return dates;
        }
        if (data.isEmpty()) {
            return dates;
        }

        NavigableSet<byte[]> badges_awarded = data.getFamilyMap(
                Bytes.toBytes("Badge")).descendingKeySet();
        for (byte[] badge : badges_awarded) {
            dates.add(new String(data.getValue(Bytes.toBytes("Badge"), badge)));
        }

        return dates;
    }

    /***
     * This method is a helper function to getFields that converts a byte array
     * to an integer
     * 
     * @param b
     *            The byte array to convert
     * @return integer value of the byte array
     */
    private static int byteArrayToInt(byte[] b) {
        int value = 0;
        try {
            for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                value += (b[i] & 0x000000FF) << shift;
            }
        } catch (java.lang.NullPointerException e) {
            return 0;
        }
        return value;
    }
}
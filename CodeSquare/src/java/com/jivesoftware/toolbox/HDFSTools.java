package com.jivesoftware.toolbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.jivesoftware.backendServlet.Commit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;

/***
 * Tools used to write to the HDFS
 * 
 * @author diivanand.ramalingam
 * @author justin.kikuchi
 */
public class HDFSTools {

    /**
     * EXPECTS a configuration file in the tomcat conf/ folder
     * 
     * @return the Configuration to the HDFS that contains our folders that
     *         store out commits
     */
    public static Configuration getConfiguration() {
        InputStream in = null;
        Configuration config = new Configuration();
        try {
            
            File f = new File(".");
            System.out.println("I am located here: " + f.getAbsolutePath());
            System.out.println("My canon path: " + f.getCanonicalPath());
            Path file = new Path("/home/interns/apache-tomcat-7.0.19/conf/hdfs_conf.xml");
            config.addResource(file);
            return config;
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
        return config;
    }

    /***
     * Makes the path in the HDFS for the commit file to be written to
     * 
     * @param dfs
     * @param date
     * @return Path the path to where the file is to be written
     * @throws IOException
     */
    public static Path makePath(FileSystem dfs, Calendar date)
            throws IOException {
        Path src = new Path("/user/interns/Commits");
        String[] extensions = { ((Integer) date.get(Calendar.YEAR)).toString(),
                ((Integer) (date.get(Calendar.MONTH) + 1)).toString(),
                ((Integer) date.get(Calendar.DAY_OF_MONTH)).toString(),
                ((Integer) date.get(Calendar.HOUR_OF_DAY)).toString(),
                ((Integer) date.get(Calendar.MINUTE)).toString() };
        for (int i = 0; i < extensions.length; i++) {
            src = new Path(src.toString() + "/" + extensions[i]);
            if (!dfs.exists(src)) {
                dfs.mkdirs(src);
            }
        }
        return src;
    }

    /**
     * Writes the Commit object as a string in it's own .txt file in the
     * appropriate folder in HDFS
     * 
     * @param dfs
     *            The HDFS that contains our folders
     * @param c
     *            The Commit object
     * @throws IOException
     */
    public static boolean writeCommitToHDFS(FileSystem dfs, Commit c)
            throws IOException {
        // If the folders don't exist, create them
        Path src = makePath(dfs, c.getPushDate().getLocal());

        // Write file
        src = new Path(src.toString() + "/" + c.getId() + ".txt");
        if (dfs.exists(src)) {
            return false;
        }
        FSDataOutputStream fs = dfs.create(src);
        fs.write(c.toString().getBytes());
        fs.close();
        System.out.println("File Written: " + src.toString() + "; "
                + c.toString());
        return true;
    }

}
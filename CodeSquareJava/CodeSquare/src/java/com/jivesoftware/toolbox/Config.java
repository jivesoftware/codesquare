/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.toolbox;

import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/**
 *
 * @author justin.kikuchi
 */
//import org.apache.commons.configuration.Configuration;
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.PropertiesConfiguration;

public class Config {
    private String jive_id = "";
    private String app_id = "";
    private String key = "";
    private String secret = "";
    /*public Config(){
        Configuration config = null;
        try {
            config = new PropertiesConfiguration("conf/testing.properties");
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.jive_id = config.getString("jive_id");
        this.app_id = config.getString("app_id");
        this.key = config.getString("key");
        this.secret = config.getString("secret");
        
        System.out.println(jive_id);
        System.out.println(secret);
    }*/
    public static void test(){
        Configuration config = new Configuration();
        Path file = new Path("conf/hdfs_conf.xml");
        config.addResource(file);
    }
}

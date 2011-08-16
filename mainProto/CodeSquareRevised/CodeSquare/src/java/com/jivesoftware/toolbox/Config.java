/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jivesoftware.toolbox;

/**
 *
 * @author justin.kikuchi
 */
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Config {
    public static void test(){

        // Using a properties file
        Configuration config = null;
        try {
            config = new PropertiesConfiguration("conf/testing.properties");
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String day = config.getString("day");
        System.out.println(day);
  }
}

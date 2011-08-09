package com.jivesoftware.backendServlet;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class JiveDate {

    private Date date;
    private Calendar localDate;
    private Calendar globalDate;
    
    
    /**
     * add date to Commit - parses unixtimestamp
     * to be used for push time
     * @param dateString
     *            A String containing unixtimestamp
     * @param timeZone
     * 			  A String cointaining timezone
     *  // isoTime is in ISO 8601 Format
     *  // global = GMT-7 (aka PDT)
     * local date is PDT
     * global date is wherever they are
     */
    // only for commitDate!
    public JiveDate(String unixTime, String timeZone){
        System.out.println("JIVEDATEHERE");
        Long uTime = Long.parseLong(unixTime);
    	date = new Date(uTime*1000);
    	
    	localDate = Calendar.getInstance();
    	localDate.setTime(date);
    	localDate.setTimeZone(TimeZone.getTimeZone("GMT-7"));
    	
    	globalDate = Calendar.getInstance();
    	globalDate.setTime(date);
    	globalDate.setTimeZone(TimeZone.getTimeZone("GMT"+timeZone));
        System.out.println(globalDate);
        System.out.println(localDate);
    }
    
    // only for pushDate!
        public JiveDate(String unixTime){
        Long uTime = Long.parseLong(unixTime);
    	date = new Date(uTime*1000);
    	
    	localDate = Calendar.getInstance();
    	localDate.setTime(date);
    	localDate.setTimeZone(TimeZone.getTimeZone("GMT-7"));
    }
    
    // NOTE: decided to forgo setter methods because it should all be 
    // handled in the constructor
    
    
    public boolean equalsLocal(int month, int date) {
         if (localDate.get(Calendar.MONTH) == month 
                 && localDate.get(Calendar.DAY_OF_MONTH) == date)  {
             return true;
         }  
         return false;
        }

    public boolean equalsGlobal(int month, int date) {
         if (globalDate.get(Calendar.MONTH) == month 
                 && globalDate.get(Calendar.DAY_OF_MONTH) == date)  {
             return true;
         }  
         return false;
        }
    
    /**
     * 
     * @return globalDate
     */
    public Calendar getGlobal() {
        return globalDate; }

    /**
     * 
     * @return globalDate's date (day of month)
     */
    public int getGlobalDate() {
    	return globalDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 
     * @return globalDate's day (day of week)
     */
    public int getGlobalDay() {
    	return globalDate.get(Calendar.DAY_OF_WEEK);
    }
    
    /**
     * 
     * @return globalDate's hour (24)
     */
    public int getGlobalHour() {
    	return globalDate.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 
     * @return globalDate's minute
     */
    public int getGlobalMinute() {
    	return globalDate.get(Calendar.MINUTE);
    }
    
    /**
     * 
     * @return globalDate's month
     */
    public int getGlobalMonth() {
    	return globalDate.get(Calendar.MONTH);
    }
    
    
    /**
     * 
     * @return globalDate's second
     */
    public int getGlobalSecond() {
    	return globalDate.get(Calendar.SECOND);
    }
    
    /**
     * 
     * @return globalDate's year
     */
    public int getGlobalYear() {
    	return globalDate.get(Calendar.YEAR);
    }
   
    
    /**
     * 
     * @return localDate
     */
    public Calendar getLocal() {
        return localDate; }
    
    /**
     * 
     * @return localDate's date (day of month)
     */
    public int getLocalDate() {
    	return localDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 
     * @return localDate's day (day of week)
     */
    public int getLocalDay() {
    	return localDate.get(Calendar.DAY_OF_WEEK);
    }
    
    /**
     * 
     * @return localDate's hour (24)
     */
    public int getLocalHour() {
    	return localDate.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 
     * @return localDate's minute
     */
    public int getLocalMinute() {
    	return localDate.get(Calendar.MINUTE);
    }
    
    /**
     * 
     * @return localDate's month
     */
    public int getLocalMonth() {
    	return localDate.get(Calendar.MONTH);
    }
      
    /**
     * 
     * @return localDate's second
     */
    public int getLocalSecond() {
    	return localDate.get(Calendar.SECOND);
    }
    
    /**
     * 
     * @return localDate's year
     */
    public int getLocalYear() {
    	return localDate.get(Calendar.YEAR);
    }
    
    /**
     * 
     * @return localDate's Date String - 
     */
    public int getLocalDateString() {
    	return localDate.get(Calendar.YEAR);
    }    
    
   
    // toStrings
    
    public String localToString() {
        return (localDate.getTime().getTime()/1000)+" "+localDate.getTimeZone();
    }
    
    public String globalToString() {
        return (globalDate.getTime().getTime()/1000)+" "+globalDate.getTimeZone();
    }
}
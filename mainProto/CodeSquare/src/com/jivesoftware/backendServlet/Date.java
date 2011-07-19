package com.jivesoftware.backendServlet;

import java.util.StringTokenizer;

public class Date {

    private int year;
    private int month;
    private String day;
    private int date;
    private int hour;
    private int minute;
    private int second;
    
    /**
     * Constructs an empty commit object
     * 
     */
    public Date() {
    }
    
    /**
     * add date to Commit - parses all --> Thu Jun 16 11:37:51 2011 -0700 info
     * @param dateString
     *            A String containing date information to be parsed
     */
    public Date(String dateString) {
        
        StringTokenizer st = new StringTokenizer(dateString.trim(), " ");
        day = st.nextToken();
        String tempMonth = st.nextToken();
        date = (Integer) st.nextElement();
        String tempTime = st.nextToken();
        year = (Integer) st.nextElement();
        
        StringTokenizer st2 = new StringTokenizer(tempTime, ":");
        hour = (Integer) st2.nextElement();
        minute = (Integer) st2.nextElement();
        second = (Integer) st2.nextElement();
        
        String months[] = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
        for(int i=1; i<months.length; i++){
            if (tempMonth.equals(months[i])) {
                month = i;
            }
            else {
                month = -1;
            }
        }
    }
    
    /**
     * Constructs a commit object
     * 
     */
    public Date(int year, int month, String day, int date, int hour, int minute, int second) {
        this.year = year; this.month = month;
        this.day = day; this.date = date; this.hour = hour;
        this.minute = minute; this.second = second;
    }
    
    /**
     * 
     * @return commit year
     */
    public int getYear() {
        return year; }
    
    /**
     * 
     * set commit year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * 
     * @return commit month
     */
    public int getMonth() {
        return month; }
    
    /**
     * 
     * set commit month
     */
    public void setMonth(int month) {
        this.month = month;
    }
    
    /**
     * 
     * @return commit day
     */
    public String getDay() {
        return day; }
    
    /**
     * 
     * set commit day
     */
    public void setDay(String day) {
        this.day = day; }
    
    /**
     * 
     * @return commit date
     */
    public int getDate() {
        return date; }
    
    /**
     * 
     * set commit date
     */
    public void setDate(int date) {
        this.date = date; }    
    
    /**
     * 
     * @return commit hour
     */
    public int getHour() {
        return hour; }
    
    /**
     * 
     * set commit hour
     */
    public void setHour(int hour) {
        this.hour = hour; }

    /**
     * 
     * @return commit minute
     */
    public int getMinute() {
        return minute; }
    
    /**
     * 
     * set commit minute
     */
    public void setMinute(int minute) {
        this.minute = minute; }
    
    /**
     * 
     * @return commit second
     */
    public int getSecond() {
        return second; }
    
    /**
     * 
     * set commit second
     */
    public void setSecond(int second) {
        this.second = second; }
    
    public String toString() {
        return day+" "+year+"-"+month+"-"+date+" "+hour+" "+minute+" "+second;
    }

}
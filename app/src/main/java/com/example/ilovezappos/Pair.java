package com.example.ilovezappos;

import java.util.Date;

public class Pair {
    private long epochDate;
    private float price;
    private Date date;

    public void setDate(long unixDate){
        this.date = epochToDate(unixDate);
    }

    public Date getDate(){
        return date;
    }

    public long getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(long epochDate) {
        this.epochDate = epochDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date epochToDate(long unixDate){
        Date dt = new Date(unixDate*1000L);
        return dt;

    }
}

package com.solanki.sahil.gojek.data.model;

public class Items {

    public String day, temp;

    public Items(String day, String temp) {
        this.day = day;
        this.temp = temp;
    }

    public Items() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}

package ru.sash0k.bluetooth_terminal.activity;

public class realtime {
    Float bpm,ax,ay,az;
    int min,hrs;
    Float tempature;

    String time;

    public realtime(){
    }

    public Float getBpm() {
        return bpm;
    }

    public void setBpm(Float bpm) {
        this.bpm = bpm;
    }

    public Float getAx() {
        return ax;
    }

    public void setAx(Float ax) {
        this.ax = ax;
    }

    public Float getAy() {
        return ay;
    }

    public void setAy(Float ay) {
        this.ay = ay;
    }

    public Float getAz() {
        return az;
    }

    public void setAz(Float az) {
        this.az = az;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getHrs() {
        return hrs;
    }

    public void setHrs(int hrs) {
        this.hrs = hrs;
    }

    public Float getTempature() {
        return tempature;
    }

    public void setTempature(Float tempature) {
        this.tempature = tempature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

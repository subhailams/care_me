package ru.sash0k.bluetooth_terminal.activity;

public class PointValue {
    int xValue,yValue;

    public PointValue() {
    }

    public PointValue(int xValue,int yValue) {
        this.xValue = xValue;
        this.yValue=yValue;
    }
    public int getxValue(){
        return xValue;
    }
    public int getyValue(){
        return yValue;
    }

}

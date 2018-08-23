package com.example.administrator.testecgview;

public class EcgDataBean {
    //数据
    private int data;
    //时间戳
    private long time;
  /*  //是否绘制时间
    private boolean drawTime;

    public boolean isDrawTime() {
        return drawTime;
    }

    public void setDrawTime(boolean drawTime) {
        this.drawTime = drawTime;
    }*/

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

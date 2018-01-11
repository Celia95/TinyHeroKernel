package com.raincoatmoon.TimeManager;

public class CronTask {
    private int time;
    private int userID;
    private String cmd;
    private String info;

    public CronTask(int time, int userID, String cmd, String info) {
        this.time = time;
        this.userID = userID;
        this.cmd = cmd;
        this.info = info;
    }

    public int getTime() {
        return time;
    }

    public int getUserID() {
        return userID;
    }

    public String getCmd() {
        return cmd;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return getInfo() + " @ " + time/3600 + "h";
    }
}

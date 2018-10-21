package com.example.android.seizurenotifier;

public class Seizure {
    private String id;
    private String desc;
    private String duration;
    private String date;
    private String possibleTriggers;
    private String startTime;
    private String endTime;
    private String injuries;
    private boolean isEmergency;

    public Seizure(String desc, String duration, String possibleTriggers, String startTime, String endTime, String injuries, boolean isEmergency,String id,String date) {
        this.desc = desc;
        this.date = date;
        this.duration = duration;
        this.possibleTriggers = possibleTriggers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.injuries = injuries;
        this.isEmergency = isEmergency;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getDuration() {
        return duration;
    }

    public String getPossibleTriggers() {
        return possibleTriggers;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getInjuries() {
        return injuries;
    }

    public boolean isEmergency() {
        return isEmergency;
    }
}

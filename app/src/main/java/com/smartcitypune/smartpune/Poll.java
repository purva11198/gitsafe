package com.smartcitypune.smartpune;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public  class Poll implements Serializable{

    public String attempted;
    public String enddate;
    public String question;
    public String startdate;
    public ArrayList<Integer> count;
    public ArrayList<String> responses;
    public ArrayList<String> uid;

    //default constructor
    public Poll(){

    }

    public ArrayList<String> getUid() {
        return uid;
    }

    public void setUid(ArrayList<String> uid) {
        this.uid = uid;
    }

    public Poll(String attempted, String enddate, String question, String startdate, ArrayList<Integer> count, ArrayList<String> responses, ArrayList<String> uid) {
        this.attempted = attempted;
        this.enddate = enddate;
        this.question = question;
        this.startdate = startdate;
        this.count = count;
        this.responses = responses;
        this.uid = uid;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public ArrayList<Integer> getCount() {
        return count;
    }

    public void setCount(ArrayList<Integer> count) {
        this.count = count;
    }

    public ArrayList<String> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<String> responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "Poll{" +
                "attempted='" + attempted + '\'' +
                ", enddate='" + enddate + '\'' +
                ", question='" + question + '\'' +
                ", startdate='" + startdate + '\'' +
                ", count=" + count +
                ", responses=" + responses +
                '}';
    }
}

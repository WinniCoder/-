package com.whu_phone.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wuhui on 2017/2/20.
 */

@Table(name = "Course")
public class MyCourse extends Model {
    @Column(name = "name")
    private String name;       //课程名
    @Column(name = "place")
    private String place;      //上课地点
    @Column(name = "start_week")
    private int startWeek;     //开始周
    @Column(name = "end_week")
    private int endWeek;       //结束周
    @Column(name = "day")
    private int day;       //上课日子
    @Column(name = "teacher")
    private String teacher;        //教课老师
    @Column(name = "start_num")
    private int startTime;      //课开始节数
    @Column(name = "end_num")
    private int endTime;        //课下课节数

    public MyCourse() {
        super();
    }

    public MyCourse(String name, String place, int startWeek, int endWeek, int day, String teacher, int startTime, int endTime) {
        super();
        this.name = name;
        this.place = place;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.day = day;
        this.teacher = teacher;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public int getDay() {
        return day;
    }

    public String getTeacher() {
        return teacher;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
}


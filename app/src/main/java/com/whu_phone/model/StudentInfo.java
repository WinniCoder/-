package com.whu_phone.model;

import java.io.Serializable;

/**
 * Created by wuhui on 2017/2/21.
 */

public class StudentInfo implements Serializable {
    public static final long serialVersionUID=1L;

    String studentID;
    String name;
    String sex;
    String grade;
    String college;
    String major;
    String dormitory;
    String studentType;
    String namePinyin;
    String joinDay;
    String birthday;
    String token;

    public StudentInfo(String birthday, String college, String dormitory, String grade, String joinDay, String major, String name, String namePinyin, String sex, String studentID, String studentType, String token) {
        this.birthday = birthday;
        this.college = college;
        this.dormitory = dormitory;
        this.grade = grade;
        this.joinDay = joinDay;
        this.major = major;
        this.name = name;
        this.namePinyin = namePinyin;
        this.sex = sex;
        this.studentID = studentID;
        this.studentType = studentType;
        this.token = token;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCollege() {
        return college;
    }

    public String getDormitory() {
        return dormitory;
    }

    public String getGrade() {
        return grade;
    }

    public String getJoinDay() {
        return joinDay;
    }

    public String getMajor() {
        return major;
    }

    public String getName() {
        return name;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public String getSex() {
        return sex;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getStudentType() {
        return studentType;
    }

    public String getToken() {
        return token;
    }
}

package com.whu_phone.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by wuhui on 2017/2/25.
 */

@Table(name = "AnnouncementInfo")
public class AnnouncementInfo extends Model{
    @Column(name = "title")
    private String title;
    @Column(name = "time")
    private String time;
    @Column(name = "department")
    private String department;
    @Column(name = "url")
    private String url;

    public AnnouncementInfo() {
        super();
    }

    public AnnouncementInfo(String title, String time, String department, String url) {
        super();
        this.title = title;
        this.time = time;
        this.department = department;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDepartment() {
        return department;
    }

    public String getUrl() {
        return url;
    }

}

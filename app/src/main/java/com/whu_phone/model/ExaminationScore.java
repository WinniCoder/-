package com.whu_phone.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by wuhui on 2017/3/2.
 */

@Table(name = "Score")
public class ExaminationScore extends Model {
    @Column(name = "name")
    private String name;
    @Column(name = "category")
    private String category;
    @Column(name = "term")
    private String term;
    @Column(name = "credit")
    private String credit;
    @Column(name = "grade")
    private String grade;

    public ExaminationScore() {
        super();
    }

    public ExaminationScore(String name, String category, String term, String credit, String grade) {
        super();
        this.name = name;
        this.category = category;
        this.term = term;
        this.credit = credit;
        this.grade = grade;
    }

    public String getCategory() {
        return category;
    }

    public String getCredit() {
        return credit;
    }

    public String getGrade() {
        return grade;
    }

    public String getName() {
        return name;
    }

    public String getTerm() {
        return term;
    }
}

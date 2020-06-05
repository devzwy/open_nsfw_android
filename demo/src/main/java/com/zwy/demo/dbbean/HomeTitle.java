package com.zwy.demo.dbbean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class HomeTitle {
    @Id
    private Long id;
    private String title;

    @Generated(hash = 2016918462)
    public HomeTitle(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Generated(hash = 869810785)
    public HomeTitle() {
    }

    public HomeTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

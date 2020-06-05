package com.zwy.demo.dbbean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class ImageBean {
    private String createTime;
    @Id
    private Long id;
    private String imgUrl = "";
    private Float nsfw = 0.0f;
    private Float sfw = 0.0f;


    @Generated(hash = 716432991)
    public ImageBean(String createTime, Long id, String imgUrl, Float nsfw,
                     Float sfw) {
        this.createTime = createTime;
        this.id = id;
        this.imgUrl = imgUrl;
        this.nsfw = nsfw;
        this.sfw = sfw;
    }

    public ImageBean(String imgUrl, Float nsfw,
                     Float sfw) {
        this.imgUrl = imgUrl;
        this.nsfw = nsfw;
        this.sfw = sfw;
    }

    @Generated(hash = 645668394)
    public ImageBean() {
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Float getNsfw() {
        return this.nsfw;
    }

    public void setNsfw(Float nsfw) {
        this.nsfw = nsfw;
    }

    public Float getSfw() {
        return this.sfw;
    }

    public void setSfw(Float sfw) {
        this.sfw = sfw;
    }

}

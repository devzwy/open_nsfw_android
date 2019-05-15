package com.zwy.nsfw.api;

public class NsfwBean {
    private float sfw;
    private float nsfw;

    public NsfwBean(float sfw, float nsfw) {
        this.sfw = sfw;
        this.nsfw = nsfw;
    }

    public float getSfw() {
        return sfw;
    }

    public float getNsfw() {
        return nsfw;
    }

    @Override
    public String toString() {
        return "NsfwBean{" +
                "sfw=" + sfw +
                ", nsfw=" + nsfw +
                '}';
    }
}

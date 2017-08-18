package com.zy.vplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 播放记录
 */
public class RecordEntity implements Serializable,Parcelable{
    private String path;
    private long duration;
    private long lastPlayPosition;

    public RecordEntity() {
    }

    public RecordEntity(String path, long duration, long lastPlayPosition) {
        this.path = path;
        this.duration = duration;
        this.lastPlayPosition = lastPlayPosition;
    }

    protected RecordEntity(Parcel in) {
        path = in.readString();
        duration = in.readLong();
        lastPlayPosition = in.readLong();
    }

    public static final Creator<RecordEntity> CREATOR = new Creator<RecordEntity>() {
        @Override
        public RecordEntity createFromParcel(Parcel in) {
            return new RecordEntity(in);
        }

        @Override
        public RecordEntity[] newArray(int size) {
            return new RecordEntity[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastPlayPosition() {
        return lastPlayPosition;
    }

    public void setLastPlayPosition(long lastPlayPosition) {
        this.lastPlayPosition = lastPlayPosition;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "RecordEntity{" +
                "path='" + path + '\'' +
                ", duration=" + duration +
                ", lastPlayPosition=" + lastPlayPosition +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(duration);
        dest.writeLong(lastPlayPosition);
    }
}

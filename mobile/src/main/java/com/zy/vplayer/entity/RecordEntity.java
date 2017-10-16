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
    private String time;
    private String size;

    public RecordEntity(String path, long duration, long lastPlayPosition) {
        this.path = path;
        this.duration = duration;
        this.lastPlayPosition = lastPlayPosition;
    }

    public RecordEntity(String path, long duration, long lastPlayPosition, String time, String size) {
        this.path = path;
        this.duration = duration;
        this.lastPlayPosition = lastPlayPosition;
        this.time = time;
        this.size = size;
    }

    private RecordEntity(Parcel in) {
        path = in.readString();
        duration = in.readLong();
        lastPlayPosition = in.readLong();
        time = in.readString();
        size = in.readString();
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLastPlayPosition() {
        return lastPlayPosition;
    }

    public void setLastPlayPosition(long lastPlayPosition) {
        this.lastPlayPosition = lastPlayPosition;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(duration);
        dest.writeLong(lastPlayPosition);
        dest.writeString(time);
        dest.writeString(size);
    }

    @Override
    public String toString() {
        return "RecordEntity{" +
                "path='" + path + '\'' +
                ", duration=" + duration +
                ", lastPlayPosition=" + lastPlayPosition +
                ", time='" + time + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}

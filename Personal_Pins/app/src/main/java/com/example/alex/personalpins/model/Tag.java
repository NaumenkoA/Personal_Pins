package com.example.alex.personalpins.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class Tag implements Parcelable, Comparable <Tag> {
    private String tagName;

    @Exclude
    private String firebaseId;

    public Tag () {

    }

    public Tag (String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public static String convertTagListToString(ArrayList<Tag> tags) {
        if (tags.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (Tag tag : tags) {
            stringBuilder.append(tag.getTagName());
            stringBuilder.append(", ");
        }
        String tagsAsString = stringBuilder.toString();
        return tagsAsString.substring(0, tagsAsString.length()-2);
    }

    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    @Exclude
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagName);
        dest.writeString(this.firebaseId);
    }

    protected Tag(Parcel in) {
        this.tagName = in.readString();
        this.firebaseId = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int compareTo(@NonNull Tag o) {
        return tagName.compareTo(o.getTagName());
    }
}

package com.example.alex.personalpins.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Pin implements Parcelable {
    private String uriAsString;
    private String mediaType;
    private String title;
    private String comment;
    private ArrayList<Tag> tags;

    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    @Exclude
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @Exclude

    private String firebaseId;

    public Pin () {

    }

    public String getUriAsString() {
        return uriAsString;
    }

    public void setUriAsString(String uriAsString) {
        this.uriAsString = uriAsString;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Pin (String title, Uri uri, String mediaType, String comment, ArrayList <Tag> tags) {
        this.title = title;
        this.uriAsString = uri.toString();
        this.comment = comment;
        this.mediaType = mediaType;
        this.tags = tags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uriAsString);
        dest.writeString(this.mediaType);
        dest.writeString(this.title);
        dest.writeString(this.comment);
        dest.writeList(this.tags);
    }

    protected Pin(Parcel in) {
        this.uriAsString = in.readString();
        this.mediaType = in.readString();
        this.title = in.readString();
        this.comment = in.readString();
        this.tags = new ArrayList<Tag>();
        in.readList(this.tags, Tag.class.getClassLoader());
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel source) {
            return new Pin(source);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };

    @Exclude
    public Uri getUri() {
        return Uri.parse(uriAsString);
    }
}

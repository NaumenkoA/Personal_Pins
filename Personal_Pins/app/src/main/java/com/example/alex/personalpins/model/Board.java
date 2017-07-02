package com.example.alex.personalpins.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Board implements Parcelable {
    private String coverImageUriAsString;
    private String boardTitle;
    @Exclude
    private String firebaseId;

    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    @Exclude
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Board (String title, Uri uri) {
        boardTitle = title;
        coverImageUriAsString = uri.toString();
    }

    public Board () {
    }

    public Board (String title) {
        boardTitle = title;
        coverImageUriAsString = "";
    }

    public String getCoverImageUriAsString() {
        return coverImageUriAsString;
    }

    public void setCoverImageUriAsString(String coverImageUriAsString) {
        this.coverImageUriAsString = coverImageUriAsString;
    }

    @Exclude
    public Uri getCoverImageUri() {
        return Uri.parse(coverImageUriAsString);
    }

    @Exclude
    public void setCoverImageUri(Uri coverImageUri) {
        this.coverImageUriAsString = coverImageUri.toString();
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coverImageUriAsString);
        dest.writeString(this.boardTitle);
        dest.writeString(this.firebaseId);
    }

    protected Board(Parcel in) {
        this.coverImageUriAsString = in.readString();
        this.boardTitle = in.readString();
        this.firebaseId = in.readString();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel source) {
            return new Board(source);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };
}

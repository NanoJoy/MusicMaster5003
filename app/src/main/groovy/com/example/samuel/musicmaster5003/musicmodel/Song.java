package com.example.samuel.musicmaster5003.musicmodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 11/28/2017.
 */

public class Song implements Parcelable {
    private final String fileName;
    private final List<Chord> chords;

    public Song(Parcel in) {
        List<String> data = new ArrayList<>();
        in.readStringList(data);
        fileName = data.get(0);
        chords = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            chords.add(i - 1, Chord.fromString(data.get(i)));
        }
    }

    public Song(String fileName, List<Chord> chords) {
        this.fileName = fileName;
        this.chords = chords;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Chord> getChords() {
        return chords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        List<String> strings = new ArrayList<String>();
        strings.add(0, fileName);
        for (int i = 0; i < chords.size(); i++) {
            strings.add(i + 1, chords.get(i).toString().replaceFirst(" ", ":"));
        }
        dest.writeStringList(strings);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}

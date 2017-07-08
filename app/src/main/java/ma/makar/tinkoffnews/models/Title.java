package ma.makar.tinkoffnews.models;

import android.support.annotation.NonNull;

import java.util.Date;

public class Title implements Comparable<Title> {

    public final String id;
    public final String text;
    private final Date publicationDate;

    public Title(String id, String text, Date publicationDate) {
        this.id = id;
        this.text = text;
        this.publicationDate = publicationDate;
    }

    @Override
    public int compareTo(@NonNull Title title) {
        return publicationDate.compareTo(title.publicationDate);
    }
}

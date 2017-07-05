package ma.makar.tinkoffnews.core.mappers;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import ma.makar.base.Assert;
import ma.makar.base.SystemUtils;
import ma.makar.tinkoffnews.Constants;
import ma.makar.tinkoffnews.models.Title;

public class TitlesJsonMapper implements Mapper<List<Title>, String> {

    private static final String TAG = "TitlesJsonMapper";
    private static final String TEXT_JSON_FIELD = "text";
    private static final String DATE_JSON_FIELD = "publicationDate";
    private static final String TIME_JSON_FIELD = "milliseconds";
    static final String ID_JSON_FIELD = "id";

    @Override
    public List<Title> dataToModel(String data) {
        List<Title> titles = new ArrayList<>();
        if (TextUtils.isEmpty(data)) {
            Assert.fail("JSONObject with data is null Object or empty");
            return titles;
        }

        try {
            JSONObject baseObject = new JSONObject(data);
            JSONArray array = baseObject.getJSONArray(Constants.BASE_JSON_FIELD);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString(ID_JSON_FIELD);
                String text = SystemUtils.stripHtml(object.getString(TEXT_JSON_FIELD));
                JSONObject objectDate = object.getJSONObject(DATE_JSON_FIELD);
                long publicationDate = objectDate.getLong(TIME_JSON_FIELD);
                Date date = new Date(publicationDate);
                titles.add(new Title(id, text, date));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Conversion error", e);
        }
        return titles;
    }

    @Override
    @Nullable
    public String modelToData(List<Title> model) {
        throw new UnsupportedOperationException("The method is empty and not suitable for use");
    }
}

package ma.makar.tinkoffnews.core.mappers;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ma.makar.base.Assert;
import ma.makar.base.SystemUtils;
import ma.makar.tinkoffnews.Constants;
import ma.makar.tinkoffnews.models.News;

public class NewsJsonMapper implements Mapper<News, String> {

    private static final String TAG = "TitlesJsonMapper";
    private static final String TITLE_JSON_FIELD = "title";
    private static final String CONTENT_JSON_FIELD = "content";

    @Override
    @Nullable
    public News dataToModel(String data) {
        if (TextUtils.isEmpty(data)) {
            Assert.fail("JSONObject with data is null Object or empty");
            return null;
        }

        try {
            JSONObject baseObject = new JSONObject(data);
            JSONObject object = baseObject.getJSONObject(Constants.BASE_JSON_FIELD);
            String content = SystemUtils.stripHtml(object.getString(CONTENT_JSON_FIELD));
            JSONObject title = object.getJSONObject(TITLE_JSON_FIELD);
            String id = title.getString(TitlesJsonMapper.ID_JSON_FIELD);
            return new News(id, content);
        } catch (JSONException e) {
            Log.e(TAG, "Conversion error", e);
        }
        return null;
    }

    @Override
    @Nullable
    public String modelToData(News model) {
        throw new UnsupportedOperationException("The method is empty and not suitable for use");
    }
}

package ma.makar.tinkoffnews.core.helpers;

import ma.makar.tinkoffnews.Constants;

public class TinkoffNewsUrlCreator implements UrlCreator<String> {

    @Override
    public String getUrl(String id) {
        return Constants.NEWS_CONTENT_URL + id;
    }
}

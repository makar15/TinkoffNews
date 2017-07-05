package ma.makar.tinkoffnews.core.mappers;

public interface Mapper<M, D> {

    M dataToModel(D date);

    /**
     * Not yet required
     */
    D modelToData(M model);
}

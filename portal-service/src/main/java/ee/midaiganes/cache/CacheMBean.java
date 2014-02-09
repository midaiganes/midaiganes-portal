package ee.midaiganes.cache;

public interface CacheMBean {
    long getNumberOfGets();

    long getCacheSize();

    long getNumberOfPuts();

    long getCacheHits();

    long getCacheMisses();
}

package micronaut.demo.beer.domain;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("markup")
public class BeerMarkupConfiguration {
    private String databaseName = "billing";
    private String collectionName = "markup";

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}

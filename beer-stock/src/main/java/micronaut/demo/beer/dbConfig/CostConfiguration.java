package micronaut.demo.beer.dbConfig;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("cost")
public class CostConfiguration {

    private String databaseName = "beerstock";
    private String collectionName = "cost";

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

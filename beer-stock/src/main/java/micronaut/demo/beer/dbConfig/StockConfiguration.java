package micronaut.demo.beer.dbConfig;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("stock")
public class StockConfiguration {

    private String databaseName = "beerstock";
    private String collectionName = "stock";

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

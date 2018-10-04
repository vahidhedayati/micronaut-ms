package micronaut.demo.beer.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * An enum to manage unit measurements of each item sold
 * this deducts this amount of total of overall barrels
 * added to the pool of available beer stock !
 *
 */
public enum BeerSize {


    // 1 barrel = 327.318 pints
    // 1 pint out of 327.318T =  0.00305513292883
    PINT(new Double(0.00305513292883)),

    //Double above calculations:
    HALF_PINT(new Double(0.00152756646442)),

    //We deduct 1 bottle from stock levels
    BOTTLE(new Double(1.00));


    private static final Map<String, BeerSize> lookup = new HashMap<String, BeerSize>();


    Double value;

    BeerSize(Double val) {
        this.value = val;
    }

    public Double getValue(){
        return value;
    }

    static BeerSize byValue(Double val) {
        return lookup.get(val);
    }

}

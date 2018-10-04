package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic markup percentage price on each bottle/pint sold
 */
public class BeerMarkup {


	private double bottleMarkup;


	private double pintMarkup;
	private String name;

	@JsonCreator
	public BeerMarkup(@JsonProperty("name") String name, @JsonProperty("bottleMarkup") double bottleMarkup, @JsonProperty("pintMarkup") double pintMarkup) {
		this.name=name;
		this.bottleMarkup = bottleMarkup;
		this.pintMarkup = pintMarkup;
	}

	public BeerMarkup() {}


    public String getName() {
        return this.name;
    }
    public double getBottleMarkup() {
        return this.bottleMarkup;
    }
    public double getPintMarkup() {
        return this.pintMarkup;
    }

    public void setBottleMarkup(double bottleMarkup) {
        this.bottleMarkup = bottleMarkup;
    }

    public void setPintMarkup(double pintMarkup) {
        this.pintMarkup = pintMarkup;
    }

    public void setName(String name) {
        this.name = name;
    }




    /*
	@Override
	public String toString() {
		return "BeerMarkup{" +
				"name='" + this.name + '\'' +
				",bottleMarkup='" + this.bottleMarkup + '\'' +
				", pintMarkup=" + this.pintMarkup +
				'}';
	}
	*/



}

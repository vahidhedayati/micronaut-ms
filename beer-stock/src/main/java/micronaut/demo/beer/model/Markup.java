package micronaut.demo.beer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic markup percentage price on each bottle/pint sold
 */
public class Markup {
	public String getName() {
		return name;
	}

	private String name;


	public double getBottleMarkup() {
		return bottleMarkup;
	}

	public double getPintMarkup() {
		return pintMarkup;
	}

	private double bottleMarkup;
	private double pintMarkup;

	@JsonCreator
	public Markup(@JsonProperty("name") String name,@JsonProperty("bottleMarkup") double bottleMarkup,@JsonProperty("pintMarkup") double pintMarkup) {
		this.name=name;
		this.bottleMarkup = bottleMarkup;
		this.pintMarkup = pintMarkup;
	}

	/*
	@Override
	public String toString() {
		return "Markup{" +
				"name='" + name + '\'' +
				",bottleMarkup='" + bottleMarkup + '\'' +
				", pintMarkup=" + pintMarkup +
				'}';
	}

*/
}

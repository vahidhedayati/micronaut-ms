package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BeerCost {

	private  String name;

	private double bottleCost;
	private double pintCost;



	@JsonCreator
	public BeerCost(@JsonProperty("name") String name,
                    @JsonProperty("cost") double bottleCost, @JsonProperty("cost") double pintCost) {
		this.name = name;
		this.bottleCost = bottleCost;
		this.pintCost = pintCost;

	}


	public BeerCost() {}

	/*public BeerCost(String name, double bottleCost, double pintCost) {
		this.name = name;
		this.bottleCost = bottleCost;
		this.pintCost = pintCost;
	}
	*/


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}



	public Double getBottleCost() {
		return bottleCost;
	}

	public void setBottleCost(double bottleCost) {
		this.bottleCost = bottleCost;
	}


	public Double getPintCost() {
		return pintCost;
	}

	public void setPintCost(Double pintCost) {
		this.pintCost = pintCost;
	}

	@Override
	public String toString() {
		return "BeerCost{" +
				"name='" + name + '\'' +
				", bottleCost='" + bottleCost + '\'' +
				", pintCost=" + pintCost +
				'}';
	}
}

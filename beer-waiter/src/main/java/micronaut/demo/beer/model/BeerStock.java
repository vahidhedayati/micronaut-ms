package micronaut.demo.beer.model;

public class BeerStock {

	private  String name;


	private long bottles;

	private double barrels; // 1 barrel = 327.318 pints
	private double availablePints;


	/*
	@JsonCreator
	public BeerStock(@JsonProperty("name") String name, @JsonProperty("size") long bottles,
				 @JsonProperty("cost") double barrels) {
		this.name = name;
		this.bottles = bottles;
		this.barrels = this.getBarrels()+barrels;
		this.availablePints=this.getAvailablePints()+barrels*new Double(327.318);
	}
	*/

	public BeerStock() {}

	public BeerStock(String name, long bottles, double barrels) {
		this.name = name;
		this.bottles = bottles;
		//this.barrels = barrels;
		this.barrels = this.getBarrels()+barrels;
		this.availablePints=this.getAvailablePints()+barrels*new Double(327.318);
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}



	public long getBottles() {
		return bottles;
	}

	public void setBottles(long bottles) {
		this.bottles = bottles;
	}


	public Double getBarrels() {
		return barrels;
	}

	public void setBarrels(double barrels) {
		this.barrels = barrels;
	}

	public Double getAvailablePints() {
		return availablePints;
	}

	public void setAvailablePints(double availablePints) {
		this.availablePints = availablePints;
	}



	@Override
	public String toString() {
		return "BeerStock{" +
				"name='" + name + '\'' +
				"bottles='" + bottles + '\'' +
				"barrels='" + barrels + '\'' +
				", availablePints=" + availablePints +
				'}';
	}

}

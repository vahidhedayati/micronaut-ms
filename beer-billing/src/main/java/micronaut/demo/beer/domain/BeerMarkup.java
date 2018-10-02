package micronaut.demo.beer.domain;

/**
 * A generic markup percentage price on each bottle/pint sold
 */
public class BeerMarkup {

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

	public BeerMarkup(String name, double bottleMarkup, double pintMarkup) {
		this.name=name;
		this.bottleMarkup = bottleMarkup;
		this.pintMarkup = pintMarkup;
	}

	public BeerMarkup() {}



/*
	@Override
	public String toString() {
		return "BeerMarkup{" +
				"name='" + name + '\'' +
				",bottleMarkup='" + bottleMarkup + '\'' +
				", pintMarkup=" + pintMarkup +
				'}';
	}

*/

}

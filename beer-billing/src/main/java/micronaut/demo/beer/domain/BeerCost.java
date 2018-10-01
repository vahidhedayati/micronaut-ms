package micronaut.demo.beer.domain;

/**
 * A generic markup percentage price on each bottle/pint sold
 */
public class BeerCost {



	private double bottleMarkup;
	private double pintMarkup;

	public BeerCost(double bottleMarkup, double pintMarkup) {
		this.bottleMarkup = bottleMarkup;
		this.pintMarkup = pintMarkup;
	}

	@Override
	public String toString() {
		return "BeerCost{" +
				"bottleMarkup='" + bottleMarkup + '\'' +
				", pintMarkup=" + pintMarkup +
				'}';
	}
}

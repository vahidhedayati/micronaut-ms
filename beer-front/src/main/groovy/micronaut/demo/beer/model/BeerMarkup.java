package micronaut.demo.beer.model;

/**
 * A generic markup percentage price on each bottle/pint sold
 */
public class BeerMarkup {


	public double getBottleMarkup() {
		return bottleMarkup;
	}

	public double getPintMarkup() {
		return pintMarkup;
	}

	private double bottleMarkup;
	private double pintMarkup;

	public BeerMarkup(double bottleMarkup, double pintMarkup) {
		this.bottleMarkup = bottleMarkup;
		this.pintMarkup = pintMarkup;
	}

	@Override
	public String toString() {
		return "BeerMarkup{" +
				"bottleMarkup='" + bottleMarkup + '\'' +
				", pintMarkup=" + pintMarkup +
				'}';
	}
}

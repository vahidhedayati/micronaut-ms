package micronaut.demo.beer.model;

import micronaut.demo.beer.BeerSize;

public class BeerItem {
	private  String name;
	private  BeerSize size;



	private int amount;


	public BeerItem() {

	}

	public String getName() {
		return name;
	}
	public BeerSize getSize() {
		return size;
	}
	public int getAmount() {
		return amount;
	}
	public BeerItem(String name, BeerSize size, int amount) {
		super();
		this.name = name;
		this.size = size;
		this.amount=amount;
	}
	


}

package micronaut.demo.beer.model;

public class BeerItem {
	private  String name;
	private  BeerSize size;



	private int amount;



	private double price;


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
	public double getPrice() { return price; }
	public BeerItem(String name, BeerSize size, int amount, double price) {
		super();
		this.name = name;
		this.size = size;
		this.amount=amount;
		this.price=price;
	}
	


}

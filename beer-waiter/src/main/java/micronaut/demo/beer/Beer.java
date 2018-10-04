package micronaut.demo.beer;

public class Beer {
	private  String name;
	private  BeerSize size;
	private int amount;
	private double price;



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

	public Beer() {
		
	}
	
	public Beer(String name, BeerSize size, int amount, double price) {
		super();
		this.name = name;
		this.size = size;
		this.amount=amount;
		this.price=price;
	}
}

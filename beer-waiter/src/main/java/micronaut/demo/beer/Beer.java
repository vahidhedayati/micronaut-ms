package micronaut.demo.beer;

public class Beer {
	private  String name;
	private  BeerSize size;
	private int amount;



	public String getName() {
		return name;
	}
	public BeerSize getSize() {
		return size;
	}
	public int getAmount() {
		return amount;
	}
	public Beer() {
		
	}
	
	public Beer(String name, BeerSize size, int amount) {
		super();
		this.name = name;
		this.size = size;
		this.amount=amount;
	}
}

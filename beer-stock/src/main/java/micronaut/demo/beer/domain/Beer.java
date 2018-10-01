package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import micronaut.demo.beer.enums.BeerSize;

import java.math.BigDecimal;

public class Beer {

	private  String name;


	private long bottles;

	private double barrels; // 1 barrel = 327.318 pints
	private double availablePints;


	/*
	@JsonCreator
	public Beer(@JsonProperty("name") String name, @JsonProperty("size") long bottles,
				 @JsonProperty("cost") double barrels) {
		this.name = name;
		this.bottles = bottles;
		this.barrels = this.getBarrels()+barrels;
		this.availablePints=this.getAvailablePints()+barrels*new Double(327.318);
	}
	*/

	public Beer() {}

	public Beer(String name, long bottles, double barrels) {
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


	public Beer addPint(int amount) {
		if (this.getBarrels()>0) {
			this.availablePints=this.getAvailablePints() - (BeerSize.PINT.getValue()*amount);
			updateBarrels();
			return this;
		}
		return null;
	}

	public Beer addHalfPint(int amount) {
		if (this.getBarrels()>0) {
			this.availablePints=this.getAvailablePints() - (BeerSize.HALF_PINT.getValue()*amount);
			updateBarrels();
			return this;
		}
		return null;
	}


	public Beer addBottle(int amount) {
		if (this.getBottles()>0 && this.getBottles()-amount>0) {
			this.bottles=this.getBottles()-amount;
			return this;
		}
		return null;
	}

	public void updateBarrels() {
		Double cv = this.getAvailablePints() / new Double(327.318);
		int currentBarrels = new BigDecimal(cv.toString()).intValue();
		//The amount of pints sold has now overlapped into another barrel
		if (currentBarrels < this.getBarrels()) {
			this.barrels=currentBarrels;
		}
	}

	@Override
	public String toString() {
		return "Beer{" +
				"name='" + name + '\'' +
				"bottles='" + bottles + '\'' +
				"barrels='" + barrels + '\'' +
				", availablePints=" + availablePints +
				'}';
	}

}

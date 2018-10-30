package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SequenceTest {

	private  String name;


	private Date dateCreated;


	@JsonCreator
	public SequenceTest(@JsonProperty("name") String name,
                        @JsonProperty("dateCreated") Date dateCreated) {
		this.name = name;
		this.dateCreated = dateCreated;


	}


	public SequenceTest() {}



	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDateCreatedString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return df.format(dateCreated);
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}


	@Override
	public String toString() {
		return "SequenceTest{" +
				"name='" + name + '\'' +
				", dateCreated='" + getDateCreatedString() +
				'}';
	}
}

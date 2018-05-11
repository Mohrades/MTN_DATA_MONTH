package domain.models;

public class Sharing {
	
	private int id;
	private long value;
	private String msisdn;
	
	public Sharing() {

	}

	public Sharing(int id, String msisdn, long value) {
		this.id = id;
		this.msisdn = msisdn;
		this.value = value;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}

}

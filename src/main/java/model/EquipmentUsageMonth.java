package model;

public class EquipmentUsageMonth extends EquipmentUsage {
	private String month;
	

	public EquipmentUsageMonth(Equipment eq) {
		super(eq);
		// TODO Auto-generated constructor stub
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

}

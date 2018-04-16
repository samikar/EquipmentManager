package fi.danfoss.equipmentmanager.model;

public class MonthlyUsage {
	private String month;
	
	private double available;
	
	private double inUse;
	
	private double calibration;
	
	private double maintenance;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	
	public double getAvailable() {
		return available;
	}

	public void setAvailable(double available) {
		this.available = available;
	}

	public double getInUse() {
		return inUse;
	}

	public void setInUse(double inUse) {
		this.inUse = inUse;
	}

	public double getCalibration() {
		return calibration;
	}

	public void setCalibration(double calibration) {
		this.calibration = calibration;
	}

	public double getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(double maintenance) {
		this.maintenance = maintenance;
	}
	
}

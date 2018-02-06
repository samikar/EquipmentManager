package model;

public class EquipmentUsage extends Equipment {
	private double available;
	
	private double inUse;
	
	private double calibration;
	
	private double maintenance;

	public EquipmentUsage(Equipment eq) {
		this.setEquipmentId(eq.getEquipmentId());
		
		this.setName(eq.getName());
		
		this.setSerial(eq.getSerial());
		
		this.setStatus(eq.getStatus());
		
		this.setEquipmenttype(eq.getEquipmenttype());
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

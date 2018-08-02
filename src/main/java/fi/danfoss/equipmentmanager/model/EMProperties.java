package fi.danfoss.equipmentmanager.model;

public class EMProperties {
	private String DBurl;
	private String DBuser;
	private String DBpassword;
	private String DBdriver;
	private String ADuser;
	private String ADpassword;
	private String ADurl; 
	private String WORKDAY;
	private String STARTHOUR;
	private String STARTMINUTE;
	private String ENDHOUR;
	private String ENDMINUTE;
	private String TempFilePath;
	public String getDBurl() {
		return DBurl;
	}
	public void setDBurl(String dBurl) {
		DBurl = dBurl;
	}
	public String getDBuser() {
		return DBuser;
	}
	public void setDBuser(String dBuser) {
		DBuser = dBuser;
	}
	public String getDBpassword() {
		return DBpassword;
	}
	public void setDBpassword(String dBpassword) {
		DBpassword = dBpassword;
	}
	public String getDBdriver() {
		return DBdriver;
	}
	public void setDBdriver(String dBdriver) {
		DBdriver = dBdriver;
	}
	public String getADuser() {
		return ADuser;
	}
	public void setADuser(String aDuser) {
		ADuser = aDuser;
	}
	public String getADpassword() {
		return ADpassword;
	}
	public void setADpassword(String aDpassword) {
		ADpassword = aDpassword;
	}
	public String getADurl() {
		return ADurl;
	}
	public void setADurl(String aDurl) {
		ADurl = aDurl;
	}
	public String getWORKDAY() {
		return WORKDAY;
	}
	public void setWORKDAY(String wORKDAY) {
		WORKDAY = wORKDAY;
	}
	public String getSTARTHOUR() {
		return STARTHOUR;
	}
	public void setSTARTHOUR(String sTARTHOUR) {
		STARTHOUR = sTARTHOUR;
	}
	public String getSTARTMINUTE() {
		return STARTMINUTE;
	}
	public void setSTARTMINUTE(String sTARTMINUTE) {
		STARTMINUTE = sTARTMINUTE;
	}
	public String getENDHOUR() {
		return ENDHOUR;
	}
	public void setENDHOUR(String eNDHOUR) {
		ENDHOUR = eNDHOUR;
	}
	public String getENDMINUTE() {
		return ENDMINUTE;
	}
	public void setENDMINUTE(String eNDMINUTE) {
		ENDMINUTE = eNDMINUTE;
	}
	public String getTempFilePath() {
		return TempFilePath;
	}
	public void setTempFilePath(String tempFilePath) {
		this.TempFilePath = tempFilePath;
	}
	
}

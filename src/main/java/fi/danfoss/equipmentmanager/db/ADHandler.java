package fi.danfoss.equipmentmanager.db;

import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import fi.danfoss.equipmentmanager.utils.PropertyUtils;

public class ADHandler {
	DirContext statcontext;
	final static Logger logger = Logger.getLogger(ADHandler.class);

	/**
	 * Initializes connection to Danfoss AD 
	 * 
	 * @return
	 */
	public DirContext init() {
		Properties appProperties = PropertyUtils.loadProperties();
		Properties LDAPproperties = new Properties();
		
		LDAPproperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		LDAPproperties.put(Context.PROVIDER_URL, appProperties.getProperty("ADURL"));
		LDAPproperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		LDAPproperties.put(Context.SECURITY_PRINCIPAL, appProperties.getProperty("ADuser") + "@danfoss");
		LDAPproperties.put(Context.SECURITY_CREDENTIALS, appProperties.getProperty("ADpassword"));
		
		try {
			logger.info("Connecting to AD.");
			statcontext = new InitialDirContext(LDAPproperties);

		} catch (NamingException e) {
			logger.error("AD connection failed: " + e.toString());
		}
		return statcontext;
	}
	
	public void close() {
		try {
			statcontext.close();
		} catch (NamingException e) {
			logger.error("AD connection could not be closed: " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Searches AD for employee name with employeeId 
	 * 
	 * @param employeeId		EmployeeId to search
	 * @return					Employee's name as a String
	 * 
	 */
	public String findEmployeeName(String employeeId) {
		StringBuilder nameBuilder = new StringBuilder();
		try {
			SearchControls searchCtrls = new SearchControls();
			String returnedAtts[] = { "givenName", "sn" };
			searchCtrls.setReturningAttributes(returnedAtts);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(EmployeeId=" + employeeId + ")";
			logger.info("Searching AD for employeeId " + employeeId + "...");
			System.out.println("Searching AD for employeeId " + employeeId + "...");
			// This search takes a long time...
			NamingEnumeration values = statcontext.search("ou=Internal Accounts, ou=P35 Accounts, ou=Users, ou=Accounts", filter, searchCtrls);

			while (values.hasMoreElements()) {
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll();
								vals.hasMoreElements();
								nameBuilder.append(vals.nextElement() + " "));
						
					}
				}
			}
		} catch (NamingException e) {
			logger.error("Error searchinng for employee in AD: " + e.toString());
			e.printStackTrace();
		}
		return nameBuilder.toString().trim();
	}
}
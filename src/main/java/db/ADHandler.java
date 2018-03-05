package db;

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

public class ADHandler {
	DirContext statcontext;
	public ADHandler() {

	}

	public DirContext init() {
		Properties appProperties = PropertyUtils.loadProperties();
		Properties LDAPproperties = new Properties();
		
		LDAPproperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		LDAPproperties.put(Context.PROVIDER_URL, appProperties.getProperty("ADURL"));
		LDAPproperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		LDAPproperties.put(Context.SECURITY_PRINCIPAL, appProperties.getProperty("ADuser") + "@danfoss");
		LDAPproperties.put(Context.SECURITY_CREDENTIALS, appProperties.getProperty("ADpassword"));
		
		try {
			statcontext = new InitialDirContext(LDAPproperties);
//			SearchControls searchCtrls = new SearchControls();
//			String returnedAtts[] = { "givenName", "sn" };
//			searchCtrls.setReturningAttributes(returnedAtts);
//			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return statcontext;
	}
	
	public void close() {
		try {
			statcontext.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String findEmployeeName(String employeeId) {
		StringBuilder nameBuilder = new StringBuilder();
		try {
			SearchControls searchCtrls = new SearchControls();
			String returnedAtts[] = { "givenName", "sn" };
			searchCtrls.setReturningAttributes(returnedAtts);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(EmployeeId=" + employeeId + ")";
			
			System.out.println("Searching...");
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
			e.printStackTrace();
		}
		return nameBuilder.toString().trim();
	}
		
	/*
	public static String findEmployeeName(String employeeId) {
		Properties appProperties = PropertyUtils.loadProperties();
		Properties LDAPproperties = new Properties();
		StringBuilder nameBuilder = new StringBuilder();
		
		LDAPproperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		LDAPproperties.put(Context.PROVIDER_URL, appProperties.getProperty("ADURL"));
		LDAPproperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		LDAPproperties.put(Context.SECURITY_PRINCIPAL, appProperties.getProperty("ADuser") + "@danfoss");
		LDAPproperties.put(Context.SECURITY_CREDENTIALS, appProperties.getProperty("ADpassword"));
		
		try {
			DirContext context = new InitialDirContext(LDAPproperties);
			SearchControls searchCtrls = new SearchControls();
			String returnedAtts[] = { "givenName", "sn" };
			searchCtrls.setReturningAttributes(returnedAtts);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(EmployeeId=" + employeeId + ")";
			
			System.out.println("Searching...");
			// This search takes a long time...
			NamingEnumeration values = context.search("ou=Internal Accounts, ou=P35 Accounts, ou=Users, ou=Accounts", filter, searchCtrls);

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
			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		System.out.println("Search complete!");
		return nameBuilder.toString().trim();
	}
	*/
}
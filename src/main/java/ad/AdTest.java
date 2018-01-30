package ad;

import java.util.Enumeration;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class AdTest {
	public AdTest() {

	}

	public void doLookup() {
		String userName = "";
		String password = "";
		String base = "DC=danfoss,DC=net";
		String dn = "cn=" + userName + "," + "CN=Accounts," + base;  
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "ldap://danfoss.net:389/dc=danfoss,dc=net");
		properties.put(Context.SECURITY_AUTHENTICATION,"simple");
		properties.put(Context.SECURITY_PRINCIPAL, "@danfoss");
		properties.put(Context.SECURITY_CREDENTIALS, password);
		
		try {
			DirContext context = new InitialDirContext(properties);

			SearchControls searchCtrls = new SearchControls();

			//String returnedAtts[] = {"givenName", "sn", "EmployeeId", "cn", "mail"};
			String returnedAtts[] = {"givenName", "sn"};
			searchCtrls.setReturningAttributes(returnedAtts);
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(EmployeeId=)";			

			System.out.println("Searching...");
			NamingEnumeration values = context.search("ou=Internal Accounts, ou=P35 Accounts, ou=Users, ou=Accounts", filter, searchCtrls);
			
			while (values.hasMoreElements())
			{
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs)
				{
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();)
					{
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); 
							vals.hasMoreElements(); 
							System.out.print(attributeID + ": " + vals.nextElement() + " "));
					}
				}
				System.out.println("");
			}

			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		System.out.println("Search complete!");
	}

	public static void main(String[] args) {
		AdTest sample = new AdTest();
		sample.doLookup();
	}

}
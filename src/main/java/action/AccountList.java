package action;

import javax.persistence.Query;

import model.Account;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;

@Name("accountList")
@Restrict("#{identity.hasRole('admin')}")
public class AccountList extends EntityQuery<Account> {

	private static final long serialVersionUID = 6399831773976567468L;

	// Combines the Seam-managed persistence context with Lucene capabilities
	@In private FullTextEntityManager entityManager;

	private String queryString;

	final String[] fields = {
		"firstname", "lastname", "username", "email",
	};

	public AccountList() {
		setEjbql("from Account");
	}

	public String getQueryString() {
		return this.queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public Account getSingleResult() {
		return null;
	}

	@Override
	protected Query createQuery() {
		System.err.println("AccountList.createQuery()");
		if (queryString == null || queryString.length() == 0) {
			return entityManager.createQuery("from Account");
		}
		try {
			final MultiFieldQueryParser parser = new MultiFieldQueryParser(
				fields, new StandardAnalyzer());
			final org.apache.lucene.search.Query luceneQuery = 
				parser.parse(queryString);
			return entityManager.createFullTextQuery(luceneQuery, Account.class);
		} catch (ParseException e) {
			// Weird but this method gets called very often, so
			// make sure we only print the message once.
			// Sorry if this loses any other important message!
			FacesMessages.instance().clear();
			FacesMessages.instance().add("Parse error: " + e);
			// return all instead of none
			return entityManager.createQuery("from Account");
		}
	}
}

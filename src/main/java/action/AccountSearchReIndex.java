package action;

import java.util.List;

import model.Account;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

@Name("accountSearchReIndex")
@Restrict("#{s:hasRole('admin')}")
public class AccountSearchReIndex {

	private static final long serialVersionUID = 609820820330281L;

	@Logger
	private Log log;

	@In private FullTextEntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Observer(value = { "accountCreated", "accountEdited", "accountDeleted" })
	public String reindex() {
		log.info("Reindexing Account Search Index");

		final List<Account> entries = entityManager.createQuery("from Account").getResultList();
		for (Account b : entries) {
			entityManager.index(b);
		}
		return null;
	}
}

package action;

import global.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import model.Account;
import model.Userrole;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.framework.EntityHome;

import unix.SystemAccountAccessor;

/** Uses the Seam Entity Framework but also uses
 * the SystemAccount class to save the Account data
 * into the operating system account.
 * @author ian
 */
@Name("accountHome")
@Restrict("#{identity.hasRole('admin')}")
public class AccountHome extends EntityHome<Account> {

	private static SystemAccountAccessor osDAO =
		SystemAccountAccessor.getInstance();
	
	@Override
	public String persist() {
		if (!osDAO.addAccount(getInstance())) {
			return "failed";
		}
		getInstance().setSystemAccountCreated(true);
		return super.persist();
	}

	@Override
	public String remove() {
		if (!osDAO.deleteAccount(getInstance())) {
			return "failed";
		}
		return super.remove();
	}

	@Override
	public String update() {
		if (!osDAO.updateAccount(getInstance())) {
			return "failed";
		}
		return super.update();
	}

	private static final long serialVersionUID = 1306635550541105929L;

	public void setAccountId(Integer id) {
		setId(id);
	}

	public Integer getAccountId() {
		return (Integer) getId();
	}

	@Override
	protected Account createInstance() {
		Account account = new Account();
		Query q = getEntityManager().createQuery(
		"from Userrole u where u.name='" + Constants.DEFAULT_USER_ROLE + "'");
		Userrole r = (Userrole) q.getSingleResult();
		account.getRoles().add(r);
		return account;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Account getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Userrole> getUserroles() {
		return getInstance() == null ? null : new ArrayList<Userrole>(
				getInstance().getRoles());
	}
}

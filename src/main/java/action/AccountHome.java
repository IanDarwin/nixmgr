package action;

import java.util.ArrayList;
import java.util.List;

import model.Account;
import model.UserInRole;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.annotations.security.Restrict;

import unix.SystemAccountAccessor;

/** Uses the Seam Entity Framework but also uses
 * the SystemAccount class to save the Account data
 * into the operating system account.
 * @author ian
 */
@Name("accountHome")
@Restrict("#{identity.inRole('admin')}")
public class AccountHome extends EntityHome<Account> {

	private SystemAccountAccessor osDAO =
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

	public List<UserInRole> getUserInRoles() {
		return getInstance() == null ? null : new ArrayList<UserInRole>(
				getInstance().getUserInRoles());
	}

}

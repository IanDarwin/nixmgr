package action;

import model.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("accountHome")
public class AccountHome extends EntityHome<Account> {

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

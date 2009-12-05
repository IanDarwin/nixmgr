package action;

import model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("userInRoleHome")
public class UserInRoleHome extends EntityHome<UserInRole> {

	@In(create = true)
	AccountHome accountHome;
	@In(create = true)
	UserroleHome userroleHome;

	public void setUserInRoleId(Integer id) {
		setId(id);
	}

	public Integer getUserInRoleId() {
		return (Integer) getId();
	}

	@Override
	protected UserInRole createInstance() {
		UserInRole userInRole = new UserInRole();
		return userInRole;
	}

	public void wire() {
		getInstance();
		Account account = accountHome.getDefinedInstance();
		if (account != null) {
			getInstance().setAccount(account);
		}
		Userrole userrole = userroleHome.getDefinedInstance();
		if (userrole != null) {
			getInstance().setUserrole(userrole);
		}
	}

	public boolean isWired() {
		return true;
	}

	public UserInRole getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}

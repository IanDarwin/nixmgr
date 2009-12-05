package action;

import model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("userInRoleList")
public class UserInRoleList extends EntityQuery<UserInRole> {

	private static final String EJBQL = "select userInRole from UserInRole userInRole";

	private static final String[] RESTRICTIONS = {};

	private UserInRole userInRole = new UserInRole();

	public UserInRoleList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UserInRole getUserInRole() {
		return userInRole;
	}
}

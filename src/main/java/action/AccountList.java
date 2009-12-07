package action;

import java.util.Arrays;

import model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.annotations.security.Restrict;

@Name("accountList")
@Restrict("#{identity.hasRole('admin')}")
public class AccountList extends EntityQuery<Account> {

	private static final String EJBQL = "select account from Account account";

	private static final String[] RESTRICTIONS = {
			"lower(account.firstname) like concat(lower(#{accountList.account.firstname}),'%')",
			"lower(account.lastname) like concat(lower(#{accountList.account.lastname}),'%')",
			"lower(account.password) like concat(lower(#{accountList.account.password}),'%')",
			"lower(account.username) like concat(lower(#{accountList.account.username}),'%')",};

	private Account account = new Account();

	public AccountList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Account getAccount() {
		return account;
	}
}

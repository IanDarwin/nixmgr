package unix;

import model.Account;

/**
 * SystemAccount ONLY deals with interfacing between Account objects and 
 * the underlying operating system account (e.g, Unix password file).
 */
public abstract class SystemAccountAccessor  {
	
	/** Simple factory, in case we need to run on other OSes */
	public static SystemAccountAccessor getInstance() {
		return new UnixAccountAccessor();
	}
	
	/** Create this Account to the operating system. */
	public abstract boolean addAccount(Account a);

	/** Sync this Account's password out to its system account. */
	public abstract boolean updateAccount(Account a);

	/** Remove this account from the operating system accounts database. */
	public abstract boolean deleteAccount(Account a);

}

package unix;

import java.io.IOException;

import model.Account;

/**
 * SystemAccount ONLY deals with interfacing between Account objects and 
 * the underlying operating system account (e.g, Unix password file).
 * This version was written for OpenBSD and may need some tweaking
 * for other *Nix-like systems.
 */
public class UnixAccountAccessor extends SystemAccountAccessor {

	/** XXX Security Note - putting the encrypted password on the
	 * command line like this is visible in "ps" briefly - DO NOT
	 * use this if you allow non-admins to login to the machine
	 * that the accounts are created on!!!!!
	 * XXX The BSD "login class" 'student' is hard-coded here for now.
	 */
	private final static String ADD_STRING =
	"sudo useradd -m -g =uid -L student -p \"$(encrypt %2$s)\" -s /sbin/nologin -c %3$s '%1$s'";

	/** Create this Account to the operating system. */
	public void addAccount(Account a) {
		System.out.println("Adding " + a);
		String osCommand = 
			String.format(ADD_STRING, 
				a.getUsername(),
				a.getPassword(),
				a.getFullName());
		System.out.println("Running " + osCommand);
		try {
			Runtime.getRuntime().exec(osCommand);
		} catch (IOException e) {
			throw new RuntimeException("Could not execute system command", e);
		}
	}

	/** Sync this Account's password out to its system account. */
	public void setPassword(Account a) {
		System.out.println("Synching " + a + "'s password to the os");
		throw new RuntimeException("Not written yet");
	}

	/** Remove this account from the operating system accounts database. */
	public void deleteAccount(Account a) {
		throw new RuntimeException("Cannot remove accounts yet");
	}

}

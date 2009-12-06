package unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import model.Account;

import org.jboss.seam.faces.FacesMessages;

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
	 * XXX ADD_STRING and MOD_STRING *must* take the same 3 args
	 */
	private final static String ADD_STRING =
	"sudo useradd -m -g =uid -L student -p \"$(encrypt %2$s)\" -s /bin/ksh -c \'%3$s\' \'%1$s\'";

	private final static String MOD_STRING =
	"sudo usermod -p \"$(encrypt %2$s)\" -s /bin/ksh -c \'%3$s\' \'%1$s\'";

	/** Create this Account to the operating system. */
	public boolean addAccount(Account a) {
		System.out.println("Adding " + a);
		return addModUser(a, ADD_STRING);
	}

	/** Sync Account's password out to its system account. */
	public boolean updateAccount(Account a) {
		System.out.println("Synching " + a + "'s password to the os");
		return addModUser(a, MOD_STRING);
	}

	/** Obviously common code for add and mod account */
	private boolean addModUser(Account a, String template) {
		final String osCommand = 
			String.format(template, 
				a.getUsername(),
				a.getPassword(),
				a.getFullName());
		System.out.println("Running " + osCommand);
		try {
			String args[] = { "sh", "-c", osCommand };
			final ProcessBuilder builder = new ProcessBuilder(args);
			builder.redirectErrorStream(true);
			final Process proc = builder.start();
			BufferedReader is = new BufferedReader(
				new InputStreamReader(proc.getInputStream()));
			String line;
			while ((line = is.readLine()) != null) {
				System.out.println("-->" + line);
				FacesMessages.instance().add(
				"Account Creation failed:\n" + line);
			}
			int ret = proc.waitFor();
			return ret == 0;
		} catch (IOException e) {
			FacesMessages.instance().add("Could not execute system command: " + e);
			return false;
		} catch (InterruptedException e) {
			// really a CANTHAPPEN
			e.printStackTrace();
			return false;
		}
	}


	/** Remove this account from the operating system accounts database. */
	public boolean deleteAccount(Account a) {
		throw new RuntimeException("Cannot remove accounts yet");
	}

}

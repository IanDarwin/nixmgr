package unix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.faces.application.FacesMessage;

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
	 */
	private final static String ADD_STRING =
	"sudo useradd -m -g =uid -b /home/student -L student -p \"$(encrypt -m %2$s)\" -s /bin/ksh -c \'%3$s\' \'%1$s\'";

	private final static String MOD_STRING =
	"sudo usermod -p \"$(encrypt -m %2$s)\" -s /bin/ksh -c \'%3$s\' \'%1$s\'";

	private final static String SYNCH_STRING = "cd /var/yp; sudo make";

	/** Create this Account to the operating system. */
	public boolean addAccount(Account acct) {
		System.out.println("Adding " + acct);
		final String osCommand = 
			String.format(ADD_STRING, 
				acct.getUsername(),
				acct.getPassword(),
				acct.getFullName());
		return runCommand("Account creation", acct, osCommand);
	}

	/** Sync Account's password out to its system account. */
	public boolean updateAccount(Account acct) {
		System.out.println("Synching " + acct + "'s password to the os");
		final String osCommand = 
			String.format(MOD_STRING, 
				acct.getUsername(),
				acct.getPassword(),
				acct.getFullName());
		return runCommand("Account update", acct, osCommand);
	}

	public boolean deleteAccount(Account acct) {
		System.out.println("Deleting " + acct + "'s password to the os");
		final String osCommand = 
			String.format("sudo userdel -r %s", 
				acct.getUsername());
		return runCommand("Account deletion", acct, osCommand);
	}

	/** Obvious common code for account work */
	private boolean runCommand(String runType, @SuppressWarnings("unused")
	Account acct, String osCommand) {
		// System.out.println("Running " + osCommand);
		try {
			String args[] = { "sh", "-c", osCommand };
			final ProcessBuilder builder = new ProcessBuilder(args);
			builder.redirectErrorStream(true);
			final Process proc = builder.start();
			BufferedReader is = new BufferedReader(
				new InputStreamReader(proc.getInputStream()));
			String line;
			// Any line it prints on {stdout,stderr} becomes a FacesMessage
			while ((line = is.readLine()) != null) {
				System.out.println("-->" + line);
				FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR,
				"System command for " + runType + " failed: " + line);
			}
			int ret = proc.waitFor();

			if (ret == 0) {
				// Now try to sync our changed config data to NIS
				// Catch any errors here as this is NOT fatal; the
				// changes will get synched later by a cron job.
				args[2] = SYNCH_STRING;
				try {
					System.out.println("Running " + SYNCH_STRING);
					new ProcessBuilder(args).start().waitFor();
				} catch (Throwable t) {
					FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR,
					"System command to update NIS data failed: " + t);
				}
			}
			// Return with main command's ret
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
}

package action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.faces.application.FacesMessage;

import org.jboss.seam.faces.FacesMessages;

public class UnixCommand {
	
	/** Run a UNIX command (via sh -c), capture ANY output
	 * as FacesMessages, and return true if the command succeeded.
	 * @param runType - a message like 'create account'
	 * @param osCommand - the actual OS command string
	 */
	public static boolean runCommand(String runType, String osCommand) {
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
				FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR,
				"System command for " + runType + " failed: " + line);
			}
			int ret = proc.waitFor();

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

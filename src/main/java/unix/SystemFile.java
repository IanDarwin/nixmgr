package unix;

/** Somewhat controlled access to read/write files
 */
public enum SystemFile {
	
	WHITELIST("/etc/whitelist");

	private final String unixName;

	private SystemFile(final String unixName) {
		this.unixName = unixName;
	}

	public String getUnixName() {
		return unixName;
	}
}

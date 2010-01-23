package unix;

/** Somewhat controlled access to read/write files
 */
public enum SystemFile {
	
	WHITELIST("/etc/whitelist"),
	RESTRICTEDWORKSATATIONLIST("/etc/restrictedworkstations");

	private final String unixName;

	private SystemFile(final String unixName) {
		this.unixName = unixName;
	}

	public String getName() {
		// If you ever need another OS, expand the constructor args, and
		// switch on the current OS here. For now:
		return unixName;
	}
}

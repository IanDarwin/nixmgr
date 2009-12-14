package action;

import model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("whitelistList")
public class WhitelistList extends EntityQuery<Whitelist> {

	private static final String EJBQL = "select whitelist from Whitelist whitelist";

	private static final String[] RESTRICTIONS = {
	"lower(whitelist.name) like concat(lower(#{whitelistList.whitelist.name}),'%')",
	};

	private Whitelist whitelist = new Whitelist();

	public WhitelistList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
		setOrder("whiteList.name");
	}

	public Whitelist getWhitelist() {
		return whitelist;
	}
}

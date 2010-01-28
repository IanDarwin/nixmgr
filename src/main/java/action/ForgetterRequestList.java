package action;

import model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("forgetterRequestList")
public class ForgetterRequestList extends EntityQuery<ForgetterRequest> {

	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select forgetterrequest from ForgetterRequest forgetterrequest";

	private static final String[] RESTRICTIONS = {"lower(forgetterrequest.requestcode) like lower(concat(#{forgetterRequestList.forgetterRequest.requestCode},'%'))",};

	private ForgetterRequest forgetterRequest = new ForgetterRequest();

	public ForgetterRequestList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ForgetterRequest getForgetterRequest() {
		return forgetterRequest;
	}
}

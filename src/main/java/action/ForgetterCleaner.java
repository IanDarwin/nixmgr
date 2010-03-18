package action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import model.ForgetterRequest;

/** Quartz-scheduled bean to clean up old unused ForgetterRequest objects */
@Stateless(activationConfig = {
	// The cronTrigger expression attempts to run once/day at 2345
	@ActivationConfigProperty(
		propertyName="cronTrigger",
		propertyValue="0 45 23 * * ?")
})
public class ForgetterCleaner implements Job {
	
	/** How many days requests can kick around - must be enough for long weekends */
	public static final int DAYS = 7;

	@In
	private EntityManager entityManager;

	/** XXX rewrite using ForgetterRequestList, doh! */
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		System.out.println("ForgetterCleaner.execute()");
		final Calendar c = Calendar.getInstance();
		c.roll(Calendar.DAY_OF_MONTH, - DAYS);
		final Date d = c.getTime();
		final Query query = entityManager.createQuery("from ForgetterRequest r where r.recordCreated < ?");
		query.setParameter(1, d);
		List<ForgetterRequest> list = query.getResultList();
		for (ForgetterRequest fr : list) {
			System.out.println("Removing expired request " + fr);
			entityManager.remove(fr);
		}
	}
}

package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.darwinsys.security.PassPhrase;

/** 
 * "Forgotten Password Request" model.
 * When somebody who MIGHT be the user requests a
 * password change, we use this Entity to record the
 * request, and, notify the user at their registered
 * email address, with email containing a link to
 * a URL that will actually change the password and
 * notify the user that it's been changed.
 */
@Entity
@Table(name = "forgetterRequest", schema = "public")
public class ForgetterRequest implements java.io.Serializable {

	private static final long serialVersionUID = -281819781979721L;
	private int id;
	private String requestCode = PassPhrase.getNext(18);
	private Account person;
	private Date recordCreated = new Date();

	public ForgetterRequest() {
		// nothing to do
	}
	
	public ForgetterRequest(Account person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "ForgetterRequest[" + id + 
			"," + requestCode + "," + 
			(person == null ? "NO PERSON" : person.getUsername()) + "]";
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", unique = true, nullable = false, insertable = true, updatable = false)
	@NotNull
	public Account getPerson() {
		return person;
	}
	public void setPerson(Account person) {
		this.person = person;
	}

	@Column(name = "request_code", nullable = false, length = 32)
	@NotNull
	@Length(max = 32)
	public String getRequestCode() {
		return this.requestCode;
	}

	public void setRequestCode(String requestcode) {
		this.requestCode = requestcode;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "RECORD_CREATED", nullable = false, length = 10)
	@NotNull
	public Date getRecordCreated() {
		return this.recordCreated;
	}

	public void setRecordCreated(Date recordCreated) {
		this.recordCreated = recordCreated;
	}
}

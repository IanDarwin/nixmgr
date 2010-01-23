package action;

import org.jboss.seam.annotations.Name;

import com.darwinsys.security.PassPhrase;

@Name("randomPass")
public class RandomPass {
	
	public String getRandom() {
		return PassPhrase.getNext(7);
	}
}

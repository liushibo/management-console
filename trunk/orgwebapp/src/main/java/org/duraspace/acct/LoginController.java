package org.duraspace.acct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.domain.User;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class LoginController extends SimpleFormController {

    protected final Log log = LogFactory.getLog(getClass());

    private User user;

	public LoginController()
	{
		setCommandClass(User.class);
		setCommandName("user");
	}

	@Override
	protected ModelAndView onSubmit(Object command,
			BindException bindException)
	throws Exception
	{
		User user = (User)command;
		log.info("submitting user: "+user);
		return new ModelAndView(getSuccessView());
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
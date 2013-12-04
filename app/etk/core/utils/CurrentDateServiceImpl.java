package etk.core.utils;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CurrentDateServiceImpl implements CurrentDateService {

	@Override
	public Date asJavaDate() {
		return new Date();
	}

}

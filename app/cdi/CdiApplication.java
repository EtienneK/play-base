package cdi;

import javax.enterprise.inject.Alternative;

import play.Application;

@Alternative
public class CdiApplication extends Application {

	public CdiApplication() {
		super(null);
	}

	public CdiApplication(Application application) {
		super(application.getWrappedApplication());
	}

}

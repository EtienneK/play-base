package cdi.auth;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import play.Application;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.Token;

public class UserServicePlugin extends securesocial.core.java.BaseUserService {

	private UserService delegate;

	public UserServicePlugin(Application application) {
		super(application);
		delegate = BeanProvider.getContextualReference(UserService.class, false);
	}

	@Override
	public Identity doSave(Identity user) {
		return delegate.doSave(user);
	}

	@Override
	public void doSave(Token token) {
		delegate.doSave(token);
	}

	@Override
	public Identity doFind(IdentityId identityId) {
		return delegate.doFind(identityId);
	}

	@Override
	public Token doFindToken(String tokenId) {
		return delegate.doFindToken(tokenId);
	}

	@Override
	public Identity doFindByEmailAndProvider(String email, String providerId) {
		return delegate.doFindByEmailAndProvider(email, providerId);
	}

	@Override
	public void doDeleteToken(String uuid) {
		delegate.doDeleteToken(uuid);
	}

	@Override
	public void doDeleteExpiredTokens() {
		delegate.doDeleteExpiredTokens();
	}

}

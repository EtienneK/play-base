package etk.security;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.joda.time.DateTime;

import securesocial.core.Identity;
import securesocial.core.IdentityId;
import etk.core.models.Token;
import etk.core.models.User;
import etk.core.utils.CurrentDateService;

@ApplicationScoped
public class UserServiceImpl implements UserService {

	private EntityManager entityManager;
	private CurrentDateService currentDateService;

	protected UserServiceImpl() {
	}

	@Inject
	public UserServiceImpl(EntityManager entityManager, CurrentDateService currentDateService) {
		this.entityManager = entityManager;
		this.currentDateService = currentDateService;
	}

	@Override
	public Identity doSave(Identity identity) {
		entityManager.persist(User.createFrom(identity));
		return identity;
	}

	@Override
	public void doSave(securesocial.core.java.Token token) {
		entityManager.persist(new etk.core.models.Token(token.uuid, token.email, token.creationTime.toDate(),
				token.expirationTime.toDate(), token.isSignUp));
	}

	@Override
	public Identity doFind(IdentityId userId) {
		try {
			return (User) entityManager
					.createQuery(
							"FROM " + User.class.getName() + " WHERE userId = :userId AND providerId = :providerId")
					.setParameter("userId", userId.userId()).setParameter("providerId", userId.providerId())
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public securesocial.core.java.Token doFindToken(String uuid) {
		Token fetched = entityManager.find(Token.class, uuid);
		securesocial.core.java.Token ret = new securesocial.core.java.Token();

		ret.creationTime = new DateTime(fetched.getCreationTime());
		ret.email = fetched.getEmail();
		ret.expirationTime = new DateTime(fetched.getExpirationTime());
		ret.isSignUp = fetched.isSignUp();
		ret.uuid = fetched.getUuid();

		return ret;
	}

	@Override
	public Identity doFindByEmailAndProvider(String email, String providerId) {
		try {
			return (User) entityManager
					.createQuery("FROM " + User.class.getName() + " WHERE email = :email AND providerId = :providerId")
					.setParameter("email", email).setParameter("providerId", providerId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public void doDeleteToken(String uuid) {
		entityManager.remove(doFindToken(uuid));
	}

	@Override
	public void doDeleteExpiredTokens() {
		List<Token> tokens = entityManager
				.createQuery("FROM " + Token.class.getName() + " WHERE expirationTime < :now", Token.class)
				.setParameter("now", currentDateService.asJavaDate()).getResultList();

		for (Token token : tokens) {
			entityManager.remove(token);
		}
	}

}

package cdi.auth;

import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.Token;

public interface UserService {
	/**
	 * Saves the Identity. This method gets called when a user logs in. This is
	 * your chance to save the user information in your backing store.
	 * 
	 * @param user
	 */
	public abstract Identity doSave(Identity user);

	/**
	 * Saves a token
	 * 
	 * Note: If you do not plan to use the UsernamePassword provider just
	 * provide en empty implementation
	 * 
	 * @param token
	 */
	public abstract void doSave(Token token);

	/**
	 * Finds the user in the backing store.
	 * 
	 * @return an Identity instance or null if no user matches the specified id
	 */
	public abstract Identity doFind(IdentityId identityId);

	/**
	 * Finds a token
	 * 
	 * Note: If you do not plan to use the UsernamePassword provider just
	 * provide en empty implementation
	 * 
	 * @param tokenId
	 *            the token id
	 * @return a Token instance or null if no token matches the specified id
	 */
	public abstract Token doFindToken(String tokenId);

	/**
	 * Finds an identity by email and provider id.
	 * 
	 * Note: If you do not plan to use the UsernamePassword provider just
	 * provide en empty implementation.
	 * 
	 * @param email
	 *            - the user email
	 * @param providerId
	 *            - the provider id
	 * @return an Identity instance or null if no user matches the specified id
	 */
	public abstract Identity doFindByEmailAndProvider(String email, String providerId);

	/**
	 * Deletes a token
	 * 
	 * Note: If you do not plan to use the UsernamePassword provider just
	 * provide en empty implementation
	 * 
	 * @param uuid
	 *            the token id
	 */
	public abstract void doDeleteToken(String uuid);

	/**
	 * Deletes all expired tokens
	 * 
	 * Note: If you do not plan to use the UsernamePassword provider just
	 * provide en empty implementation
	 * 
	 */
	public abstract void doDeleteExpiredTokens();
}

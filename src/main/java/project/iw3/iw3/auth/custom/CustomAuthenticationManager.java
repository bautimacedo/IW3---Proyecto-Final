package project.iw3.iw3.auth.custom;

import java.util.Collection;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import project.iw3.iw3.auth.IUserBusiness;
import project.iw3.iw3.auth.User;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {

	private IUserBusiness userBusiness;

	private PasswordEncoder pEncoder;

	
	// constructor
	public CustomAuthenticationManager(PasswordEncoder pEncoder, IUserBusiness userBusiness) {
		this.pEncoder = pEncoder;
		this.userBusiness = userBusiness;
	}

	@Override // recibe una autenticacion incompleta y devuelve una completa basicamente.
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName(); //obtengo username y password que vienen en texto plano.
		String password = authentication.getCredentials().toString();
		User user = null;

		try {
			user = userBusiness.load(username); //trato de cargar el usuario x su username.
		} catch (NotFoundException e) {
			throw new BadCredentialsException(e.getMessage());
		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
			throw new AuthenticationServiceException(e.getMessage());
		}
		String validation = user.validate(); // acordate que este metodo hacia todas las comprobaciones si estaba la cuenta expirada, bloqueada, etc.
		if (validation.equals(User.VALIDATION_ACCOUNT_EXPIRED))
			throw new AccountExpiredException(User.VALIDATION_ACCOUNT_EXPIRED);
		if (validation.equals(User.VALIDATION_CREDENTIALS_EXPIRED))
			throw new CredentialsExpiredException(User.VALIDATION_CREDENTIALS_EXPIRED);
		if (validation.equals(User.VALIDATION_DISABLED))
			throw new DisabledException(User.VALIDATION_DISABLED);
		if (validation.equals(User.VALIDATION_LOCKED))
			throw new LockedException(User.VALIDATION_LOCKED);
		if (!pEncoder.matches(password, user.getPassword())) // si la password no matchea tirainvalid password.
			throw new BadCredentialsException("Invalid password");
		return new UsernamePasswordAuthenticationToken(user, null,user.getAuthorities()); //devuelvo una instancia de Authentication.
		// fijate que requiere el usuario y las authorities (osea los roles)

		
	}
	@SuppressWarnings("serial")
	public Authentication authWrap(String name, String pass) {
		return new Authentication() {
			@Override
			public String getName() {
				return name;
			}
			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			}
			@Override
			public boolean isAuthenticated() {
				return false;
			}
			@Override
			public Object getPrincipal() {
				return null;
			}
			@Override
			public Object getDetails() {
				return null;
			}
			@Override
			public Object getCredentials() {
				return pass;
			}
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		};
	}

}
package project.iw3.iw3.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserBusiness implements IUserBusiness {

	@Autowired
	private UserRepository userDAO;



	@Override
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException { //fijate que busco o por mail o por username (ambos campos son unique)
		Optional<User> ou;
		try {
			ou = userDAO.findOneByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ou.isEmpty()) { // si no lo encuentro
			throw NotFoundException.builder().message("No se encuentra el usuari@ email o nombre =" + usernameOrEmail)
					.build();
		}
		return ou.get();
	}

	@Override //  el password encoder es xq nosotros en la bd no guardamos contraseñas en texto plano.
	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		if (!pEncoder.matches(oldPassword, user.getPassword())) { //oldpassword seria en texto plano, y la password vieja en la bd la obtengo con getpassword. matches se fija si son la misma con el decodificador.
			throw BadPasswordException.builder().build();
		}
		user.setPassword(pEncoder.encode(newPassword));
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, false);
	}

	@Override
	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, true);
	}

	private void setDisable(String usernameOrEmail, boolean enable) throws NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		user.setEnabled(enable);
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public List<User> list() throws BusinessException {
		try {
			return userDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

}
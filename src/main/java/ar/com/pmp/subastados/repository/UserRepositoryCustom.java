package ar.com.pmp.subastados.repository;

import java.util.List;

import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.service.dto.FilterUserDTO;

public interface UserRepositoryCustom {

	List<User> searchUsers(FilterUserDTO filter);

}

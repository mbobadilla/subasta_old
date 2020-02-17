package ar.com.pmp.subastados.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import ar.com.pmp.subastados.domain.DestacadoCaballo;
import ar.com.pmp.subastados.service.dto.DestacadoCaballoDTO;

@Service
public class DestacadoCaballoMapper {

	public DestacadoCaballoDTO persistenceToDTO(DestacadoCaballo source) {
		return new DestacadoCaballoDTO(source);
	}

	public List<DestacadoCaballoDTO> persistencesToDTOs(List<DestacadoCaballo> source) {
		return source.stream().filter(Objects::nonNull).map(this::persistenceToDTO).collect(Collectors.toList());
	}

	public DestacadoCaballo DTOtoPersistence(DestacadoCaballoDTO sourceDTO) {
		if (sourceDTO == null) {
			return null;
		} else {
			DestacadoCaballo target = new DestacadoCaballo();
			BeanUtils.copyProperties(sourceDTO, target);
			return target;
		}
	}

	public List<DestacadoCaballo> DTOstoPersistences(List<DestacadoCaballoDTO> userDTOs) {
		return userDTOs.stream().filter(Objects::nonNull).map(this::DTOtoPersistence).collect(Collectors.toList());
	}

}

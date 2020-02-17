package ar.com.pmp.subastados.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import ar.com.pmp.subastados.domain.Product;
import ar.com.pmp.subastados.service.dto.ProductDTO;

@Service
public class ProductMapper {

	public ProductDTO persistenceToDTO(Product source) {
		return new ProductDTO(source);
	}

	public List<ProductDTO> persistencesToDTOs(List<Product> source) {
		return source.stream().filter(Objects::nonNull).map(this::persistenceToDTO).collect(Collectors.toList());
	}

	public Product DTOtoPersistence(ProductDTO sourceDTO) {
		if (sourceDTO == null) {
			return null;
		} else {
			Product target = new Product();
			BeanUtils.copyProperties(sourceDTO, target);
			return target;
		}
	}

	public List<Product> DTOstoPersistences(List<ProductDTO> userDTOs) {
		return userDTOs.stream().filter(Objects::nonNull).map(this::DTOtoPersistence).collect(Collectors.toList());
	}

}

package com.orion.engineering.configuration.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationDAO extends JpaRepository<ConfigurationModel, String> {
	Optional<ConfigurationModel> findByKey(String key);

	List<ConfigurationModel> findAllByType(String type);
}
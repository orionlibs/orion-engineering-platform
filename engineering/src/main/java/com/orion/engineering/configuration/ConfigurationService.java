package com.orion.engineering.configuration;

import com.orion.engineering.configuration.model.ConfigurationDAO;
import com.orion.engineering.configuration.model.ConfigurationModel;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService {
	@Autowired
	private ConfigurationDAO dao;

	public Optional<ConfigurationModel> getByKey(String key) {
		return dao.findByKey(key);
	}

	@Transactional
	public List<ConfigurationModel> getByType(String type) {
		return dao.findAllByType(type);
	}

	@Transactional
	public ConfigurationModel save(ConfigurationModel model) {
		return dao.save(model);
	}
}

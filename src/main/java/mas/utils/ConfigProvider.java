package mas.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import mas.exceptions.InvalidConfigException;
import mas.models.common.Context;
import mas.models.common.enums.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ConfigProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigProvider.class);

	private Context getConfigsFromJson(EnvironmentType env, String filePath) {
		ObjectMapper mapper = new ObjectMapper();		
		Context context = null;
		
		try {
			context = mapper.readValue(new File(filePath), Context.class);	
			context.setEnvironment(env);
			
		} catch (IOException e) {
			LOGGER.error("Error while reading config json file: " + e.getMessage());
		}
		return context;
	}
	
	public Context getConfigs(String environment, String configFile) throws InvalidConfigException {		
		Optional<EnvironmentType> parsedEnvironment = ArgumentsValidator.defineEnvironment(environment);
		Boolean fileIsCorrect = ArgumentsValidator.fileExistsAndIsNotDirectory(configFile);

		if(parsedEnvironment.isPresent() && fileIsCorrect) {
			return  getConfigsFromJson(parsedEnvironment.get(), configFile);			

		} else {
			throw new InvalidConfigException("Invalid configs");
		}
	}
}

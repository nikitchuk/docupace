package mas.utils;

import mas.models.common.enums.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentsValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentsValidator.class);
	
	private ArgumentsValidator() {}	
	
	
	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(ePattern);
		Matcher m = p.matcher(email);
		
		if(m.matches()) {
			return true;
		} else {
			LOGGER.warn(email + " is not valid e-mail address.");
			return false;
		}
	}	
	
	public static Optional<EnvironmentType> defineEnvironment(String name) {
		if ("QA".equals(name)) {
			return Optional.of(EnvironmentType.QA);
					
		} else if("STAGE".equals(name)){
			return Optional.of(EnvironmentType.STAGE);
		} else {
			LOGGER.warn("UNKNOWN ENVIRONMENT NAME");
			return Optional.ofNullable(null);
		}
	}
	
	public static boolean fileExistsAndIsNotDirectory(String path) {
		File file = new File(path);
		
		return (file.exists() && !file.isDirectory()) ? true : false;
	}
}

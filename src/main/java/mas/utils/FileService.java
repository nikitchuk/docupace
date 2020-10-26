package mas.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
	
	public List<String> importConfigFile(String filePath) {
		File configFile = new File(filePath);
		List<String> lines = new ArrayList<>();

		try {
			lines = Files.readAllLines(Paths.get(configFile.toURI()));
		} catch (IOException e) {
			LOGGER.error("ERROR READING FROM FILE: " + e.getMessage());			
		}
		
		LOGGER.info("CONFIGS IMPORT SUCCESSFUL. IMPORTED: " + lines.size());
		return lines;
	}

	private static final String TEST_DOCUMENT = "testFile.pdf";
	private static final String PHOTO = "documents/house2.jpg";
	private static final String PHOTO_CORRUPTED = "documents/empty_picture.jpg";


	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	public FileService() {
	}

	public static String getPhotoPath() {
		return getAbsolutePath(PHOTO);
	}

	public static String getPhotoPathCorrupted() {
		return getAbsolutePath(PHOTO_CORRUPTED);
	}

	public static String getDocPath(){
		return getAbsolutePath(TEST_DOCUMENT);
	}

	public static String getAbsolutePath(final String resourceFileName) {
		try {
			URL link = Thread.currentThread().getContextClassLoader().getResource(resourceFileName);
			Path path = getPath(link.toURI());
			String absolute = path.toAbsolutePath().toString();
			if ("jar".equals(link.getProtocol())) {
				return makeCopy(path);
			} else {
				return absolute;
			}
		} catch (URISyntaxException | NullPointerException | IOException e) {
			logger.warn("File not found: {}", resourceFileName);
			return null;
		}
	}

	private static Path getPath(URI uri) throws IOException {
		try {
			return Paths.get(uri);
		} catch (FileSystemNotFoundException e) {
			final String[] uriParts = uri.toString().split("!");
			final FileSystem fs = FileSystems.newFileSystem(URI.create(uriParts[0]), new HashMap<>());
			return fs.getPath(uriParts[1]);
		}
	}

	private static String makeCopy(Path source) throws IOException {
		Path target = Paths.get(
				new File("").getAbsolutePath(),
				"target",
				source.getFileName().toString());
		Path newFile = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		return newFile.toAbsolutePath().toString();
	}

	public static Path getResourceFilePath(final String resourceFileName) {
		try {
			URL link = Thread.currentThread().getContextClassLoader().getResource(resourceFileName);
			return getPath(link.toURI());
		} catch (IOException | URISyntaxException e) {
			return null;
		}
	}

}

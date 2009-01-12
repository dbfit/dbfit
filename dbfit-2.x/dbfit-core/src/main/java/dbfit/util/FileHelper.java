package dbfit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for Fit Fixtures.
 */
public final class FileHelper {
    private static final int BUFFER_SIZE = 4096;
    private static final String FITNESSE_ROOT_DIR = "FitNesseRoot";
    private static final String FILES = "files";
    private static final String FILES_DIR = File.separatorChar + FILES + File.separatorChar;

    private FileHelper() {
    }

    /**
     * Get a File from the files folder.
     * 
     * @param fileName the filename relative the <code>files</code> folder.
     * @return the File
     */
    public static File getFile(String fileName) {
        File file;
        // Normalize fileName for current platform
        String platformFileName = fileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        if (platformFileName.startsWith(FILES_DIR)) {
            // References a file within the "files" directory
            file = new File(FITNESSE_ROOT_DIR + platformFileName);
        } else if (platformFileName.startsWith(File.separator)
                || (platformFileName.length() >= 2 && Character.isLetter(platformFileName.charAt(0)) && platformFileName
                    .charAt(1) == ':')) {
            // Interpret as absolute file name
            file = new File(platformFileName);
        } else {
            // Interpret as file name relative to the "files" directory
            file = new File(FITNESSE_ROOT_DIR + FILES_DIR + platformFileName);
        }
        return file;
    }

    /**
     * Get the content of a File from the files folder as an InputStream.
     * 
     * @param fileName the filename relative the <code>files</code> folder.
     * @return the File content as an InputStream
     * @throws FileNotFoundException if file not found
     */
    public static InputStream getFileAsStream(String fileName) throws FileNotFoundException {
        File file = getFile(fileName);
        FileInputStream fis = new FileInputStream(file);
        return fis;
    }

    /**
     * Get the content of a File from the files folder as an InputStream.
     * 
     * @param fileName the fileName relative the <code>files</code> folder.
     * @return the File content as an InputStream
     * @throws IOException if file not found of not readable
     */
    public static String getFileAsString(String fileName) throws IOException {
        InputStream fis = getFileAsStream(fileName);
        StringBuffer sb = new StringBuffer();
        char[] buf = new char[BUFFER_SIZE];
        int len = 0;
        InputStreamReader reader = new InputStreamReader(fis);
        try {
            while ((len = reader.read(buf)) > 0) {
                sb.append(buf, 0, len);
            }
        } finally {
            reader.close();
        }
        
        return sb.toString();
    }

}

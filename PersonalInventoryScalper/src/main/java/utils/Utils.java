package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

//From https://github.com/steevp/UpdogFarmer/blob/f476c77aae1f1553456dc240c4c2407234f56dad/app/src/main/java/com/steevsapps/idledaddy/utils/Utils.java
public class Utils {
	private static final Logger log = (Logger) LoggerFactory.getLogger(Utils.class);
	
    private final static String SHA1_ALGORITHM = "SHA-1";
    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /**
     * Formats a date of MMM dd, yyyy hh:mm a
     * @param s
     * @return
     */
    public static long dateToUnix(String s) {
    	DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mmaZ");
		try {
			Date date = dateFormat.parse(s+"-0800"); //Login is done in washington (GMT-0800) and thus times are returned from there
			long unixTime = (long) date.getTime()/1000;
			return unixTime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error("Bad date string: " + s);
			e.printStackTrace();
			return -1;
		}
    }
    
    /**
     * Convert byte array to hex string
     * https://stackoverflow.com/a/9855338
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Convert ArrayList to comma-separated String
     */
    public static String arrayToString(List<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (int i=0,size=list.size();i<size;i++) {
            final String string = list.get(i);
            builder.append(string);
            if (i + 1 < size) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    /**
     * Convert array to comma-separated String
     */
    public static String arrayToString(String[] array) {
        return arrayToString(Arrays.asList(array));
    }

    /**
     * Save Logcat to file
     */
    public static void saveLogcat(File file) throws IOException {
        final Process p = Runtime.getRuntime().exec("logcat -d");
        try ( final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
              final BufferedWriter bw = new BufferedWriter(new FileWriter(file)) ) {
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Strips non-ASCII characters from String
     */
    public static String removeSpecialChars(String s) {
        return s.replaceAll("[^\\u0000-\\u007F]", "");
    }

    /**
     * Check if API key is valid
     */
    public static boolean isValidKey(String key) {
        return key.matches("^[0-9A-Fa-f]+$");
    }

    /**
     * Calculate the SHA-1 hash of a file
     */
    public static byte[] calculateSHA1(File file) throws IOException, NoSuchAlgorithmException {
        try (final InputStream fis = new FileInputStream(file)) {
            final MessageDigest md = MessageDigest.getInstance(SHA1_ALGORITHM);
            final byte[] buffer = new byte[8192];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                md.update(buffer, 0, n);
            }
            return md.digest();
        }
    }

    /**
     * Get the current unix time
     */
    public static long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Calculate HMAC SHA1
     */
    public static byte[] calculateRFC2104HMAC(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        final SecretKeySpec secretKey = new SecretKeySpec(key, HMAC_SHA1_ALGORITHM);
        final Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    /**
     * Run a block of code a maximum of maxTries
     *
    public static void runWithRetries(int maxTries, ThrowingTask task) throws Exception {
        int count = 0;
        while (count < maxTries) {
            try {
                task.run();
                return;
            } catch (Exception e) {
                if (++count >= maxTries) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
    } */
}

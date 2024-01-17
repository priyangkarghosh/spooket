package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * The Class PlayerPrefs.
 * 
 * @author priyangkar ghosh
 */
public class PlayerPrefs {

	/** The PlayerPrefs path. */
	private static final String PATH = "prefs.txt";

	/** The prefs hash map. */
	private static HashMap<String, String> prefs = new HashMap<>();

	/**
	 * Inits the player prefs.
	 */
	public static void init() {
		try {
			// gets the file
			BufferedReader br = new BufferedReader(new FileReader(PATH));
			
			// reads the first line
			// line is formatted in the same way as a hash map
			String line = br.readLine();
			if (line == null) {
				br.close();
				return;
			}
			
			// removes the []
			line = line.substring(1, line.length() - 1);
			
			// closes the reader
			br.close();
			
			// checks if there is anything saved
			if (line.length() == 0) return;
			
			// reads the data after splitting it by comma
			String[] data = line.split(", ");
			for (String pair : data) {
				// the key and value are seperated by =
				int regex = pair.indexOf('=');
				
				// sets the prefs hash map
				set(
					pair.substring(0, regex),
					pair.substring(regex + 1)
				);
			}
		}

		catch (FileNotFoundException e) {
			System.out.println("Player prefs file was not found.");
		}

		catch (IOException e) {
			System.out.println("Error reading player prefs file.");
		}
	}

	/**
	 * Saves the player prefs.
	 */
	public static void save() {
		try {
			// writes data to saved dataPath (the file which data was read from)
			BufferedWriter bw = new BufferedWriter(new FileWriter(PATH));
			bw.write(prefs.toString());
			bw.close();
		}

		catch (FileNotFoundException e) {
			System.out.println("No file at the specified path.");
		}

		catch (IOException e) {
			System.out.println("Error writing in the file.");
		}
	}

	/**
	 * Gets the integer value from a key.
	 *
	 * @param {String} key - the key
	 * @param {int} defaultValue - the default value
	 * @return {int} the int value of the key
	 */
	public static int get(String key, int defaultValue) {
		try {
			return Integer.parseInt(prefs.get(key));
		}

		catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * Gets the double value from a key.
	 *
	 * @param {String} key - the key
	 * @param {double} defaultValue - the default value
	 * @return {double} the double value of the key
	 */
	public static double get(String key, double defaultValue) {
		try {
			return Double.parseDouble(prefs.get(key));
		}

		catch (NumberFormatException | NullPointerException e) {
			return defaultValue;
		}
	}

	/**
	 * Gets the String value from a key.
	 *
	 * @param {String} key - the key
	 * @param {String} defaultValue - the default value
	 * @return {String} the double value of the key
	 */
	public static String get(String key, String defaultValue) {
		String value = prefs.get(key);
		if (value == null) return defaultValue;
		return value;
	}

	/**
	 * Sets the String value with key and value.
	 *
	 * @param {String} key - the key
	 * @param {int} value - the value
	 */
	public static void set(String key, int value) {
		prefs.put(key, String.valueOf(value));
	}

	/**
	 * Sets the String value with key and value.
	 *
	 * @param {String} key - the key
	 * @param {double} value - the value
	 */
	public static void set(String key, double value) {
		prefs.put(key, String.valueOf(value));
	}

	/**
	 * Sets the String value with key and value.
	 *
	 * @param {String} key - the key
	 * @param {String} value - the value
	 */
	public static void set(String key, String value) {
		prefs.put(key, value);
	}

	/**
	 * Removes the value by key.
	 *
	 * @param {String} key - the key
	 */
	public static void remove(String key) {
		prefs.remove(key);
	}
}

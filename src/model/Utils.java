package model;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.stage.FileChooser;

/**
 * Utility functions for ALGY
 * @author dorian
 */
public class Utils {

	/**
	 * Different log levels
	 */
	public enum Level {
		INFO("[ALGY-INFO] %1$s"), LOG("[ALGY-LOG] %1$s"), RUN("[ALGY-RUN] %1$s"), ERROR("[ALGY-ERROR] %1$s"), COMPIL(
				"[ALGY-COMPIL] %1$s"), BTCODE("[ALGY-BYTECODE] %1$s");

		private String prefix;

		public String getPrefix() {
			return prefix;
		}

		private Level(String prefix) {
			this.prefix = prefix;
		}

	}

	/**
	 * Help to process code string in order to export the program in text format
	 */
	private static final HashMap<String, BiFunction<String, ListIterator<String>, String>> TEXT_EXPORTER;

	/**
	 * Default function if string not recognised
	 */
	private static final BiFunction<String, ListIterator<String>, String> DEFAULT;

	/**
	 * Define if logs are display or not
	 */
	public static final Properties PROPS;

	/**
	 * Initialize text_exporter lambdas
	 */
	static {
		PROPS = new Properties();
		loadAppParams();

		BiFunction<String, ListIterator<String>, String> identity = (s, i) -> s;
		DEFAULT = (s, i) -> "";

		TEXT_EXPORTER = new HashMap<>();
		TEXT_EXPORTER.put("pushContinue", (s, i) -> "endif");
		TEXT_EXPORTER.put("macro", (s, i) -> "\n == " + msg("Text.macro.create") + s + " == ");
		TEXT_EXPORTER.put("setFrom", (s, i) -> "\n" + msg("Text.macro.init") + " :");
		TEXT_EXPORTER.put("setUntilInsertion", (s, i) -> "\n" + msg("Text.macro.cond") + " :");
		TEXT_EXPORTER.put("setCore", (s, i) -> "\n" + msg("Text.macro.loop") + " :");
		TEXT_EXPORTER.put("setTerm", (s, i) -> "\n" + msg("Text.macro.terminate") + " :");
		TEXT_EXPORTER.put("pushVar", identity);
		TEXT_EXPORTER.put("pushBranch", (s, i) -> apply(s, i));
		TEXT_EXPORTER.put("TOP_STACK_FALSE",
				(s, i) -> msg("Text.if") + " (" + apply(i) + ") " + msg("Text.then") + " : ");
		TEXT_EXPORTER.put("ALWAYS", (s, i) -> msg("Text.else") + " : ");
		TEXT_EXPORTER.put("pushArray", identity);
		TEXT_EXPORTER.put("pushValue", identity);
		TEXT_EXPORTER.put("pushTwoOp", (s, i) -> {
			String sec = apply(i);
			String fir = apply(i);
			return fir + apply(s, i) + sec;
		});
		TEXT_EXPORTER.put("ADD", (s, i) -> " + ");
		TEXT_EXPORTER.put("SUB", (s, i) -> " - ");
		TEXT_EXPORTER.put("MULT", (s, i) -> " * ");
		TEXT_EXPORTER.put("DIV", (s, i) -> " / ");
		TEXT_EXPORTER.put("MOD", (s, i) -> " % ");
		TEXT_EXPORTER.put("EQUAL", (s, i) -> " == ");
		TEXT_EXPORTER.put("DIFFERENT", (s, i) -> " != ");
		TEXT_EXPORTER.put("LOWER", (s, i) -> " < ");
		TEXT_EXPORTER.put("LOWER_EQUAL", (s, i) -> " <= ");
		TEXT_EXPORTER.put("GREATER", (s, i) -> " > ");
		TEXT_EXPORTER.put("GREATER_EQUAL", (s, i) -> " >= ");
		TEXT_EXPORTER.put("pushIndexedVar", (s, i) -> s + "[" + apply(i) + "]");
		TEXT_EXPORTER.put("indexedAssign", (s, i) -> s + "[" + apply(i) + "] = " + apply(i));
		TEXT_EXPORTER.put("assign", (s, i) -> s + " = " + apply(i));
		TEXT_EXPORTER.put("intPrompt", (s, i) -> "? : " + msg("Text.variable.read") + "\"" + s + "\"");
		TEXT_EXPORTER.put("charPrompt", (s, i) -> "? : " + msg("Text.variable.read") + "\"" + s + "\"");
		TEXT_EXPORTER.put("print", (s, i) -> {
			String res = msg("Text.variable.write") + " ";
			i.previous();
			i.previous();
			if (i.previous().matches("push(Va(lue|r)|Array).*")) {
				i.next();
				res += apply(i) + " " + msg("Text.variable.with") + " \"" + s + "\"";
			} else {
				i.next();
				res += "\"" + s + "\"";
			}
			return res;
		});
		TEXT_EXPORTER.put("callMacro", (s, i) -> s);

	}

	/**
	 * Ressource getter object for lang
	 */
	private static ResourceBundle bundle;

	/**
	 * Current language of ALGY
	 */
	private static String lang;

	/**
	 * Transform serialized object into string
	 * @param object Object loaded
	 * @return String to analyse
	 * @throws IOException Launched if error during conversion
	 */
	public static String asString(Serializable object) throws IOException {
		String res = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(stream);
		oos.writeObject(object);
		oos.close();
		res = Base64.getEncoder().encodeToString(stream.toByteArray());
		return res;
	}

	/**
	 * Uncode saved data in order to load blocks
	 * @param serializable the serialized data from the file
	 * @param <R> Type of return
	 * @return Usable string for blocks construction
	 */
	@SuppressWarnings("unchecked")
	public static <R> R from(String serializable) {
		R obj = null;
		if (serializable != null && serializable.length() > 0) {
			byte[] data = Base64.getDecoder().decode(serializable);
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			try (ObjectInputStream ois = new ObjectInputStream(stream);) {

				obj = (R) ois.readObject();
			} catch (IOException e1) {
				Utils.printLog(Level.ERROR, "Error occured during uncoding of a string see logs file for more informations");
				Utils.writeException(e1);
			} catch (Exception e) {
				Utils.printLog(Level.ERROR, "Error occured during uncoding of a string see logs file for more informations");
				Utils.writeException(e);
			}
		}

		return obj;
	}

	/**
	 * Change language of ALGY
	 * @param lang Current language
	 */
	public static void changeLanguage(String lang) {
		Utils.printLog(Level.LOG, "Language changed to : " + lang.toUpperCase());
		Utils.lang = lang;
		File file = new File("lang");
		URL[] urls = null;
		try {
			urls = new URL[] { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			bundle = ResourceBundle.getBundle("Msg", new Locale(lang), loader);
		} catch (MalformedURLException e) {
			Utils.printLog(Level.ERROR, "Error occured during language changing see logs file for more informations");
			Utils.writeException(e);
		}
	}
	
	/**
	 * Load parameters in properties file
	 */
	private static void loadAppParams() {
		try {
			File file = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "params.properties");
			if (!file.exists()) {
				BufferedWriter writer = Files.newBufferedWriter(file.toPath());
				for (Level level : Level.values())
					writer.write(level.name() + " = false\n");
				writer.close();
			}
			PROPS.load(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

		} catch (IOException e) {
			Utils.printLog(Level.ERROR, "Error occured during loading params of ALGY see logs for more informations");
			Utils.writeException(e);
		}
	}

	/**
	 * Print logs in console
	 * @param level level of the log to print used with {@link model.Utils.Level}
	 * @param log Log to print
	 */
	public static void printLog(Level level, String log) {
		if (String.valueOf(true).equals(PROPS.getProperty(level.name())))
			if (level.equals(Level.ERROR))
				System.err.println(String.format(level.prefix, log));
			else
				System.out.println(String.format(level.prefix, log));

	}

	/**
	 * Get the matching string according to the key
	 * @param key Key corresponding to a line in properties
	 * @return Matched string with the key
	 */
	public static String msg(String key) {
		if (bundle == null)
			changeLanguage(System.getProperty("user.language"));
		return bundle.getString(key);
	}

	/**
	 * Function to translate code into text
	 * @param i iterator in the code
	 * @return A translated string
	 */
	private static String apply(ListIterator<String> i) {
		String res = null;
		if (i.hasPrevious())
			res = apply(i.previous(), i);

		return res;
	}

	/**
	 * Function to translate code into text
	 * @param line Line to translate
	 * @param i iterator in the code
	 * @return Translated line
	 */
	private static String apply(String line, ListIterator<String> i) {
		String[] tab = line.split(" ", 2);
		return TEXT_EXPORTER.getOrDefault(tab[0], DEFAULT).apply((tab.length > 1 ? tab[1] : null), i);
	}

	/**
	 * Transform the code into readable text for algorithm
	 * @param code Code to translate
	 * @return String to export into file
	 */
	public static String exportToText(String code) {
		String[] lines = code.split("\n");
		ListIterator<String> it = Arrays.asList(lines).listIterator(lines.length);
		StringBuilder sb = new StringBuilder();

		while (it.hasPrevious()) {
			String line = apply(it);
			if (line.trim().length() > 0)
				sb.insert(0, line + "\n");
		}

		List<String> text = new ArrayList<String>(Arrays.asList(sb.toString().split("\n")));
		String prefix = "";
		int i = 0;
		while (i < text.size()) {
			String curr = text.get(i);

			if (curr.matches("\\s*endif.*")) {
				prefix = prefix.substring(2);
				text.remove(i);
				if (text.get(i - 1).matches("\\s*" + msg("Text.else") + ".*"))
					text.set(i - 1, "");
			} else {

				if (curr.matches("\\s*" + msg("Text.if") + " \\(.*")) {

					text.set(i, prefix + curr);
					prefix = prefix + "  ";
				} else if (curr.matches("\\s*" + msg("Text.else") + ".*")) {
					text.set(i, prefix.substring(2) + curr);
				} else {
					text.set(i, prefix + curr);
				}
				i++;
			}
		}

		return text.stream().collect(Collectors.joining("\n"));
	}

	/**
	 * Getter of the current language
	 * @return The language string
	 */
	public static String getLang() {
		return lang;
	}

	/**
	 * Write exceptions stack trace in log file
	 * @param e Exception to write
	 */
	public synchronized static void writeException(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		String stacktrace = errors.toString();
		try {
			errors.close();
		} catch (IOException e1) {
		}

		File fileObj = new File("./logs.txt");
		try {
			BufferedWriter writer = Files.newBufferedWriter(fileObj.toPath(), StandardOpenOption.APPEND,
					StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			String[] stackToPrint = stacktrace.split("\n");
			if (stackToPrint.length == 0) {
				stackToPrint = new String[1];
				stackToPrint[0] = "Error occured but didn't generate stack trace";
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			writer.write("[ALGY-ERROR][" + dateFormat.format(date) + "] Message : " + e.getMessage() + "\n");
			for (String line : stackToPrint) {
				line = "[ALGY-ERROR][" + dateFormat.format(date) + "] " + line + "\n";
				writer.write(line);
			}
			writer.write("[ALGY-ERROR][" + dateFormat.format(date) + "]===================================================================================================================================");
			writer.close();
		} catch (IOException ex) {
			Utils.printLog(Level.ERROR, "Error occured during writing exceptoin stack trace in logs file");
		}
	}

	/**
	 * Save a program in a file
	 * @param text Text to save in the file
	 * @param extension Extension of the file
	 */
	public static void save(String text, String extension) {
		File selected = chooseFile(extension);
		if (selected != null) {

			try {
				BufferedWriter writer = Files.newBufferedWriter(selected.toPath(), StandardOpenOption.CREATE,
						StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
				writer.write(text);
				writer.close();
				Utils.printLog(Level.LOG, "Saved in file : \"" + selected.getAbsolutePath() + "\"");
			} catch (IOException e) {
				Utils.printLog(Level.ERROR, "Error occured during program saving see logs file for more informations");
				Utils.writeException(e);
			}
		}

	}

	/**
	 * Open a file chooser when save load or export
	 * @param extension default extension of the searched file
	 * @return Choosed file
	 */
	public static File chooseFile(String extension) {
		FileChooser dialog = new FileChooser();
		File fileText = Stream.of(new File("./text")).peek(File::mkdirs).findFirst().get();
		File fileAlgo = Stream.of(new File("./algos")).peek(File::mkdirs).findFirst().get();
		dialog.setInitialDirectory(extension.equals("algy") ? fileAlgo : fileText);
		dialog.setTitle(extension.equals("algy") ? msg("Bridge.save") : msg("Bridge.txt"));
		dialog.setInitialFileName("unknown." + extension);
		dialog.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension, "*." + extension));

		return dialog.showSaveDialog(null);
	}

}

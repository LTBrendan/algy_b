package view.main;

import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.IOException;
import java.net.URISyntaxException;

import model.Utils;
import model.Utils.Level;
public class Main {
	
    public static void main (String [] args) throws IOException, InterruptedException, URISyntaxException {
        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless() && String.valueOf(true).equals(Utils.PROPS.getProperty(Level.LOG.name()))){
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5) + "ALGY.exe";
            Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
        }else{
            App.main(new String[0]);
            Utils.printLog(Level.INFO, "Program has ended, please type 'exit' to close the console");
        }
    }
}
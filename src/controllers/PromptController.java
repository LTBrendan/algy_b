package controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.web.PromptData;
import javafx.util.Callback;
import model.Program;
import view.component.Popup;
import view.component.PopupTable;

/**
 * Used to handle prompt request from javascript
 * @author Daphnis
 */
public class PromptController implements Callback<PromptData, String> {

	/**
	 * Program to process when the execution is launched
	 */
	private Program program;

	/**
	 * Pattern to match input requests
	 */
	private Pattern create;

	/**
	 * Constructor of the PromptController
	 * @param program The initialized program
	 */
	public PromptController(Program program) {
		super();
		this.program = program;
		this.create = Pattern.compile("CA\\.(\\w+)_array\\s*(\\w*)");
	}

	/**
	 * Called when javascript use the prompt method
	 * @param data Data passed by the javascript
	 */
	@Override
	public String call(PromptData data) {
		String res = null;
		// si il y a un . alors on affiche la popup pour les tableaux, sinon la popup
		// normale
		Matcher matcher = create.matcher(data.getMessage());
		if (matcher.matches()) {
			Object[] response = null;
			if(matcher.groupCount() >1)
				response = PopupTable.showPopupTable(matcher.group(1), matcher.group(2));
			else
				response = PopupTable.showPopupTable(matcher.group(1));
			if (response != null) {
				res = (String) response[0];
				Integer[] array = (Integer[]) response[2];
				program.putTempArray(res, array);
				program.putTempConstant(res + ".length", array.length);
			}

		} else {
			res = Popup.showInputPopup(data.getDefaultValue(), data.getMessage());
		}

		return res;
	}

}
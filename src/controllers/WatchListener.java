package controllers;

import java.util.Arrays;

import model.Utils;
import model.Utils.Level;
import model.vars.CharArray;
import model.vars.CharVar;
import model.vars.ConstantVar;
import model.vars.Index;
import model.vars.IntArray;
import model.vars.IntVar;
import model.vars.listeners.VarListener;
import view.main.WatchPanel;
import virtualTouchMachine.Array;
import virtualTouchMachine.Variable;

/**
 * Implement a listener to watch variables state in a window
 * @author dorian This class is mainly used in WatchPanel
 */
public class WatchListener implements VarListener {

	/**
	 * Panel to update when a variable change
	 */
	private WatchPanel watchpanel;

	/**
	 * Constructor of the class
	 * @param panel Panel to link with the listener
	 */
	public WatchListener(WatchPanel panel) {
		this.watchpanel = panel;
	}

	@Override
	public void onCreate(Variable var) {
		String value = "";
		String type = "";
		String associated = "";
		String associatedStr = "";
		if (var instanceof CharVar) {
			value = (char) var.getValue() + "";
			type = "CHARACTER";
		} else if (var instanceof IntVar) {
			value = var.getValue() + "";
			if (var instanceof Index) {
				type = "INDEX";
				associated = " (" + ((Index) var).getAssociatedArray().getName() + ")";
				associatedStr = " | Link :" + associated;
			} else if (var instanceof ConstantVar)
				type = "CONSTANT";
			else
				type = "INTEGER";
		}
		Utils.printLog(Level.LOG, "Creation : \"" + var.getName() + "\" = " + var.getValue() + associatedStr + " | Type : " + type);

		watchpanel.addRow(var.getName(), var.getName() + associated, type, value);

	}

	@Override
	public void onCreate(Array array) {
		String type = "INTEGER";
		Integer[] first = ((IntArray) array).getValues();
		Object[] vals = new Object[first.length];
		if (array instanceof CharArray) {
			type = "CHARACTER";
			for (int i = 0; i < first.length; i++) {
				vals[i] = (char) first[i].intValue();
			}
		} else {
			for (int i = 0; i < first.length; i++) {
				vals[i] = first[i].intValue();
			}
		}

		Utils.printLog(Level.LOG, "Creation : \"" + array.getName() + "\" = " + Arrays.toString(vals) + " | Type : " + type);

		String[] toput = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			toput[i] = vals[i] + "";
		}
		watchpanel.addRow2(array.getName(), type, toput);
	}

	@Override
	public void onDelete(Variable var) {
		Utils.printLog(Level.LOG, "Suppression : \"" + var.getName() + "\"");
		watchpanel.removeRow(var.getName());
	}

	@Override
	public void onDelete(Array array) {
		Utils.printLog(Level.LOG, "Suppression : \"" + array.getName() + "\"");
		watchpanel.removeRow2(array.getName());
	}

	@Override
	public void onUpdate(Variable var, String old, String cur) {
		Utils.printLog(Level.RUN, "Update : \"" + var.getName() + "\" : " + old + " => " + cur);
		watchpanel.updateRow(var.getName(), cur);
	}

	@Override
	public void onUpdate(Array array, int index, String old, String cur) {
		Utils.printLog(Level.RUN, "Update : " + array.getName() + "[" + index + "] : " + old + " => " + cur);
		watchpanel.updateRow2(array.getName(), index, cur);
	}

	@Override
	public void onRename(Variable arr, String old, String cur) {
		Utils.printLog(Level.LOG, "Rename : \"" + old + "\" => \"" + cur + "\"");
		watchpanel.updateRowName(old, cur);

	}

	@Override
	public void onRename(Array arr, String old, String cur) {
		Utils.printLog(Level.LOG, "Rename : \"" + old + "\" => \"" + cur + "\"");
		watchpanel.updateRowName2(old, cur);
	}
}

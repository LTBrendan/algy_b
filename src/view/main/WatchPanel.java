package view.main;

import static model.Utils.msg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * This panel display the variables created in the blockly workspace
 * 
 * @author dorian
 */
public class WatchPanel extends StackPane {

	/**
	 * Main Panel used to arrange components
	 */
	private SplitPane main;

	/**
	 * Table witch display Variables state
	 */
	private TableView<VarCell> tableVar;

	/**
	 * Table witch display Arrays state
	 */
	private TableView<Cell> tableArray;

	/**
	 * Content of tableVar
	 */
	private final ObservableList<VarCell> data;

	/**
	 * Display arrays or reduced
	 */
	private final ObservableList<Cell> arraysDisplay;

	/**
	 * Column with the title of the table var
	 */
	private TableColumn<VarCell, String> varCol;

	/**
	 * Column with the title of the table array
	 */
	private TableColumn<Cell, String> arrayCol;

	/**
	 * Column with the name of Variables
	 */
	private TableColumn<VarCell, String> nameCol1;

	/**
	 * Column with the initialization value of Variables
	 */
	private TableColumn<VarCell, String> typeCol;

	/**
	 * Column with the current value of Variables
	 */
	private TableColumn<VarCell, String> valCol1;

	/**
	 * Column with the name of Arrays
	 */
	private TableColumn<Cell, String> nameCol2;

	/**
	 * Column with the index of the Array
	 */
	private TableColumn<Cell, String> indCol;

	/**
	 * Column with the current values of the Array
	 */
	private TableColumn<Cell, String> valCol2;

	/**
	 * Constructor of the panel witch construct the UI
	 */
	public WatchPanel() {
		// call sub-methods
		initComponents();
		addComponents();
		// initialize table contents
		data = tableVar.getItems();
		arraysDisplay = tableArray.getItems();

		// set default message of the empty tables
		tableVar.setPlaceholder(new Label(msg("WatchPanel.empty")));
		tableArray.setPlaceholder(new Label(msg("WatchPanel.empty")));

		// Define a listener for click on an array to display all values or shrink
		tableArray.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				// Get the clicked Array in the table
				Cell comp = tableArray.getSelectionModel().getSelectedItem();
				if (comp != null && comp instanceof ExpandableCell) {
					((ExpandableCell) comp).toggle();
				}

				tableArray.refresh();
			}
		});
	}

	/**
	 * Initialize UI components
	 */

	private void initComponents() {
		main = new SplitPane();

		// Create tableVar
		tableVar = new TableView<VarCell>();
		varCol = new TableColumn<VarCell, String>(msg("WatchPanel.sep.var"));
		nameCol1 = new TableColumn<VarCell, String>(msg("WatchPanel.table.var.name"));
		typeCol = new TableColumn<VarCell, String>(msg("WatchPanel.table.var.start"));
		valCol1 = new TableColumn<VarCell, String>(msg("WatchPanel.table.var.end"));
		varCol.getColumns().addAll(Arrays.asList(nameCol1, typeCol, valCol1));
		tableVar.getColumns().add(varCol);
		tableVar.setEditable(false);
		tableVar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Create tableArray
		tableArray = new TableView<Cell>();
		arrayCol = new TableColumn<Cell, String>(msg("WatchPanel.sep.tab"));
		nameCol2 = new TableColumn<Cell, String>(msg("WatchPanel.table.var.name"));
		nameCol2.setSortable(false);
		indCol = new TableColumn<Cell, String>(msg("WatchPanel.table.tab.start"));
		indCol.setSortable(false);
		valCol2 = new TableColumn<Cell, String>(msg("WatchPanel.table.tab.end"));
		valCol2.setSortable(false);
		arrayCol.getColumns().addAll(Arrays.asList(nameCol2, indCol, valCol2));
		tableArray.getColumns().add(arrayCol);
		tableArray.setEditable(false);
		tableArray.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Define how each column will be fill according to the objects TableComponent
		// attributes
		nameCol1.setCellValueFactory(p -> p.getValue().getName());
		nameCol2.setCellValueFactory(p -> p.getValue().getName());
		typeCol.setCellValueFactory(p -> p.getValue().getMiddle());
		valCol1.setCellValueFactory(p -> p.getValue().getValue());
		indCol.setCellValueFactory(p -> p.getValue().getMiddle());
		valCol2.setCellValueFactory(p -> p.getValue().getValue());

		// Align center columns
		nameCol1.setStyle("-fx-alignment: CENTER;");
		nameCol2.setStyle("-fx-alignment: CENTER;");
		valCol1.setStyle("-fx-alignment: CENTER;");
		valCol2.setStyle("-fx-alignment: CENTER;");
		indCol.setStyle("-fx-alignment: CENTER;");
		typeCol.setStyle("-fx-alignment: CENTER;");
	}

	private interface Cell {
		StringProperty getName();

		StringProperty getMiddle();

		StringProperty getValue();
	};

	/**
	 * Object display in tables his attributes are used in different columns of
	 * tables
	 * 
	 * @author dorian
	 *
	 */
	private static class VarCell implements Cell {

		private String name;

		/**
		 * Name of the variable
		 */
		private StringProperty displayed;

		/**
		 * type of the variable or index if an Array
		 */
		private StringProperty type;

		/**
		 * Current Value of the variable
		 */
		private StringProperty value;

		/**
		 * Constructor of the class
		 * 
		 * @param name Name of the variable
		 * @param displayed the displayed name
		 * @param type type of the variable
		 * @param value Current Value of a variable
		 */
		private VarCell(String name, String displayed, String type, String value) {
			this.name = name;
			this.displayed = new SimpleStringProperty(displayed);
			this.type = new SimpleStringProperty(type);
			this.value = new SimpleStringProperty(value);
		}

		/**
		 * Setter of the name
		 * 
		 * @param fname new Name
		 */
		public void setName(String fname) {
			displayed.set(fname);
			name = fname;
		}

		public String getRealName() {
			return name;
		}

		/**
		 * Getter of the name
		 * 
		 * @return name of the component
		 */
		public StringProperty getName() {
			return displayed;
		}

		/**
		 * Setter of the index
		 * 
		 * @param type
		 *            new type
		 */
		public void setType(String type) {
			this.type.set(type);
		}

		/**
		 * Getter of the index
		 * 
		 * @return index of the component
		 */
		public StringProperty getMiddle() {
			return type;
		}

		/**
		 * Setter of of the value
		 * 
		 * @param fvalue new value
		 */
		public void setValue(String fvalue) {
			value.set(fvalue);
		}

		/**
		 * Getter of value
		 * 
		 * @return value of the component
		 */
		public StringProperty getValue() {
			return value;
		}

	}

	private class ExpandableCell implements Cell {

		private Set<Integer> updatedIndices;

		private String type;

		private String[] values;

		private StringProperty name;

		private StringProperty middle;

		private SimpleStringProperty value;

		private List<ArrayCell> cells;

		private boolean expanded;

		public ExpandableCell(String name, String type, String[] array) {
			this.type = type;
			this.updatedIndices = new LinkedHashSet<>();
			this.name = new SimpleStringProperty(name);
			this.value = new SimpleStringProperty("[...]");
			this.middle = new SimpleStringProperty(type);
			this.cells = new ArrayList<>(array.length);
			this.values = array;
			for (int i = 0; i < array.length; i++)
				cells.add(new ArrayCell(String.valueOf(i), array[i]));
		}

		public void toggle() {
			if (expanded) {
				value.set("[...]");
				middle.set(type);
				int start = arraysDisplay.indexOf(this) + 1;
				for (int i = start; i < start + cells.size(); i++)
					arraysDisplay.remove(start);
			} else {
				updatedIndices.forEach(i -> cells.get(i).setValue(values[i]));
				updatedIndices.clear();
				value.set("[...]");
				middle.set(type);
				arraysDisplay.addAll(arraysDisplay.indexOf(this) + 1, cells);
			}
			expanded = !expanded;
		}

		public void redefine(String[] vals) {
			updatedIndices.clear();
			int minlength = Math.min(vals.length, values.length);
			for (int i = 0; i < minlength; i++)
				update(i, vals[i]);
			if (values.length < vals.length) {
				int currentIndex = arraysDisplay.indexOf(this) + 1;
				for (int i = values.length; i < vals.length; i++) {
					ArrayCell cell = new ArrayCell(String.valueOf(i), vals[i]);
					cells.add(cell);
					if (expanded)
						arraysDisplay.add(currentIndex + i, cell);

				}
			} else {
				arraysDisplay.removeAll(cells);
				Iterator<ArrayCell> it = cells.listIterator(vals.length);
				while (it.hasNext()) {
					it.next();
					it.remove();
				}
				if (expanded)
					arraysDisplay.addAll(arraysDisplay.indexOf(this) + 1, cells);

			}
			this.values = vals;
		}

		public void update(int i, String value) {
			values[i] = value;
			if (expanded)
				cells.get(i).setValue(value);
			else
				this.updatedIndices.add(i);
		}

		@Override
		public StringProperty getName() {
			return name;
		}

		@Override
		public SimpleStringProperty getValue() {
			return value;
		}

		@Override
		public StringProperty getMiddle() {
			return middle;
		}

	}

	private class ArrayCell implements Cell {

		private SimpleStringProperty name;

		private SimpleStringProperty index;

		private SimpleStringProperty value;

		public ArrayCell(String index, String value) {
			super();
			this.name = new SimpleStringProperty();
			this.index = new SimpleStringProperty(index);
			this.value = new SimpleStringProperty(value);
		}

		@Override
		public StringProperty getName() {
			return name;
		}

		@Override
		public StringProperty getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value.set(value);
		}

		@Override
		public StringProperty getMiddle() {
			return index;
		}

	}

	/**
	 * Add components to the GridPane
	 */
	private void addComponents() {
		main.setOrientation(Orientation.VERTICAL);
		// ProgressBar bar = new ProgressBar();
		main.getItems().addAll(tableArray, tableVar/* , bar */);
		this.getChildren().add(main);

		// bar.setStyle("-fx-accent: green;");
	}

	/**
	 * Add a new Variable to tableVar
	 * 
	 * @param name Name of the variable
	 * @param displayed The displayed name
	 * @param type Type of the variable
	 * @param value Initialization value of the variable
	 */
	public void addRow(String name, String displayed, String type, String value) {
		VarCell cell = null;
		int i = 0;
		while (i < data.size() && cell == null) {
			String nm = data.get(i).getRealName();
			if ((nm != null) && (nm.equals(name)))
				cell = data.get(i);
			i++;
		}

		if (cell != null) {
			cell.setType(type);
			cell.setValue(value);
		} else {
			data.add(new VarCell(name, displayed, type, value));
		}
		tableVar.refresh();

	}

	/**
	 * Remove a Variable form tableVar
	 * 
	 * @param name Name of the Variable to delete
	 */
	public void removeRow(String name) {

		VarCell cell = null;
		int i = 0;
		while (i < data.size() && cell == null) {
			String nm = data.get(i).getRealName();
			if ((nm != null) && (nm.equals(name)))
				cell = data.remove(i);
			i++;
		}

		tableVar.refresh();
	}

	/**
	 * Update the final value / current value of a Variable
	 * 
	 * @param name Name of the updated Variable
	 * @param newVal New value of the Variable
	 */
	public void updateRow(String name, String newVal) {
		VarCell cell = null;
		int i = 0;
		while (i < data.size() && cell == null) {
			String nm = data.get(i).getRealName();
			if ((nm != null) && (nm.equals(name)))
				cell = data.get(i);
			i++;
		}
		if (cell != null)
			cell.setValue(newVal);

		tableVar.refresh();
	}

	/**
	 * Update the name of a Variable
	 * 
	 * @param old Old name of the variable
	 * @param cur Current name of the Variable
	 */
	public void updateRowName(String old, String cur) {

		VarCell cell = null;
		int i = 0;
		while (i < data.size() && cell == null) {
			String nm = data.get(i).getRealName();
			if ((nm != null) && (nm.equals(old)))
				cell = data.get(i);
			i++;
		}
		if (cell != null)
			cell.setName(cur);

		tableVar.refresh();
	}

	/**
	 * Add a new Array to tableArray or replace the existing one if same name
	 * 
	 * @param name Name of the new Array
	 * @param vals Values of this Array
	 * @param type Type of the variable
	 */
	public void addRow2(String name, String type, String[] vals) {
		ExpandableCell newTable = null;
		int i = 0;
		while (i < arraysDisplay.size() && newTable == null) {
			String nm = arraysDisplay.get(i).getName().get();
			if ((nm != null) && (nm.equals(name)))
				newTable = (ExpandableCell) arraysDisplay.get(i);
			i++;
		}

		if (newTable != null) {
			newTable.redefine(vals);
		} else {
			newTable = new ExpandableCell(name, type, vals);
			arraysDisplay.add(newTable);
		}
	}

	/**
	 * Remove an array from tableArray
	 * 
	 * @param name Name of the Array to remove
	 */
	public void removeRow2(String name) {
		ExpandableCell cell = null;
		int i = 0;
		while (i < arraysDisplay.size() && cell == null) {
			String nm = arraysDisplay.get(i).getName().get();
			if ((nm != null) && (nm.equals(name)))
				cell = (ExpandableCell) arraysDisplay.get(i);
			i++;
		}
		if (i > 0) {
			arraysDisplay.remove(i - 1);
			arraysDisplay.removeAll(cell.cells);
		}
	}

	/**
	 * Update a value from an Array of tableArray
	 * 
	 * @param name Name of the updated Array
	 * @param index Index updated
	 * @param newVal New value of the indexed value
	 */
	public void updateRow2(String name, int index, String newVal) {

		ExpandableCell cell = null;
		int i = 0;
		while (i < arraysDisplay.size() && cell == null) {
			String nm = arraysDisplay.get(i).getName().get();
			if ((nm != null) && (nm.equals(name)))
				cell = (ExpandableCell) arraysDisplay.get(i);
			i++;
		}
		if (cell != null)
			cell.update(index, newVal);
	}

	/**
	 * Rename an Array of tableArray
	 * 
	 * @param old Old name of the Array
	 * @param cur Current name of the Array
	 */
	public void updateRowName2(String old, String cur) {
		Cell cell = null;
		int i = 0;
		while (i < arraysDisplay.size() && cell == null) {
			String nm = arraysDisplay.get(i).getName().get();
			if ((nm != null) && (nm.equals(old)))
				cell = arraysDisplay.get(i);
			i++;
		}
		if (cell != null)
			cell.getName().set(cur);
	}

	/**
	 * Refresh the language used in the panel
	 */
	public void refresh() {
		varCol.setText(msg("WatchPanel.sep.var"));
		arrayCol.setText(msg("WatchPanel.sep.tab"));
		tableVar.setPlaceholder(new Label(msg("WatchPanel.empty")));
		tableArray.setPlaceholder(new Label(msg("WatchPanel.empty")));
		nameCol1.setText(msg("WatchPanel.table.var.name"));
		nameCol2.setText(msg("WatchPanel.table.var.name"));
		typeCol.setText(msg("WatchPanel.table.var.start"));
		valCol1.setText(msg("WatchPanel.table.var.end"));
		indCol.setText(msg("WatchPanel.table.tab.start"));
		valCol2.setText(msg("WatchPanel.table.tab.end"));
		tableArray.refresh();
		tableVar.refresh();
	}
}

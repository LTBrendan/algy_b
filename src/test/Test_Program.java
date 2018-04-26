package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Program;
import virtualTouchMachine.Array;

/**
 * A test to try our program
 * 
 * @author Brendan
 */
public class Test_Program {

	/**
	 * Program to be test
	 */
	private Program prog;

	/**
	 * Set up the program instructions and run it
	 * 
	 * @throws Exception
	 *             Launched during execution
	 */
	@Before
	public void setUp() throws Exception {
		prog = new Program();
		Integer[] tab = new Integer[20];
		IntStream.range(0, 20).forEach(v -> tab[v] = 0);
		prog.putTempArray("tab", tab);
		prog.putTempConstant("tab.length", 20);
		prog.executeLine("create_index i tab");
		prog.executeLine("create_index j tab");
		prog.executeLine("create_index f tab");
		prog.executeLine("create_integer_array tab");
		prog.analyse("macro init\n" + "setLoop\n" + "setFrom\n" + "pushValue 0\n" + "assign indice\n"
				+ "pushVar tab.length\n" + "pushValue 2\n" + "pushTwoOp DIV\n" + "assign prout\n"
				+ "setUntilInsertion\n" + "pushVar indice\n" + "pushVar tab.length\n" + "pushTwoOp EQUAL\n"
				+ "pushBranchTerminate\n" + "setCore\n" + "pushVar indice\n" + "pushVar prout\n" + "pushTwoOp MOD\n"
				+ "pushVar indice\n" + "indexedAssign tab\n" + "pushVar indice\n" + "pushValue 1\n" + "pushTwoOp ADD\n"
				+ "assign indice\n" + "macro sup\n" + "setLoop\n" + "setFrom\n" + "pushVar i\n" + "pushValue 1\n"
				+ "pushTwoOp ADD\n" + "assign j\n" + "setUntilInsertion\n" + "pushVar j\n" + "pushVar f\n"
				+ "pushTwoOp GREATER\n" + "pushBranchTerminate\n" + "setCore\n" + "createContinue\n" + "pushVar i\n"
				+ "pushIndexedVar tab\n" + "pushVar j\n" + "pushIndexedVar tab\n" + "pushTwoOp DIFFERENT\n"
				+ "pushBranch TOP_STACK_FALSE\n" + "pushVar j\n" + "pushValue 1\n" + "pushTwoOp ADD\n" + "assign j\n"
				+ "createContinue\n" + "pushBranch ALWAYS\n" + "pushLastContinue\n" + "pushVar f\n"
				+ "pushIndexedVar tab\n" + "pushVar j\n" + "indexedAssign tab\n" + "pushVar f\n" + "pushValue 1\n"
				+ "pushTwoOp SUB\n" + "assign f\n" + "pushContinue\n" + "macro main\n" + "setLoop\n" + "setFrom\n"
				+ "pushValue 0\n" + "assign i\n" + "pushVar tab.length\n" + "pushValue 1\n" + "pushTwoOp SUB\n"
				+ "assign f\n" + "setUntilInsertion\n" + "pushVar i\n" + "pushVar f\n" + "pushTwoOp GREATER\n"
				+ "pushBranchTerminate\n" + "setCore\n" + "callMacro sup\n" + "pushVar i\n" + "pushValue 1\n"
				+ "pushTwoOp ADD\n" + "assign i").run("init");
		prog.run();
	}

	/**
	 * Clear the program
	 * 
	 * @throws Exception
	 *             an Exception
	 */
	@After
	public void tearDown() throws Exception {
		prog = null;
	}

	/**
	 * Test variable initialisation
	 */
	@Test
	public void var_initialization() {
		assertNotNull(prog.getVariable("i"));
	}

	/**
	 * Test table length initialisation
	 */
	@Test
	public void tab_length_initialization() {
		assertNotNull(prog.getVariable("tab.length"));
	}

	/**
	 * Test table length value
	 */
	@Test
	public void var_tab_length() {
		assertEquals(prog.getVariable("tab.length").getValue(), 20);
	}

	/**
	 * Test var int
	 */
	@Test
	public void var_number() {
		assertEquals(prog.getAllVariables().size(), 4);
	}

	/**
	 * Test arrays number
	 */
	@Test
	public void arrays_number() {
		assertEquals(prog.getAllArrays().size(), 1);
	}

	/**
	 * Test program to be successful
	 */
	@Test
	public void prog_success() {
		Array tab = prog.getArray("tab");
		for (int i = 0; i < prog.getVariable("f").getValue() - 1; i++) {
			for (int j = i + 1; j < prog.getVariable("f").getValue(); j++) {
				assertNotEquals(tab.getValue(i), tab.getValue(j));
			}
		}
	}

	/**
	 * Test variable rename
	 */
	@Test
	public void new_var_rename() {
		prog.executeLine("rename j test");
		assertNotNull(prog.getVariable("test"));
	}

	/**
	 * Test variable rename
	 */
	@Test
	public void old_var_rename() {
		prog.executeLine("rename j test");
		assertNull(prog.getVariable("j"));
	}

	/**
	 * Test variable removed
	 */
	@Test
	public void delete_var() {
		prog.executeLine("delete test");
		assertNull(prog.getVariable("test"));
	}

}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;

import javax.swing.JFileChooser;

/**
*
* This is parser class for parser files
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

/** The parser and interpreter.
    The top level parse function, a main method for testing, and several
    utility methods are provided.
    You need to implement parseProgram and all the rest of the parser.
    */

public class Parser {

	/**
     * Top level parse method, called by the World
     */
    static RobotProgramNode parseFile(File code){
	Scanner scan = null;
	try {
	    scan = new Scanner(code);

	    // the only time tokens can be next to each other is
	    // when one of them is one of (){},;
	    scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

	    RobotProgramNode n = parseProgram(scan);  // You need to implement this!!!

	    scan.close();
	    return n;
	} catch (FileNotFoundException e) {
	    System.out.println("Robot program source file not found");
	} catch (ParserFailureException e) {
	    System.out.println("Parser error:");
	    System.out.println(e.getMessage());
	    scan.close();
	}
	return null;
    }

    /** For testing the parser without requiring the world */

    public static void main(String[] args){
	if (args.length>0){
	    for (String arg : args){
		File f = new File(arg);
		if (f.exists()){
		    System.out.println("Parsing '"+ f+"'");
		    RobotProgramNode prog = parseFile(f);
		    System.out.println("Parsing completed ");
		    if (prog!=null){
			System.out.println("================\nProgram:");
			System.out.println(prog);}
		    System.out.println("=================");
		}
		else {System.out.println("Can't find file '"+f+"'");}
	    }
	} else {
	    while (true){
		JFileChooser chooser = new JFileChooser(".");//System.getProperty("user.dir"));
		int res = chooser.showOpenDialog(null);
		if(res != JFileChooser.APPROVE_OPTION){ break;}
		RobotProgramNode prog = parseFile(chooser.getSelectedFile());
		System.out.println("Parsing completed");
		if (prog!=null){
		    System.out.println("Program: \n"+prog);
		}
		System.out.println("=================");
	    }
	}
	System.out.println("Done");
    }

    // Useful Patterns

    private static Pattern NUMPAT = Pattern.compile("-?\\d+");  //("-?(0|[1-9][0-9]*)");
    private static Pattern OPENPAREN = Pattern.compile("\\(");
    private static Pattern CLOSEPAREN = Pattern.compile("\\)");
    private static Pattern OPENBRACE = Pattern.compile("\\{");
    private static Pattern CLOSEBRACE = Pattern.compile("\\}");

    interface Node{
    	public void excute();
    }

    /**    PROG  ::= STMT+
     */
    static RobotProgramNode parseProgram(Scanner s){
    	//THE PARSER GOES HERE!
    	PROGNode prog = new PROGNode();

    	while(s.hasNext()){
    		prog.getSTMT().add(parseSTMT(s));
    	}

    	return prog;     // just so it will compile!!
    }


    /**		STMT  ::= ACT ; | LOOP | IF | WHILE | ASSGN ;
     */
    static STMTNode parseSTMT(Scanner s){
    	if(s.hasNext("loop")){
    		return new STMTNode(parseLOOP(s));
    	}

    	else if(s.hasNext("if")){
    		return new STMTNode(parseIF(s));
    	}

    	else if(s.hasNext("while")){
    		return new STMTNode(parseWHILE(s));
    	}

    	//else if(s.hasNext("assgn")){
    	//	return new STMTNode(parseASSGN(s));
    	//}

    	else{
    		STMTNode stmt = new STMTNode(parseACT(s));

    		if(gobble(";", s)){
    			return stmt;
    		}
    		else{
    			fail("need ;", s);
    		}
    	}
    	return null; // just so it will compile!!
    }

    //action node
    /**		ACT   ::= move [( EXP )] | turnL | turnR | turnAround |
     *     				shieldOn | shieldOff | takeFuel | wait [( EXP )]
     */
    static ACTNode parseACT(Scanner s){
    	if(s.hasNext("move")){
    		return new ACTNode(parseMove(s));
    	}

    	else if(s.hasNext("turnL")){
    		return new ACTNode(parseTurnL(s));
    	}

    	else if(s.hasNext("turnR")){
    		return new ACTNode(parseTurnR(s));
    	}

    	else if(s.hasNext("turnAround")){
    		return new ACTNode(parseTurnAround(s));
    	}

    	else if(s.hasNext("shieldOn")){
    		return new ACTNode(parseShieldOn(s));
    	}

    	else if(s.hasNext("shieldOff")){
    		return new ACTNode(parseShieldOff(s));
    	}

    	else if(s.hasNext("takeFuel")){
    		return new ACTNode(parseTakeFuel(s));
    	}

    	else if(s.hasNext("wait")){
    		return new ACTNode(parseWait(s));
    	}

    	else{
    		fail("expecting token", s);
    	}

    	return null;
    }

    //move node
    static MoveNode parseMove(Scanner s){
    	String next = s.next();
		MoveNode tmp1 = null;
		MoveNode tmp2 = null;

		if(next.equals("move")){
			tmp1 = new MoveNode();
		}
		else{
			fail("need move", s);
		}

		if(s.hasNext(OPENPAREN)){
			if(!gobble(OPENPAREN, s)){
				fail("expecting OPENPAREN for MOVE", s);
			}

			tmp2 = new MoveNode(parseEXP(s));
			if(!gobble(CLOSEPAREN, s)){
				fail("expecting CLOSEPAREN for MOVE", s);
			}
			return tmp2;
		}
		return tmp1;
    }

    //turnL node
    static TurnLNode parseTurnL(Scanner s){
    	String next = s.next();

    	if(next.equals("turnL")){
    		return new TurnLNode();
    	}

    	else{
    		fail("need turnL", s);
    	}

    	return null;
    }

    //turnR node
    static TurnRNode parseTurnR(Scanner s){
    	String next = s.next();

    	if(next.equals("turnR")){
    		return new TurnRNode();
    	}

    	else{
    		fail("need turnR", s);
    	}

    	return null;
    }

    //takefuel node
    static TakeFuelNode parseTakeFuel(Scanner s){
    	String next = s.next();

    	if(next.equals("takeFuel")){
    		return new TakeFuelNode();
    	}

    	else{
    		fail("need takeFuel", s);
    	}

    	return null;
    }

    //turn around node
    static TurnAroundNode parseTurnAround(Scanner s){
    	String next = s.next();

		if(next.equals("turnAround")){
			return new TurnAroundNode();
		}

		else{
			fail("need turnAround", s);
		}

		return null;
    }

    //shield on node
    static ShieldOnNode parseShieldOn(Scanner s){
    	String next = s.next();

		if(next.equals("shieldOn")){
			return new ShieldOnNode();
		}

		else{
			fail("need shieldOn", s);
		}

		return null;
    }

    //shield off node
    static ShieldOffNode parseShieldOff(Scanner s){
    	String next = s.next();

		if(next.equals("shieldOff")){
			return new ShieldOffNode();
		}

		else{
			fail("need shieldOff", s);
		}

		return null;
    }

    //wait node
    static WaitNode parseWait(Scanner s){
    	String next = s.next();
		WaitNode tmp1 = null;
		WaitNode tmp2 = null;

		if(next.equals("wait")){
			tmp1 = new WaitNode();
		}
		else{
			fail("need wait", s);
		}

		if(s.hasNext(OPENPAREN)){
			if(!gobble(OPENPAREN, s)){
				fail("expecting OPENPAREN for MOVE", s);
			}

			tmp2 = new WaitNode(parseEXP(s));
			if(!gobble(CLOSEPAREN, s)){
				fail("expecting CLOSEPAREN for MOVE", s);
			}
			return tmp2;
		}
		return tmp1;
    }

    //loop node
    /**		LOOP  ::= loop BLOCK
     */
    static LOOPNode parseLOOP(Scanner s){
    	if(!gobble("loop", s)){
    		fail("expecting loop", s);
    	}

    	return new LOOPNode(parseBLOCK(s));
    }

    //if node
    /**		IF    ::= if ( COND ) BLOCK [elif ( COND ) BLOCK]*[else BLOCK]
     */
    static IFNode parseIF(Scanner s){
    	if(!gobble("if", s)){
			fail("expecting if", s);
    	}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for if", s);
		}

		CONDNode cond = parseCOND(s);

		if(!gobble(CLOSEPAREN, s)){
			System.out.println(CLOSEPAREN);
			fail("expecting CLOSEPAREN for if", s);
		}

		BLOCKNode block = parseBLOCK(s);
		if(s.hasNext("else")){
			s.next();
			return new IFNode(cond, block, parseBLOCK(s));
		}
		return new IFNode(cond, block);
	}


    //while node
    /**		WHILE ::= while ( COND ) BLOCK
     */
    static WHILENode parseWHILE(Scanner s){
		if(!gobble("while", s)){
			fail("expecting while", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for while", s);
		}

		CONDNode condNode = parseCOND(s);

		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEPAREN for while", s);
		}

		BLOCKNode block = parseBLOCK(s);
		return new WHILENode(condNode, block);
	}

    //assgn node
    /** 	ASSGN ::= VAR = EXP
     */
    //static ASSGNNode parseASSGN(Scanner s){
    //	if(!gobble("assgn", s)){
    //		fail("expecting assgn", s);
    //	}
    //
    //	return new ASSGNNode(parseVAR(s));
    //}

    //block node
    /**		BLOCK ::= { STMT+ }
     */
    static BLOCKNode parseBLOCK(Scanner s){
    	BLOCKNode blockNode = new BLOCKNode();

    	if(!gobble(OPENBRACE, s)){
    		fail("expecting OPENBRACE", s);
    	}

    	do{
    		if(s.hasNext(CLOSEBRACE)){
    			fail("expecting STMT", s);
    		}
    		blockNode.getSTMT().add(parseSTMT(s));
    	} while (!s.hasNext(CLOSEBRACE));

    	if(!gobble(CLOSEBRACE, s)){
    		fail("expecting CLOSEBRACE", s);
    	}

    	return blockNode;
    }

    //expression node
    /**		EXP   ::= NUM | SEN | VAR | OP ( EXP, EXP )
     */
    static EXPNode parseEXP(Scanner s) {
		if (s.hasNext(NUMPAT)){
			return new EXPNode(parseNUM(s));
		}

		else if(s.hasNext("add") || s.hasNext("sub") || s.hasNext("mul") || s.hasNext("div")){
			return new EXPNode(parseOP(s));
		}

		else{
			return new EXPNode(parseSEN(s));
		}
	}

    //operation node
    /**		OP    ::= add | sub | mul | div
     */
    static OPNode parseOP(Scanner s) {
		if(s.hasNext("add")){
			return new OPNode(parseAdd(s));
		}

		else if(s.hasNext("sub")){
			return new OPNode(parseSub(s));
		}

		else if(s.hasNext("mul")){
			return new OPNode(parseMul(s));
		}

		else if(s.hasNext("div")){
			return new OPNode(parseDiv(s));
		}

		else{
			fail("expecting token for parseOP", s);
		}

		return null;
	}

    //add node
    static AddNode parseAdd(Scanner s){
		if(!gobble("add", s)){
			fail("expecting add for parseAdd", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENBRACE for parseAdd", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting , for  parseAdd", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseAdd", s);
		}

		return new AddNode(exp1, exp2);
	}

    //sub node
    static SubNode parseSub(Scanner s){
		if(!gobble("sub", s)){
			fail("expecting add for parseSub", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENBRACE for parseSub", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting , for  parseSub", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseSub", s);
		}

		return new SubNode(exp1, exp2);
	}

    //mul node
    static MulNode parseMul(Scanner s){
		if(!gobble("mul", s)){
			fail("expecting add for parseMul", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENBRACE for parseMul", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting , for  parseMul", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseMul", s);
		}

		return new MulNode(exp1, exp2);
	}

    //div node
    static DivNode parseDiv(Scanner s){
		if(!gobble("div", s)){
			fail("expecting add for parseDiv", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENBRACE for parseDiv", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting , for  parseDiv", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseDiv", s);
		}

		return new DivNode(exp1, exp2);
	}

    //condition node
    /**		COND  ::= lt ( EXP, EXP ) | gt ( EXP, EXP ) | eq ( EXP, EXP ) | and ( COND, COND ) |
	 *					 or ( COND, COND ) | not ( COND )
     */
    static CONDNode parseCOND(Scanner s){
    	if(s.hasNext("lt")){
			return new CONDNode(parseLT(s));
    	}

		else if(s.hasNext("gt")){
			return new CONDNode(parseGT(s));
		}

		else if(s.hasNext("eq")){
			return new CONDNode(parseEQ(s));
		}

		else if(s.hasNext("and")){
			return new CONDNode(parseAND(s));
		}

		else if(s.hasNext("or")){
			return new CONDNode(parseOR(s));
		}

		else if(s.hasNext("not")){
			return new CONDNode(parseNOT(s));
		}

		else{
			fail("expecting lt | gt | eq | and | or | not", s);
		}

		return null;
	}

    //less than node
	static LTNode parseLT(Scanner s){
		if(!gobble("lt", s)){
			fail("expecting lt for lt", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENBRACE for lt", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting, for parseLT", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseLT", s);
		}

		return new LTNode(exp1, exp2);
	}

	//greater than node
	static GTNode parseGT(Scanner s){
		if(!gobble("gt", s)){
			fail("expecting gt for gt", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for gt", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting, for parseGT", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseGT", s);
		}

		return new GTNode(exp1, exp2);
	}

	//equal node
	static EQNode parseEQ(Scanner s){
		if(!gobble("eq", s)){
			fail("expecting eq for eq", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for eq", s);
		}

		EXPNode exp1 = parseEXP(s);
		if(!gobble(",", s)){
			fail("expecting, for parseEQ", s);
		}

		EXPNode exp2 = parseEXP(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseEQ", s);
		}

		return new EQNode(exp1, exp2);
	}

	//and node
	static ANDNode parseAND(Scanner s){
		if(!gobble("and", s)){
			fail("expecting gt for parseAND", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for parseAND", s);
		}

		CONDNode cond1 = parseCOND(s);
		if(!gobble(",", s)){
			fail("expecting, for parseAND", s);
		}

		CONDNode cond2 = parseCOND(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseAND", s);
		}

		return new ANDNode(cond1, cond2);
	}

	//or node
	static ORNode parseOR(Scanner s){
		if(!gobble("or", s)){
			fail("expecting gt for parseOR", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for parseOR", s);
		}

		CONDNode cond1 = parseCOND(s);
		if(!gobble(",", s)){
			fail("expecting, for parseOR", s);
		}

		CONDNode cond2 = parseCOND(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseOR", s);
		}

		return new ORNode(cond1, cond2);
	}

	//not node
	static NOTNode parseNOT(Scanner s){
		if(!gobble("not", s)){
			fail("expecting gt for parseNOT", s);
		}

		if(!gobble(OPENPAREN, s)){
			fail("expecting OPENPAREN for parseNOT", s);
		}

		CONDNode cond = parseCOND(s);
		if(!gobble(CLOSEPAREN, s)){
			fail("expecting CLOSEBRACE for parseNOT", s);
		}

		return new NOTNode(cond);
	}

	//sensor node
	/** 	SEN   ::= fuelLeft | oppLR | oppFB | numBarrels | barrelLR [( EXP )] | barrelFB [ ( EXP ) ] | wallDist
	 */
	static SENNode parseSEN(Scanner s){
		if(s.hasNext("fuelLeft")){
			return new SENNode(parseFuelLeft(s));
		}

		else if(s.hasNext("oppLR")){
			return new SENNode(parseOppLR(s));
		}

		else if(s.hasNext("oppFB")){
			return new SENNode(parseOppFB(s));
		}

		else if(s.hasNext("numBarrels")){
			return new SENNode(parseNumBarrels(s));
		}

		else if(s.hasNext("barrelLR")){
			return new SENNode(parseBarrelLR(s));
		}

		else if(s.hasNext("barrelFB")){
			return new SENNode(parseBarrelFB(s));
		}

		else if(s.hasNext("wallDist")){
			return new SENNode(parseWallDist(s));
		}

		else{
			fail("expecting token for parseSEN", s);
		}

		return null;
	}

	//fuelLeft node
	static FuelLeftNode parseFuelLeft(Scanner s) {
		String next = s.next();

		if(next.equals("fuelLeft")){
			return new FuelLeftNode();
		}

		else{
			fail("need fuelLeft", s);
		}

		return null;
	}

	//opponentLR node
	static OppLRNode parseOppLR(Scanner s) {
		String next = s.next();

		if(next.equals("oppLR")){
			return new OppLRNode();
		}

		else{
			fail("need oppLR", s);
		}

		return null;
	}

	//opponentFB node
	static OppFBNode parseOppFB(Scanner s) {
		String next = s.next();

		if(next.equals("oppFB")){
			return new OppFBNode();
		}

		else{
			fail("need oppFB", s);
		}

		return null;
	}

	//numBarrels node
	static NumBarrelsNode parseNumBarrels(Scanner s) {
		String next = s.next();

		if(next.equals("numBarrels")){
			return new NumBarrelsNode();
		}

		else{
			fail("need numBarrels", s);
		}

		return null;
	}

	//barrelLR node
	static BarrelLRNode parseBarrelLR(Scanner s) {
		String next = s.next();

		if(next.equals("barrelLR")){
			return new BarrelLRNode();
		}

		else{
			fail("need barrelLR", s);
		}

		return null;
	}

	//barrelFB node
	static BarrelFBNode parseBarrelFB(Scanner s) {
		String next = s.next();

		if(next.equals("barrelFB")){
			return new BarrelFBNode();
		}

		else{
			fail("need barrelFB", s);
		}

		return null;
	}

	//wallDist node
	static WallDistNode parseWallDist(Scanner s) {
		String next = s.next();

		if(next.equals("wallDist")){
			return new WallDistNode();
		}

		else{
			fail("need wallDist", s);
		}

		return null;
	}

	//number node
	/**		NUM   ::= "-?[1-9][0-9]*|0"
	 */
	static NUMNode parseNUM(Scanner s){
		if(!s.hasNext("-?[1-9][0-9]*|0")){
			fail("expecting a number", s);
		}

		return new NUMNode(s.nextInt());
	}

	//variable node
	/**		VAR   ::= "\\$[A-Za-z][A-Za-z0-9]*"
	 */
	static VARNode parseVAR(Scanner s){
		if(!s.hasNext("\\$[A-Za-z][A-Za-z0-9]*")){
			fail("expecting a variable", s);
		}
		return new VARNode(s.nextInt());
	}

    static class PROGNode implements RobotProgramNode{
    	private List<STMTNode> stmt;

    	public PROGNode(){
    		stmt = new ArrayList<STMTNode>();
    	}

    	public List<STMTNode> getSTMT(){
			return stmt;
		}

		public void execute(Robot robot){
			for (STMTNode node : stmt){
				node.execute(robot);
			}
		}

		public String toString(){
			String prog = "PROG";

			for (STMTNode node : stmt){
				prog += node.toString();
			}
			return prog;
		}
    }

    static class STMTNode implements RobotProgramNode{
    	private ACTNode actNode;
		private LOOPNode loopNode;
		private IFNode ifNode;
		private WHILENode whileNode;
		//private ASSGNNode assgnNode;

		public STMTNode(ACTNode actNode){
			this.actNode = actNode;
		}

		public STMTNode(LOOPNode loopNode){
			this.loopNode = loopNode;
		}

		public STMTNode(IFNode ifNode){
			this.ifNode = ifNode;
		}

		public STMTNode(WHILENode whileNode){
			this.whileNode = whileNode;
		}

		//public STMTNode(ASSGNNode assgnNode){
		//	this.assgnNode = assgnNode;
		//}

		public void execute(Robot robot){
			if (actNode != null){
				actNode.execute(robot);
			}

			else if(loopNode != null){
				loopNode.execute(robot);
			}

			else if(ifNode != null){
				ifNode.execute(robot);
			}

			//else if(assgnNode != null){
			//	assgnNode.execute(robot);
			//}

			else{
				whileNode.execute(robot);
			}
		}

		public String toString(){
			if(actNode != null){
				return actNode.toString();
			}

			else if(loopNode!=null){
				return loopNode.toString();
			}

			else if(ifNode!= null){
				return ifNode.toString();
			}

			//else if(assgnNode!= null){
			//	return assgnNode.toString();
			//}

			else{
				return whileNode.toString();
			}
		}
    }

    static class EXPNode{
		private OPNode opNode;
		private NUMNode num;
		private SENNode sen;
		private VARNode var;

		public EXPNode(OPNode opNode){
			this.opNode = opNode;
		}

		public EXPNode(NUMNode numNode){
			this.num = numNode;
		}

		public EXPNode(VARNode varNode){
			this.var = varNode;
		}

		public EXPNode(SENNode sen){
			this.sen = sen;
		}

		public int evaluate(Robot robot){
			if (num != null){
				return num.evaluate(robot);
			}

			else if (sen != null){
				return sen.evaluate(robot);
			}

			else if (var != null){
				return var.evaluate(robot);
			}

			else{
				return opNode.evaluate(robot);
			}
		}

		public String toString(){
			if(opNode != null){
				return opNode.toString();
			}

			else{
				return null;
			}
		}
	}

    static class OPNode{
		private AddNode add;
		private SubNode sub;
		private MulNode mul;
		private DivNode div;

		public OPNode(AddNode add){
			this.add = add;
		}

		public OPNode(SubNode sub){
			this.sub = sub;
		}

		public OPNode(MulNode mul){
			this.mul = mul;
		}

		public OPNode(DivNode div){
			this.div = div;
		}

		@SuppressWarnings("null")
		public int evaluate(Robot robot){
			if(add != null){
				return add.evaluate(robot);
			}

			else if (sub != null){
				return sub.evaluate(robot);
			}

			else if (mul != null){
				return mul.evaluate(robot);
			}

			else if (div != null){
				return div.evaluate(robot);
			}

			else{
				return (Integer) null;
			}
		}
	}

    static class AddNode{
		private EXPNode exp1;
		private EXPNode exp2;
		private int result;

		public AddNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public int evaluate(Robot robot){
			result = exp1.evaluate(robot) + exp2.evaluate(robot);
			return result;
		}

		public String toString(){
			return "add " + result + " / ";
		}
	}

	static class SubNode{
		private EXPNode exp1;
		private EXPNode exp2;
		private int result;

		public SubNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public int evaluate(Robot robot){
			result = exp1.evaluate(robot) - exp2.evaluate(robot);
			return result;
		}

		public String toString(){
			return "add " + result + " / ";
		}
	}

	static class MulNode{
		private EXPNode exp1;
		private EXPNode exp2;
		private int result;

		public MulNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public int evaluate(Robot robot){
			result = exp1.evaluate(robot) * exp2.evaluate(robot);
			return result;
		}

		public String toString(){
			return "add " + result + " / ";
		}
	}

	static class DivNode{
		private EXPNode exp1;
		private EXPNode exp2;
		private int result;

		public DivNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public int evaluate(Robot robot){
			result = exp1.evaluate(robot) / exp2.evaluate(robot);
			return result;
		}

		public String toString() {
			return "add " + result + " / ";
		}
	}


    static class CONDNode implements CONDInterface{
    	private LTNode ltNode;
		private GTNode gtNode;
		private EQNode eqNode;
		@SuppressWarnings("unused")
		private ANDNode andNode;
		@SuppressWarnings("unused")
		private ORNode orNode;
		@SuppressWarnings("unused")
		private NOTNode notNode;
		@SuppressWarnings("unused")
		private SENNode sen;
		@SuppressWarnings("unused")
		private NUMNode num;
		@SuppressWarnings("unused")
		private VARNode var;

		public CONDNode(LTNode ltNode){
			this.ltNode = ltNode;
		}

		public CONDNode(ANDNode andNode){
			this.andNode = andNode;
		}

		public CONDNode(ORNode orNode){
			this.orNode = orNode;
		}

		public CONDNode(NOTNode notNode){
			this.notNode = notNode;
		}

		public CONDNode(GTNode gtNode){
			this.gtNode = gtNode;
		}

		public CONDNode(EQNode eqNode){
			this.eqNode = eqNode;
		}

		public CONDNode(SENNode parseSEN){
			sen = parseSEN;
		}

		public CONDNode(NUMNode parseNUM){
			num = parseNUM;
		}

		public CONDNode(VARNode parseVAR){
			var = parseVAR;
		}

		public void execute(Robot robot){}

		public Boolean evaluate(Robot robot){
			if(ltNode != null){
				return ltNode.evaluate(robot);
			}

			else if(gtNode != null){
				return gtNode.evaluate(robot);
			}

			else{
				return eqNode.evaluate(robot);
			}
		}
	}

	static class LTNode implements CONDInterface{
		private EXPNode exp1;
		private EXPNode exp2;

		public LTNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public Boolean evaluate(Robot robot){
			return (exp1.evaluate(robot) < exp2.evaluate(robot));
		}

		public String toString(){
			return "LTNode " + exp1.toString() + exp2.toString() + " / ";
		}
	}

	static class GTNode implements CONDInterface{
		private EXPNode exp1;
		private EXPNode exp2;

		public GTNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public Boolean evaluate(Robot robot){
			return (exp1.evaluate(robot) > exp2.evaluate(robot));

		}

		public String toString(){
			return "GTNode " + exp1.toString() + exp2.toString() + " / ";
		}
	}

	static class EQNode implements CONDInterface{
		private EXPNode exp1;
		private EXPNode exp2;

		public EQNode(EXPNode exp1, EXPNode exp2){
			this.exp1 = exp1;
			this.exp2 = exp2;
		}

		public Boolean evaluate(Robot robot){
			return (exp1.evaluate(robot) == exp2.evaluate(robot));

		}

		public String toString(){
			return "EQNode " + exp1.toString() + exp2.toString() + " / ";
		}
	}

	static class ANDNode implements CONDInterface{
		private CONDNode cond1;
		private CONDNode cond2;

		public ANDNode(CONDNode cond1, CONDNode cond2){
			this.cond1 = cond1;
			this.cond2 = cond2;
		}

		public Boolean evaluate(Robot robot){
			return (cond1.evaluate(robot) && cond2.evaluate(robot));
		}

		public String toString(){
			return "ANDNode " + cond1.toString() + cond2.toString() + " / ";
		}
	}

	static class ORNode implements CONDInterface{
		private CONDNode cond1;
		private CONDNode cond2;

		public ORNode(CONDNode cond1, CONDNode cond2){
			this.cond1 = cond1;
			this.cond2 = cond2;
		}

		public Boolean evaluate(Robot robot){
			return (cond1.evaluate(robot) || cond2.evaluate(robot));
		}

		public String toString(){
			return "ORNode " + cond1.toString() + cond2.toString() + " / ";
		}
	}

	static class NOTNode implements CONDInterface{
		private CONDNode cond;

		public NOTNode(CONDNode cond){
			this.cond = cond;
		}

		public Boolean evaluate(Robot robot){
			return (!cond.evaluate(robot));
		}

		public String toString() {
			return "NOTNode " + cond.toString() + " / ";
		}
	}

    static class ACTNode implements RobotProgramNode{
    	private MoveNode move;
		private TurnLNode turnL;
		private TurnRNode turnR;
		private TakeFuelNode takeFuel;
		private WaitNode wait;
		private TurnAroundNode turnAround;
		private ShieldOnNode shieldOn;
		private ShieldOffNode shieldOff;

		public ACTNode(MoveNode move){
			this.move = move;
		}

		public ACTNode(TurnLNode turnL){
			this.turnL = turnL;
		}

		public ACTNode(TurnRNode turnR){
			this.turnR = turnR;
		}

		public ACTNode(TakeFuelNode takeFuel){
			this.takeFuel = takeFuel;
		}

		public ACTNode(WaitNode wait){
			this.wait = wait;
		}

		public ACTNode(TurnAroundNode turnAround){
			this.turnAround = turnAround;
		}

		public ACTNode(ShieldOnNode shieldOn){
			this.shieldOn = shieldOn;
		}

		public ACTNode(ShieldOffNode shieldOff){
			this.shieldOff = shieldOff;
		}

		public void execute(Robot robot){
			if (move != null){
				move.execute(robot);
			}

			else if (turnL != null){
				turnL.execute(robot);
			}

			else if (turnR != null){
				turnR.execute(robot);
			}

			else if (turnAround != null){
				turnAround.execute(robot);
			}

			else if (shieldOn != null){
				shieldOn.execute(robot);
			}

			else if (shieldOff != null){
				shieldOff.execute(robot);
			}

			else if (takeFuel != null){
				takeFuel.execute(robot);
			}

			else{
				wait.execute(robot);
			}
		}

		public String toString(){
			if (move != null){
				return move.toString();
			}

			else if (turnL != null){
				return turnL.toString();
			}

			else if (turnR != null){
				return turnR.toString();
			}

			else if (turnAround != null){
				return turnAround.toString();
			}

			else if (shieldOn != null){
				return shieldOn.toString();
			}

			else if (shieldOff != null){
				return shieldOff.toString();
			}

			else if (takeFuel != null){
				return takeFuel.toString();
			}

			else if (wait != null){
				return wait.toString();
			}

			else{
				return null;
			}
		}
    }

    static class IFNode implements RobotProgramNode{
    	@SuppressWarnings("unused")
		private CONDNode cond;
		private BLOCKNode block1;
		@SuppressWarnings("unused")
		private BLOCKNode block2;

		public IFNode(CONDNode cond, BLOCKNode block){
			this.block1 = block;
			this.cond = cond;
		}

		public IFNode(CONDNode cond, BLOCKNode block, BLOCKNode block1){
			this.cond = cond;
			this.block1 = block;
			this.block2 = block1;
		}

		public void execute(Robot robot){
			block1.execute(robot);
		}

		public String toString(){
			return block1.toString();
		}
	}

	static class WHILENode implements RobotProgramNode{
		@SuppressWarnings("unused")
		private CONDNode cond;
		private BLOCKNode block;

		public WHILENode(CONDNode cond, BLOCKNode block){
			this.cond = cond;
			this.block = block;
		}

		public void execute(Robot robot){
			block.execute(robot);
		}

		public String toString(){
			return block.toString();
		}
	}

	//static class ASSGNNode implements RobotProgramNode{
	//	if (s.hasNext(VARPAT))
	//		return new VARNode(parseVAR(s));
	//	else
	//		return new EXPNode(parseEXP(s));
	//}

    static class MoveNode implements RobotProgramNode{
    	private String value = "move";
		@SuppressWarnings("unused")
		private EXPNode expNode;

		public MoveNode(){}

		public MoveNode(EXPNode expNode){
			this.expNode = expNode;
		}

		public void execute(Robot robot){
			robot.move();
		}

		public String toString(){
			return value + " / ";
		}
    }

    static class TurnLNode implements RobotProgramNode{
    	private String value = "turnL";

		public TurnLNode(){}

		public void execute(Robot robot){
			robot.turnLeft();
		}

		public String toString(){
			return value + " / ";
		}
    }

    static class TurnRNode implements RobotProgramNode{
    	private String value = "turnR";

		public TurnRNode(){}

		public void execute(Robot robot){
			robot.turnRight();
		}

		public String toString(){
			return value + " / ";
		}
    }

    static class TakeFuelNode implements RobotProgramNode{
    	private String value = "takeFule";

		public TakeFuelNode(){}

		public void execute(Robot robot){
			robot.takeFuel();
		}

		public String toString() {
			return value + " / ";
		}
    }

    static class WaitNode implements RobotProgramNode{
    	private String value = "wait";
    	@SuppressWarnings("unused")
		private EXPNode expNode;

		public WaitNode(){}

		public WaitNode(EXPNode expNode){
			this.expNode = expNode;
		}

		public void execute(Robot robot){
			try{
				robot.wait();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		public String toString(){
			return value + " / ";
		}
    }

    static class LOOPNode implements RobotProgramNode{
    	private BLOCKNode blockNode;

		public LOOPNode(BLOCKNode BLOCK){
			this.blockNode = BLOCK;
		}

		public void execute(Robot robot){}

		public String toString(){
			return blockNode.toString();
		}
    }

    static class BLOCKNode implements RobotProgramNode{
    	private List<STMTNode> stmt;

		public BLOCKNode(){
			stmt = new ArrayList<STMTNode>();
		}

		public List<STMTNode> getSTMT(){
			return stmt;
		}

		public void execute(Robot robot){
			for(STMTNode node : stmt){
				node.execute(robot);
			}
		}

		public String toString(){
			String block = "";

			for(STMTNode node : stmt){
				block += node.toString();
			}

			return block;
		}
    }

    static class TurnAroundNode implements RobotProgramNode {
		private String value = "turnAround";

		public TurnAroundNode(){}

		public void execute(Robot robot){
			robot.turnAround();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class ShieldOnNode implements RobotProgramNode {
		private String value = "shieldOn";

		public ShieldOnNode(){}

		public void execute(Robot robot){
			robot.setShield(true);
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class ShieldOffNode implements RobotProgramNode {
		private String value = "shieldOff";

		public ShieldOffNode(){}

		public void execute(Robot robot){
			robot.setShield(false);
		}

		public String toString() {
			return value + " / ";
		}
	}

	static class SENNode {
		private FuelLeftNode fuelLeft;
		private OppLRNode oppLR;
		private OppFBNode oppFB;
		private NumBarrelsNode numBarrels;
		private BarrelLRNode barrelLR;
		private BarrelFBNode barrelFB;
		private WallDistNode wallDist;

		public SENNode(FuelLeftNode fuelLeft){
			this.fuelLeft = fuelLeft;
		}

		public SENNode(OppLRNode oppLR){
			this.oppLR = oppLR;
		}

		public SENNode(OppFBNode oppFB){
			this.oppFB = oppFB;
		}

		public SENNode(NumBarrelsNode numBarrels){
			this.numBarrels = numBarrels;
		}

		public SENNode(BarrelLRNode barrelLR){
			this.barrelLR = barrelLR;
		}

		public SENNode(BarrelFBNode barrelFB){
			this.barrelFB = barrelFB;
		}

		public SENNode(WallDistNode wallDist){
			this.wallDist = wallDist;
		}

		@SuppressWarnings("null")
		public int evaluate(Robot robot){
			if (fuelLeft != null){
				return fuelLeft.evaluate(robot);
			}

			else if (oppLR != null){
				return oppLR.evaluate(robot);
			}

			else if (oppFB != null){
				return oppFB.evaluate(robot);
			}

			else if (numBarrels != null){
				return numBarrels.evaluate(robot);
			}

			else if (barrelLR != null){
				return barrelLR.evaluate(robot);
			}

			else if (barrelFB != null){
				return barrelFB.evaluate(robot);
			}

			else if (wallDist != null){
				return wallDist.evaluate(robot);
			}

			else {
				return (Integer) null;
			}
		}

		public String toString() {
			if (fuelLeft != null) {
				return fuelLeft.toString();
			}

			else if (oppLR != null) {
				return oppLR.toString();
			}

			else if (oppFB != null) {
				return oppFB.toString();
			}

			else if (numBarrels != null) {
				return numBarrels.toString();
			}

			else if (barrelLR != null) {
				return barrelLR.toString();
			}

			else if (barrelFB != null) {
				return barrelFB.toString();
			}

			else if (wallDist != null) {
				return wallDist.toString();
			}

			else{
				return null;
			}
		}
	}

	static class FuelLeftNode implements SENInterface{
		private String value = "fuelLeft";

		public FuelLeftNode(){}

		public int evaluate(Robot robot){
			return robot.getFuel();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class OppLRNode implements SENInterface{
		private String value = "oppLR";

		public OppLRNode(){}

		public int evaluate(Robot robot){
			return robot.getOpponentLR();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class OppFBNode implements SENInterface{
		private String value = "oppFB";

		public OppFBNode(){}

		public int evaluate(Robot robot){
			return robot.getOpponentFB();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class NumBarrelsNode implements SENInterface{
		private String value = "numBarrels";

		public NumBarrelsNode(){}

		public int evaluate(Robot robot){
			return robot.numBarrels();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class BarrelLRNode implements SENInterface{
		private String value = "barrelLR";

		public BarrelLRNode(){}

		public int evaluate(Robot robot){
			return robot.getClosestBarrelLR();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class BarrelFBNode implements SENInterface{
		private String value = "barrelFB";

		public BarrelFBNode(){}

		public int evaluate(Robot robot){
			return robot.getClosestBarrelFB();
		}

		public String toString() {
			return value + " / ";
		}
	}

	static class WallDistNode implements SENInterface{
		private String value = "wallDist";

		public WallDistNode(){}

		public int evaluate(Robot robot){
			return robot.getDistanceToWall();
		}

		public String toString(){
			return value + " / ";
		}
	}

	static class NUMNode implements SENInterface{
		private int value;

		public NUMNode(int value) {
			this.value = value;
		}

		public int evaluate(Robot robot) {
			return value;
		}

		public String toString() {
			return " " + value;
		}
	}

	static class VARNode implements SENInterface{
		private int value;

		public VARNode(int value) {
			this.value = value;
		}

		public int evaluate(Robot robot) {
			return value;
		}

		public String toString() {
			return " " + value;
		}
	}


	//utility methods for the parser
    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s){
    	String msg = message + "\n   @ ...";
    	for (int i=0; i<5 && s.hasNext(); i++){
    		msg += " " + s.next();
    	}
    	throw new ParserFailureException(msg+"...");
    }

    /**
       If the next token in the scanner matches the specified pattern,
       consume the token and return true. Otherwise return false without
       consuming anything.
       Useful for dealing with the syntactic elements of the language
       which do not have semantic content, and are there only to
       make the language parsable.
     */
    static boolean gobble(String p, Scanner s){
    	if (s.hasNext(p)) { s.next(); return true;}
    	else { return false; }
    }
    static boolean gobble(Pattern p, Scanner s){
    	if (s.hasNext(p)) { s.next(); return true;}
    	else { return false; }
    }
}
// You could add the node classes here, as long as they are not declared public (or private)


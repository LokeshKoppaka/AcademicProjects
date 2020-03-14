/* @Authors : Lokesh Koppaka(862123164) , Abhilash Sunkam(862122847)
 * Course : CS 201 Compiler Construction - Project 3
 * Class : Liveness
 * Description : Performs liveness analysis across all the basic block
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ArraySparseSet;

public class Liveness extends SceneTransformer{

	@Override
	protected void internalTransform(String arg0, Map arg1) {
		SootClass mainClass = Scene.v().getMainClass(); // get main class
		List < SootMethod > sootMethodsList = mainClass.getMethods();
		for (SootMethod testMethod: sootMethodsList) {
			if (!testMethod.getName().contains("test")) {
				continue;
			}
			
			System.out.println("\n**************************** METHOD NAME:" + testMethod.getName() +" ******************************");
			//HashMap blockMap -  maps basic block to its UEEXP and VARKILL
			Map<Block, List<FlowSet<String>>> blockMap = new LinkedHashMap<Block, List<FlowSet<String>>>();
			//HashMap livenessMap - maps basic block to its LIVEOUT
			Map<Block, FlowSet<String>> livenessMap = new LinkedHashMap<Block, FlowSet<String>>();
			Stack<Block> blockStack = new Stack<Block>();
			Body methodBody = testMethod.retrieveActiveBody();
			BlockGraph blockGraph = new BriefBlockGraph(methodBody);
			Iterator<Block> graphIt = blockGraph.getBlocks().iterator();
			// Logic to compute UEEXP and VARKILL for each block - Computes UEEXP and VARKILL for all the basic blocks and maintains them in blockMap 
			while (graphIt.hasNext()) {
				Block block = graphIt.next();
				blockStack.push(block);
			}
			while(!blockStack.isEmpty()) {
				Block block = blockStack.pop();
				blockMap = computeUEVARandVARKILL(block, blockMap);
				livenessMap.put(block, new ArraySparseSet<String>());
			}
			System.out.println("-------------------------------");
			System.out.println("|No of Basic Blocks = " + blockMap.size()+"|");
			System.out.println("-------------------------------");
			// Iterative Logic to compute LIVEOUT
			boolean flag = true;
			while(flag) {
				flag = false;
				// for each block get the current LIVEOUT
				for(Block currentBlock : livenessMap.keySet()) {
					FlowSet<String> LIVEOUT = new ArraySparseSet<String>();
					List<Block> successors  = currentBlock.getSuccs();
					// Logic to update LIVEOUT = For all x in successor Union(LIVEOUT(x) - VARKILL(x) + UPEXP(x))
					for(Block block : successors) {
						FlowSet<String> succBlock2 = new ArraySparseSet<String>();
						FlowSet<String> succBlock = livenessMap.get(block);
						FlowSet<String> innerList = new ArraySparseSet<String>();
						succBlock.difference(blockMap.get(block).get(0), succBlock2);
						succBlock2.union(blockMap.get(block).get(1),innerList);
						LIVEOUT.union(innerList, LIVEOUT);
					}
					FlowSet<String> unionResult = new ArraySparseSet<String>();
					LIVEOUT.union(livenessMap.get(currentBlock),unionResult);
					// Logic to update back the latest LIVEOUT if it is changed from the previous iteration
					if(unionResult.size() != livenessMap.get(currentBlock).size()){
						livenessMap.put(currentBlock, LIVEOUT);
						flag = true;
					}
				}
			}
			// Logic to print out basic block along with its VARKILL, UEVAR, LIVE OUT
			for(Block b: livenessMap.keySet()) {
				System.out.println(b);
				System.out.println("VARKILL = " + blockMap.get(b).get(0) + " UEVAR = " + blockMap.get(b).get(1));
				System.out.println("-------------LIVE OUT ------------------");
				System.out.println("LIVE OUT = " + livenessMap.get(b));
				System.out.println("*****************************************************************");
			}
		}
		
	}
	/*  Method: computeUEVARandVARKILL
	 *  Description : helper function to compute UEVAR and VARKILL
	 *  Input : Basic Block, block Map
	 *  returns : Block Map with updated UEVAR and VARKILL
	 */
	public Map<Block, List<FlowSet<String>>> computeUEVARandVARKILL(Block b, Map<Block, List<FlowSet<String>>> blockMap){
		List<FlowSet<String>> innerList = new ArrayList<FlowSet<String>>(); // Holds VARKILL at index 0 and UEEXP at index 1
		FlowSet<String> UEEXP = new ArraySparseSet<String>();// Hold UEEXP variables
		FlowSet<String> VARKILL = new ArraySparseSet<String>(); // Holds VARKILL variables
		Iterator<Unit> blockIt = b.iterator();
		// Logic to compute UEVAR and VARKILL
		while(blockIt.hasNext()) {
			// For each unit in the basic block
			Unit unit = blockIt.next();
			for (ValueBox useBox: unit.getUseBoxes()) {
				//Logic to add rightSide Operands to UEVAR if not present in VARKILL
				if (useBox.getValue() instanceof Local) {
					Local useLocal = (Local) useBox.getValue();
					if(!VARKILL.contains(useLocal.getName())) {
						UEEXP.add(useLocal.getName());
					}
				}
			}
			for (ValueBox defBox: unit.getDefBoxes()) {
				// Logic to add leftSide Operands to VARKILL
				if (defBox.getValue() instanceof Local) {
					Local defLocal = (Local) defBox.getValue();
					VARKILL.add(defLocal.getName());
				}
			}
		}
		innerList.add(VARKILL);
		innerList.add(UEEXP);
		blockMap.put(b, innerList);
		return blockMap;
	}
}

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;


public class nfa {
	static ArrayList<State> nfaStates = new ArrayList<State>();
	static ArrayList<State> nfaAcceptingStates = new ArrayList<State>();
	static char[] inputs;
	static State nfaStartingState = null;
	
	static Set<dfaState> dfaStates = new HashSet<dfaState>();
	static dfaState dfaStartingState = null;
	static ArrayList<dfaState> sortedStates;
	static ArrayList<dfaState> dfaAcceptingStates;
	
	public static void main(String[] args) {

		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(args[0]));
		} catch (Exception e) {
		System.out.println(args[0] + " is not a valid file.");
		System.exit(-1);
		}
		
		createNFA(fileScanner);

		convertToDfa();
		
		do{
			ArrayList<dfaState> tempList = new ArrayList<dfaState>();
			tempList.addAll(dfaStates);
			for(int i=0;i<tempList.size();i++){
				if(!tempList.get(i).checked){
//					System.out.println("----- state "+ tempList.get(i).name+"not checked");
					createDfaStates(tempList.get(i));
				}
			}
		}while(dfaStatesFinsished());
		
		//sort dfa states based on name
		
		sortedStates = sortDfaStates();
		
		
		for (dfaState state : sortedStates){
			System.out.print(state.nfaStates+" ");
		}
				
		System.out.print("\n Sigma:     ");
		for(int i=0;i<inputs.length-1;i++){
			System.out.print(inputs[i]+"    ");
		}
		
		System.out.print("\n------------------");
		
		for (dfaState state : sortedStates){
			System.out.print("\n     "+state.name+":    ");
			for(int i=0;i<inputs.length-1;i++){
				System.out.print(getStateName(state.map.get(inputs[i]))+"    ");
			}
			
			
			
			
		}
		
		System.out.println("\n------------------");
		
		System.out.println("s:  "+dfaStartingState.name);
		dfaAcceptingStates = findDfaAcceptingStates();
		System.out.print("A:  ");
		
		for(int i=0;i<dfaAcceptingStates.size();i++){
			if(i==0)System.out.print("{");
			System.out.print(dfaAcceptingStates.get(i).name);
			if(i==dfaAcceptingStates.size()-1){
				System.out.println("}");
			}else{
				System.out.print(",");
			}
		}
		
		System.out.println("The following strings are accepted:");

		
	}

public static ArrayList<dfaState> findDfaAcceptingStates(){
	ArrayList<dfaState> acceptingStates = new ArrayList<dfaState>();
	for(dfaState state : dfaStates){
		for(int i=0;i<state.nfaStates.size();i++){
			for(int k=0;k<nfaAcceptingStates.size();k++){
				if(state.nfaStates.get(i) == nfaAcceptingStates.get(k)){
					acceptingStates.add(state);
				}
			}
		}
	}
	return acceptingStates;
}
	
public static int getStateName(ArrayList<State> list){
	for(dfaState state : sortedStates){
		if(state.nfaStates.equals(list)){
			return state.name;
		}
	}
	
	
	return -1;
}
	
	
public static ArrayList<dfaState> sortDfaStates(){
	ArrayList<dfaState> sortedStates = new ArrayList<dfaState>();
	
	while(sortedStates.size()<dfaStates.size()){
		for(dfaState state : dfaStates){
			if(state.name == sortedStates.size()){
				sortedStates.add(state);
			}
		}
	}
	
	return sortedStates;	
}
	
public static boolean dfaStatesFinsished(){
		for(dfaState state : dfaStates){
			if(state.checked == false){
//				System.out.println("this hits for state "+state.name);
				createDfaStates(state);
				return false;
			}
		}
	return true;
}
	
	/*
	 * createNfa takes the file and updates 3 things 
	 * 1. list of the nfa states with a string as the transitions(this means that they will have to be parsed as they are retrived
	 * 2. list of accepting states (a string will be valid if it ends at one of these states
	 * 3. the inital state of the nfa 
	 */
	public static void createNFA(Scanner fileScanner){
		//first line of file is the number of states
				int numStates = Integer.parseInt(fileScanner.nextLine());
//				System.out.println(numStates);
				
				//create the states 
				for(int i=0;i<numStates;i++){
					nfaStates.add(new State(i));
				}
				//System.out.println(nfaStates.size());
				
				//second line is availiable input tokens
				StringTokenizer inputTokens = new StringTokenizer(fileScanner.nextLine());
				
//				while(inputTokens.hasMoreTokens()){
//					System.out.print(inputTokens.nextToken()+" ");
//				}
				
				inputs = new char[inputTokens.countTokens()+1];
				for(int i =0;i<inputs.length-1;i++){
					inputs[i] = inputTokens.nextToken().charAt(0);
				}
				inputs[inputs.length-1] = ' ';//add in empty character because tokenizer removes it
				System.out.print("Sigma: ");
				for(int i=0;i<inputs.length;i++){
					System.out.print(inputs[i]+" ");
				}System.out.println("\n------");
			
				
				//Third line until the number of states has been met
				//will be the stateName: and then the transitions available given the input character
				
				for(int i =0;i<numStates;i++){
					StringTokenizer st = new StringTokenizer(fileScanner.nextLine());
					st.nextToken();//removes the name of the state
					String temp = "";
					while(st.hasMoreTokens()){
						temp += st.nextToken() + " ";
					}
					
					nfaStates.get(i).addTransitionsLine(temp);
				}
				
				for(int i =0;i<nfaStates.size();i++){
						System.out.println(nfaStates.get(i));
				}
				
				//next line is the startingState
				nfaStartingState = nfaStates.get(Integer.parseInt(fileScanner.nextLine()));
//				System.out.println(nfaStartingState.name);
				
				
				//the final line will be a list of accepting states 
				nfaAcceptingStates = new ArrayList<State>();
				StringTokenizer acceptST = new StringTokenizer(fileScanner.nextLine(),"{,}");
				while(acceptST.hasMoreTokens()){
					nfaAcceptingStates.add(nfaStates.get(Integer.parseInt(acceptST.nextToken())));
				}
				System.out.println("------");
				System.out.println("s: "+nfaStartingState.name);
				System.out.print("A: {");
				for(int i =0;i<nfaAcceptingStates.size();i++){
					System.out.print(nfaAcceptingStates.get(i).name);
					if(i==nfaAcceptingStates.size()-1){
						System.out.println("}");
					}else{
						System.out.print(",");
					}
				}
	}
	
	public static void convertToDfa(){
		
		
		System.out.print("\nTo DFA: ");		
		
		//The start state of the dfa will be the lambda transitions of the starting state in the nfa
		dfaStartingState = new dfaState(dfaStates.size());
		
		dfaStartingState.nfaStates.addAll(State.emptyMoves(nfaStartingState));
//		System.out.println("empty: "+State.emptyMoves(nfaStartingState));
		dfaStates.add(dfaStartingState);

		createDfaStates(dfaStartingState);
		
		
					
	
		}//end of convert dfa method	

	static public void createDfaStates(dfaState state){
		/* the next 2 steps we will do for all new states added to the list
 		 * for each possible input symbol
 		 * 1. apply move with character for each state included..this will return a set 
 		 * 2. apply lambda transitions to these states possibly resulting in a new set 
 		 * this final set of states will be a dfa state.
  		 * we repeat this any time a new state is made 
  		 */
		for(int k=0;k<inputs.length-1;k++){
			ArrayList<State> moveList = new ArrayList<State>();
			
			for(int i=0;i<state.nfaStates.size();i++){
				moveList.addAll(State.moveWithInput(state.nfaStates.get(i), inputs[k]));
			}
			
			ArrayList<State> emptyList = new ArrayList<State>();
			for(int j=0;j<moveList.size();j++){
				emptyList.addAll(State.emptyMoves(moveList.get(j)));
			}
			
			Set<State> tempSet = new HashSet<State>();
			tempSet.addAll(moveList);
			tempSet.addAll(emptyList);
			
//			System.out.println("state "+state.name+" with input "+inputs[k]);
//			System.out.println(tempSet);
			ArrayList<State> arrayTemp = new ArrayList<State>();
			arrayTemp.addAll(tempSet);
//			System.out.println(arrayTemp);
			state.map.put(inputs[k], arrayTemp);
			dfaState tempState = null;
			for(dfaState existingState : dfaStates){
				if(existingState.nfaStates == arrayTemp){
					System.out.println("match");
					break;
				}else{
					tempState = new dfaState(dfaStates.size());
					tempState.nfaStates.addAll(tempSet);
				}
			}
			state.checked = true;
			//need to check before insert into dfaStates
			boolean testingBool = true;
			for(dfaState state1 : dfaStates){
				
				//something is wrong here
				Set<State> tempSet2 = new HashSet<State>();
				tempSet2.addAll(state1.nfaStates);
				if(tempSet2.equals(tempSet)){
					testingBool = false;
				}
				
			}
			
			if(testingBool){
				dfaStates.add(tempState);
			}
		}
		
	
	
	}
	
	
	
}
class State{
	int name;
	String transitions;
	boolean marked = false;
	
	public State(int name){
		this.name = name;
		transitions = "";
	}
	
	public void addTransitionsLine(String trans){
		transitions = trans;
	}
	
	public String toString(){
		return ""+name;// +": "+ transitions;
	}
	
	//returns states reachable with lambda 
	public static ArrayList<State> emptyMoves(State state){
		ArrayList<State> list = new ArrayList<State>();
//		System.out.println("\nempty moves called with state "+state.name);
		list.add(state);
		
		StringTokenizer st = new StringTokenizer(state.transitions);
		String tempLambda = "";
		while(st.hasMoreTokens()){
			tempLambda = st.nextToken();
		}//we only care about the lambda transition in this method aka the last one in the string
		
		StringTokenizer st2 = new StringTokenizer(tempLambda,"{,}");
		int tempStateNum;
		while(st2.hasMoreTokens()){
			//these will be the states we can reach from this state via lambda
			tempStateNum = Integer.parseInt(st2.nextToken());
//			System.out.println("temp number is "+tempStateNum);
			
				list.add(nfa.nfaStates.get(tempStateNum));
				list.addAll(State.emptyMoves(nfa.nfaStates.get(tempStateNum)));
			
			
		}
		Set<State> tempSet = new HashSet<State>();
		tempSet.addAll(list);
		list.clear();
		list.addAll(tempSet);
		
//		System.out.println();
//		for(int i=0;i<nfa.nfaStates.size();i++){
//			System.out.println(nfa.nfaStates.get(i));
//		}
//		for(int i =0;i<list.size();i++){
//			System.out.println(list.get(i).name);
//		}
		return list;
	}

	//returns states reachable with a character
	public static ArrayList<State> moveWithInput(State state, char input){
//		System.out.println("\nmove called with state "+state.name +" and with input "+input);
		
		ArrayList<State> possibleMoves = new ArrayList<State>();
		int inputCounter;
		for(inputCounter = 0;inputCounter<nfa.inputs.length;inputCounter++){
			if(nfa.inputs[inputCounter] == input)break;
		}
		StringTokenizer st = new StringTokenizer(state.transitions);
		String temp = "";
		int tempCounter = 0;
		while(st.hasMoreTokens()){
			if(tempCounter == inputCounter){
				temp=st.nextToken();
			}
			st.nextToken();
			tempCounter++;
		}
		//temp at this point holds the string list of the states we can reach with the given character
//		System.out.print(temp);
		StringTokenizer st2 = new StringTokenizer(temp,"{,}");
		while(st2.hasMoreTokens()){
			int tempInt = Integer.parseInt(st2.nextToken());
			possibleMoves.add(nfa.nfaStates.get(tempInt));
//			System.out.println();
//			for(int i=0;i<nfa.nfaStates.size();i++){
//				System.out.println(nfa.nfaStates.get(i));
//			}
		}
		
//		System.out.println("possible moves");
//		for(int i = 0;i<possibleMoves.size();i++){
//			System.out.println(possibleMoves.get(i).name);
//		}
		return possibleMoves;
	}
	
}

class dfaState {
	int name;
	ArrayList<State> nfaStates;
	HashMap<Character, ArrayList<State>> map;
	boolean checked = false;
	
	public dfaState(int name){
		this.name = name;
		nfaStates = new ArrayList<State>();
		map = new HashMap<Character, ArrayList<State>>();
	}
}


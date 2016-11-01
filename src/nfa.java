import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class nfa {
	static ArrayList<State> nfaStates = new ArrayList<State>();
	static ArrayList<State> nfaAcceptingStates = new ArrayList<State>();
	static char[] inputs;
	static State nfaStartingState = null;
	
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
					System.out.println("adding");
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
	
		State.moveWithInput(nfaStartingState, 'a');
		
		
		
		
	}
	
	
}

class State{
	int name;
	String transitions;
	
	public State(int name){
		this.name = name;
		transitions = "";
	}
	
	public void addTransitionsLine(String trans){
		transitions = trans;
	}
	
	public String toString(){
		return ""+name +": "+ transitions;
	}
	
	//returns states reachable with lambda 
	public static ArrayList<State> emptyMoves(State state){
		ArrayList<State> availStates = new ArrayList<State>();
		availStates.add(state);
		
		StringTokenizer st = new StringTokenizer(state.transitions);
		String tempLambda = "";
		while(st.hasMoreTokens()){
			tempLambda = st.nextToken();
		}//we only care about the lambda transition in this method aka the last one in the string
		
		StringTokenizer st2 = new StringTokenizer(tempLambda,"{,}");
		while(st2.hasMoreTokens()){
			//these will be the states we can reach from this state via lambda
			availStates.add(nfa.nfaStates.get(Integer.parseInt(st2.nextToken())));
		}
		
//		for(int i =0;i<availStates.size();i++){
//			System.out.println(availStates.get(i).name);
//		}
		return availStates;
	}

	//returns states reachable with a character
	public static ArrayList<State> moveWithInput(State state, char input){
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
		StringTokenizer st2 = new StringTokenizer(temp,"{,}");
		while(st2.hasMoreTokens()){
			possibleMoves.add(nfa.nfaStates.get(Integer.parseInt(st2.nextToken())));
		}
//		for(int i = 0;i<possibleMoves.size();i++){
//			System.out.println(possibleMoves.get(i).name);
//		}
		return possibleMoves;
	}
}


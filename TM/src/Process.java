
import java.util.*;

class Process {	
    final int HISTORY_MAX_SIZE = 100; 
    static boolean ERROR;    
    private int currentRuleIndex;
    private int initStateIndex;
    private String currentSymbol;
    private Rule currentRule;    
    private Tape tape;
    private LinkedList<Integer> history;

    public Process() {
    	history = new LinkedList<Integer>();
    };
    
//    public Process(Vector<Rule> V, Tape t) {
//	tape = t;
//	currentSymbol = tape.getCurrentSymbol();
//	currentRuleIndex = SearchRule(0, t.getCurrentSymbol());
//	currentRule = V.get(currentRuleIndex);
//	history = new LinkedList<Integer>();
//	ERROR = false;
//    }	

    public void setTape(Tape t) {
	tape = t;
    }
    
    private int SearchRule(int currentState,String currentSymbol) {
	int i;
	Rule r;
	for(i = 0; i < TMUI.ruleVet.size(); i++) {
		r = TMUI.ruleVet.get(i);
//		r = TMUI.tab.model.data.get(i);
		if(currentState == r.ot && r.oy.equals(currentSymbol))
			return i;
	}
		return -1;
    }
    		
    public void reset(int initStateIndex) {
    this.initStateIndex = initStateIndex;
	currentSymbol = tape.getCurrentSymbol();
	currentRuleIndex = SearchRule(initStateIndex, currentSymbol);
	currentRule = TMUI.ruleVet.get(currentRuleIndex);
	ERROR = false;
	history = new LinkedList<Integer>();
	TMUI.repaintTapeArea();
    }

    public int shiftAndGetHaltStatus () {   	
    	if(ERROR) return Rule.ERROR;//Error
    	
    	history.addLast(currentRuleIndex);    	
    	currentRule = TMUI.ruleVet.get(currentRuleIndex);    	
		tape.writeSymbol(currentRule.ny);
    	
    	switch(currentRule.dir) {
	    	case Rule.RIGHT: tape.shiftRight();break;
	    	case Rule.LEFT: tape.shiftLeft();break;
    	}
    	if(currentRule.nt < 0){
    		return currentRule.nt;
    	}
    	return Rule.NOT_HALTED;
    }

//    public void updateMachine() {
//	currentSymbol = tape.getCurrentSymbol();
//	currentRuleIndex = SearchRule(currentState, currentSymbol);
//	currentRule = TMUI.ruleVet.get(currentRuleIndex);
//    }
    
    public int stepForward() {  
		if(currentRuleIndex == -1) {
			ERROR = true;
		}else {			
//			steps++;			
			currentSymbol = tape.getCurrentSymbol();
			if(history.isEmpty())
				currentRuleIndex = SearchRule(initStateIndex,currentSymbol);
			else {
				currentRule = TMUI.ruleVet.get(currentRuleIndex);
				currentRuleIndex = SearchRule(currentRule.nt,currentSymbol);
			}			
			
			if(currentRuleIndex == -1) {
				ERROR = true;
			}else {
				TMUI.tab.repaintTable(currentRuleIndex);				
				if(history.size() > HISTORY_MAX_SIZE) {
				    history.removeFirst();
				}
			}

		}
		return currentRuleIndex;
  }
	
    public boolean stepBack() {
//	steps--;
//	if(steps < 0) {
//	    steps = 0;
//	}
	if(ERROR) ERROR = false;
	if(!history.isEmpty()) {
		currentRuleIndex = history.removeLast();	
		currentRule = TMUI.ruleVet.get(currentRuleIndex);
	    if((//tape.getCurrentPosition() == 0  || 
		    	tape.getCurrentPosition() == tape.getSize() - 1) && 
		        tape.getCurrentSymbol().equals(tape.getFillSymbol()) && 
		        tape.getSize() > TMUI.originalTape.getSize()) {
			
			tape.deleteCell();
		    
	    } else if(currentRule.dir == Rule.RIGHT) {
		tape.shiftLeft();
	    } else if(currentRule.dir == Rule.LEFT) {
		tape.shiftRight();
	    }      

		tape.writeSymbol(currentRule.oy);
	    
	    if(!history.isEmpty()) {
	    	TMUI.tab.repaintTable(currentRuleIndex);
	    }else {
	    	stepForward();
	    }	

	    return true;
	} else {
	    return false;
	}
    }
}	

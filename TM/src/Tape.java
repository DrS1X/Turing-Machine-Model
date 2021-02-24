
import java.util.*;

public class Tape {
	
    private static String FILL_SYMBOL = " ";
    private static int TRAILING_EDGE = 0;

    private int position; 
    private LinkedList<String> tape;
    private int origin = TRAILING_EDGE;

    public Tape(int positionInRight,String s) {
    	if(positionInRight == -1)
    		position = TRAILING_EDGE;
    	else
    		position = TRAILING_EDGE+s.length()-1;
    	
        tape = new LinkedList<String>();

        int i;
        String c;
        for(i = 0;i<TRAILING_EDGE;i++)
        	tape.add(FILL_SYMBOL);
        for(i = 1; i<s.length()+1; i++) {
        	c = s.substring(i-1,i);
        	if(c.equals("[")) {
        		s = s.substring(0,i-1)+s.substring(i,i+1)+s.substring(i+2);
        		position = TRAILING_EDGE+i-1;
        		tape.add(s.substring(i-1,i));
        	}else {
        		tape.add(c);
        	}
        }
        for(i = 0; i<TRAILING_EDGE;i++)
        	tape.add(FILL_SYMBOL);
    }
	
    
    public Tape(Tape t) {
	position = t.position;
        tape = new LinkedList<String>();
        for (String s : t.tape)
            tape.add(s);
	// tape = (LinkedList<String>) t.tape.clone();
    }

    public int getCurrentPosition() {
	return position;
    }

    public void setCurrentPosition(int i) {
	position = i;
    }

    public String getCurrentSymbol() {
	return tape.get(position);
    }

    public String getSymbolAt(int i) {
	return tape.get(i);
    }
    
    public int getSize() {
	return tape.size();
    }

    public String getFillSymbol() {
	return FILL_SYMBOL;
    }

    public void setFillSymbol(String s) {
	FILL_SYMBOL = s;
    }
    
    public void deleteCell() {
	tape.remove(position);
	if (position != 0) 
	    position--;
	else
	    origin--;
    }

    public void shiftLeft() {
	if (position == 0) {
		origin++;
		tape.addFirst(FILL_SYMBOL);
	} else {
	    position--;
	}
    }

    public void shiftRight() {
	if (position == tape.size() - 1) {
		tape.addLast(FILL_SYMBOL);
	}
	position++;
    }

    public void writeSymbol(String symbol) {
	if (!(tape.get(position)).equals(symbol)) {
	    tape.remove(position);
	    tape.add(position, symbol);
	}
    }

    public int getOrigin() {
	return origin;
    }
}		
		
		

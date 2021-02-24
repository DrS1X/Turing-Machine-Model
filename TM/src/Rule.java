
class Rule {
    public static final int NOT_HALTED = 0;
    public static final int STOP = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
//    public static final int HALTED = -1;
    public static final int ACCEPT = -2;
    public static final int REJECT = -3;
    public static final int ERROR = -4;
	int ot, nt, dir;
	String oy, ny;
	public Rule() {}
	
	public Rule(int ot,int symbolIndex) {
		this.ot = ot;
		this.nt = -1;
		this.dir = -1;
		this.oy = this.ny = RuleTable.SYMBOLSET[symbolIndex].equals("B") ? " " : RuleTable.SYMBOLSET[symbolIndex];			
	}
	
	public Rule(int ot,String oy,int nt,String ny,String dir) {
		this.ot = ot;
		this.oy = oy;
		this.nt = nt;
		this.ny = ny;		
		switch(dir) {
		case "L": this.dir = LEFT; break;
		case "R": this.dir = RIGHT; break;
		case "S": this.dir = STOP; break;
		}
	}	
}

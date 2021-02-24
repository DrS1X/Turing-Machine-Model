
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TMUI extends JFrame {
	//static final long serialVersionUID = 1L;
    static final int MIN_SPEED = 0, MAX_SPEED = 100,INIT_SPEED = 50;

//    static final String[] INITHEAD = {"右端","左端","自定义"};
//	static JLabel initStateLabel = new JLabel("初始状态");
//	static JLabel initHeadLabel = new JLabel("读写头初始位置");
//	static JLabel inputLabel = new JLabel("输入纸带");
//	static JLabel speedLabel = new JLabel("速度");
//    static String statusString = "就绪";
    static final String[] INITHEAD = {"Right End","Left End","Custom"};
	static JLabel initStateLabel = new JLabel("Initial State");
	static JLabel initHeadLabel = new JLabel("Initial Read-write Head");
	static JLabel inputLabel = new JLabel("Tape");
	static JLabel speedLabel = new JLabel("Speed");
    static String statusString = "Ready";
    static String tip = "";
    
    JFileChooser fileChooserWindow = new JFileChooser();
    
    JComboBox<String> stateComboBox;
//    DefaultCellEditor actionDefaultCellEditor,stateDefaultCellEditor,symbolDefaultCellEditor;
//	static DefaultTableModel model = new DefaultTableModel(null,COLUMNNAMES);
//	static JTable tab = new JTable(model);
	static RuleTable tab;
    static Vector<Rule> ruleVet = new Vector<Rule>();
	static JScrollPane sc = new JScrollPane(tab);
    
	static JTextField initStateTextField = new JTextField();
	
	static JComboBox<String> initHeadComboBox = new JComboBox<String>(INITHEAD);
	
	ImageIcon importIcon,exportIcon,addIcon,delIcon,delAllIcon,breakPointIcon;
	JButton importButton,exportButton,addButton,delButton,delAllButton,breakPointButton;
	
    static TapeArea tapeArea = new TapeArea();
    
    static JComboBox<String> inputComboBox = new JComboBox<String>();
    static String inputString, lastInputString = null;
    
    static JButton runButton,backButton, stepButton, resetButton,debugButton;
    static ImageIcon runIcon, pauseIcon,backIcon,stepIcon,resetIcon,debugIcon;
     
    static JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, 
					     MAX_SPEED, INIT_SPEED);
    
    public static JTextArea statusTextArea = new JTextArea();    
    public static JTextArea tipTextArea = new JTextArea();
    public static javax.swing.Timer stepTimer;    
    
	static int stateNum;
    public static int delay,initHeadPosition = 1;
    static int currentRuleIndex, oldTapeLength,initState = 0, halted = Rule.NOT_HALTED;
    static boolean frozen = true, debug = false, initProcess = false;
    
    static Tape tape;
    static Tape originalTape = new Tape(initHeadPosition,"");
    static Process process;
    static Vector<Integer> breakPoint = new Vector<Integer> ();
    GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
    JPanel menuPanel = new JPanel();
    JPanel tabPanel = new JPanel();
    JPanel tapeAndStatusPanel = new JPanel();
    Box tapeAndStatusBox = new Box(BoxLayout.Y_AXIS);
    JToolBar controlBar = new JToolBar(JToolBar.HORIZONTAL);
    JToolBar tableBar = new JToolBar(JToolBar.HORIZONTAL);
    BufferedImage  image;
    JLabel imageLabel;
    JPanel imagePanel;
    static final int MENUWIDTH = 260;
    static final int EDGE = 20;
    
    public TMUI(String s) {
    	super(s);
    }
     
    public void setUI() {
    	tab = new RuleTable(){
    		public boolean isCellEditable(int row,int col) {
    			if(col == 0 || col == 1)
    				return false;
    			else
    				return true;
    		}
    	};
        stateNum = 0;
    	process = new Process();    	
       	setSize(new Dimension(900, 700));
       	try {
       		image = ImageIO.read(getClass().getResource("images/turingMahchine.png"));
       	}
        catch (IOException e) {
            e.printStackTrace();
        }
       	int imageWidth = image.getWidth();
       	int imageHeight = image.getHeight();
       	int menuHeight = imageHeight*(MENUWIDTH)/imageWidth;     
       	Image _image = image.getScaledInstance(MENUWIDTH,menuHeight,Image.SCALE_DEFAULT);
       	imageLabel = new JLabel(new ImageIcon(_image));
       		
       	Dimension screenSize=java.awt.Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕的大小
       	Dimension frameSize=this.getSize();//这里的this可替换成 窗体的名字，下同
       	this.setLocation((screenSize.width-frameSize.width)/2,(screenSize.height-frameSize.height)/2);  	               	
           
       	JPanel inputPanel = new JPanel(new BorderLayout());
	    inputPanel.add(inputLabel, BorderLayout.WEST);
	    inputPanel.add(inputComboBox, BorderLayout.SOUTH);
       	
	    JPanel speedPanel = new JPanel(new BorderLayout());
	    speedPanel.add(speedLabel, BorderLayout.WEST);
	    speedPanel.add(speedSlider, BorderLayout.SOUTH);
	    
	    menuPanel.setLayout(gbl);
//	    gbc.insets= ins;
	    gbc.insets = new Insets(0,0,0,0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(imageLabel, gbc);
		menuPanel.add(imageLabel);
		
       	setGBC(0,4,4,1,tableBar,new Insets(0,0,0,0),false);
		setGBC(0,6,4,1,inputPanel,new Insets(0,EDGE,0,EDGE),true);
		setGBC(0,7,3,1,initHeadLabel,new Insets(0,EDGE,0,0),true);
		setGBC(3,7,1,1,initHeadComboBox,new Insets(0,EDGE,0,EDGE),true);
		setGBC(0,9,3,1,initStateLabel,new Insets(0,EDGE,0,0),true);
		setGBC(3,9,1,1,initStateTextField,new Insets(0,EDGE,0,EDGE),true);
//		setGBC(0,10,4,1,0,speedLabel,new Insets(0,0,0,0),false);
		setGBC(0,11,4,1,speedPanel,new Insets(0,EDGE,0,EDGE),true);
		setGBC(2,12,2,1,controlBar,new Insets(0,0,0,0),false);
       	
    	initStateTextField.setText(new String("0"));    	

//    	initHeadComboBox.setToolTipText("可用[ ]自定义读写头的初始位置，例如“1[0]0”");
    	initHeadComboBox.setToolTipText("use [] to flag initial position of read-write head, e.g. 1[0]0");
       	initHeadComboBox.addActionListener(new ActionListener() {
       		public void actionPerformed(ActionEvent e) {
       			switch(initHeadComboBox.getSelectedIndex()) {
       			case 1: originalTape = new Tape(-1,inputString);break;
       			case 0: originalTape = new Tape(-1,inputString);break;
       			case 2:{
//       				tip = "请用[ ]定义读写头的初始位置，例如“1[0]0”";
       				tip = "use [] to flag initial position of read-write head, e.g. 1[0]0";
       				printStatus();
       			}
       			}
       		}
       	}); 	
       	initStateTextField.addActionListener(new ActionListener() {
       		public void actionPerformed(ActionEvent e) {
       			String s = initStateTextField.getText();
       			initState = Integer.parseInt(s);
       		}
       	});
       	
//    actionDefaultCellEditor = new DefaultCellEditor(new JComboBox<String>(ACTIONSET));	 
//    symbolDefaultCellEditor = new DefaultCellEditor( new JComboBox<String>( SYMBOLSET ));
//    stateComboBox = new JComboBox<String>( STATESET );
//    stateComboBox.setEditable(true);
//    stateDefaultCellEditor = new DefaultCellEditor( stateComboBox );       	
   
    importIcon = new ImageIcon(getClass().getResource("images/Open.png"));
    exportIcon = new ImageIcon(getClass().getResource("images/Save.png"));
   	addIcon = new ImageIcon(getClass().getResource("images/Plus.png"));
   	delIcon = new ImageIcon(getClass().getResource("images/Garbage.png"));
   	delAllIcon = new ImageIcon(getClass().getResource("images/ClearAll.png"));
   	breakPointIcon = new ImageIcon(getClass().getResource("images/BreakPoint.png"));
   	importButton = new JButton();
   	exportButton = new JButton();   	
   	addButton = new JButton();
   	delButton = new JButton();
   	delAllButton = new JButton();
   	breakPointButton = new JButton();
   	importIcon = makeButton(importButton,importIcon);
   	exportIcon = makeButton(exportButton,exportIcon);
   	addIcon = makeButton(addButton,addIcon);
   	delIcon = makeButton(delButton,delIcon);
   	delAllIcon = makeButton(delAllButton,delAllIcon);
   	breakPointIcon = makeButton(breakPointButton,breakPointIcon);
   	importButton.setToolTipText("Import control rule");
   	exportButton.setToolTipText("Export control rule");
   	addButton.setToolTipText("Add 3 rows");
   	delButton.setToolTipText("Delete last 3 rows");
   	delAllButton.setToolTipText("Clear table");
   	breakPointButton.setToolTipText("Flag/ Clear breakpoint");
    importButton.addActionListener(new FileInput());
    exportButton.addActionListener(new FileOutput()); 
    addButton.addActionListener(new AddButtonListener());
    delButton.addActionListener(new DelButtonListener());
    delAllButton.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e) {
    		stateNum = 0;
    		tab.delAll();
    		ruleVet.clear();
    		breakPoint.clear();
    	}
    });
    breakPointButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		int r = tab.getSelectedRow();
    		int pointIndex;
    		if(r != -1) {
    			pointIndex = breakPoint.indexOf((Integer)r);
    			if (pointIndex == -1) {
    				breakPoint.add(r);
    				tab.setBreakPoint(r);
    			}
    			else{
    				breakPoint.remove(pointIndex);
    				tab.clearBreakPoint(r);
    			}
    		}
    	}
    });
    tableBar.add(importButton);
    tableBar.add(exportButton);    
    tableBar.add(addButton);
    tableBar.add(delButton);
    tableBar.add(delAllButton);
    tableBar.add(breakPointButton);
    tableBar.setFloatable(false);
    
    pauseIcon = new ImageIcon(getClass().getResource("images/Stop.png"));
    
    runIcon   = new ImageIcon(getClass().getResource("images/Start.png"));
    backIcon  = new ImageIcon(getClass().getResource("images/Back.png"));
    stepIcon  = new ImageIcon(getClass().getResource("images/Forward.png"));
    resetIcon = new ImageIcon(getClass().getResource("images/Update.png"));
    debugIcon = new ImageIcon(getClass().getResource("images/Debug.png"));
    runButton = new JButton(runIcon);
    runButton.addActionListener(new RunButtonListener());
    runButton.setToolTipText("Run");    
    backButton = new JButton(backIcon);
    backButton.addActionListener(new BackButtonListener());
    backButton.setToolTipText("Last step");    
    stepButton = new JButton(stepIcon);
    stepButton.addActionListener(new StepButtonListener());
    stepButton.setToolTipText("Next step");    
    resetButton = new JButton(resetIcon);
    resetButton.addActionListener(new ResetButtonListener());	
    resetButton.setToolTipText("Reset");
    debugButton = new JButton(debugIcon);
    debugButton.setToolTipText("Debug");
    debugButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		if(debug) {
    			debug = false;
    			stepButton.setEnabled(false);
    			backButton.setEnabled(false);
    		}else {
    			debug = true;
    			stepButton.setEnabled(true);
    			backButton.setEnabled(true);
    		}
    	}
    });
    runIcon   = makeButton(runButton,runIcon);
    backButton.setIcon(backIcon);
    stepIcon  = makeButton(stepButton,stepIcon);
    resetIcon = makeButton(resetButton,resetIcon);
	stepButton.setEnabled(false);
	backButton.setEnabled(false);
	
    inputComboBox.addActionListener(new inputListener());
    inputComboBox.setEditable(true);
//    inputComboBox.setToolTipText("输入二进制整数，若输入两个整数需用空格分隔");
    inputComboBox.setToolTipText("Enter binary integer separated by blank space");

    speedSlider.addChangeListener(new SpeedSliderListener());
//    speedSlider.setToolTipText("调整运行速度");
    speedSlider.setPreferredSize(new Dimension(500, 30));
    
    delay = (int)((Math.exp(MAX_SPEED/10.0) / Math.exp(speedSlider.getValue()/10.0))/3.0);
    
    stepTimer = new javax.swing.Timer(delay, new StepTimerListener());
    stepTimer.setInitialDelay(delay);
    
    tapeArea.setPreferredSize(new Dimension(getWidth(), 50));
    tapeArea.setBorder(BorderFactory.createRaisedBevelBorder());
    
    speedSlider.setMaximumSize(new Dimension(MENUWIDTH-2*EDGE, 50));
    speedSlider.setPreferredSize(new Dimension(MENUWIDTH-2*EDGE, 50));
    Hashtable<Integer, JComponent> hashtable = new Hashtable<Integer, JComponent>();
    hashtable.put(MIN_SPEED, new JLabel("Slow"));
    hashtable.put(MAX_SPEED, new JLabel("Fast")); 
    speedSlider.setLabelTable(hashtable);
    speedSlider.setPaintTicks(true);
    speedSlider.setPaintLabels(true);    
    
    controlBar.setFloatable(false);
    controlBar.add(runButton);
    controlBar.add(resetButton);
    controlBar.add(debugButton);
    controlBar.add(backButton);
    controlBar.add(stepButton);
       
    statusTextArea.setEditable(false);
    statusTextArea.setBackground(null);
   	tabPanel.add(tab);
   	
   	tipTextArea.setForeground(Color.red);
   	tipTextArea.setBackground(null);
   	tipTextArea.setEditable(false);
   	statusTextArea.setForeground(Color.black);
   	JPanel statusAndTip = new JPanel(new FlowLayout(0,30,0));
   	statusAndTip.add(statusTextArea);
   	statusAndTip.add(tipTextArea);
   	statusTextArea.setPreferredSize(new Dimension(100,20));
   	tapeAndStatusBox.add(tapeArea);
   	tapeAndStatusBox.add(statusAndTip);
   	
    setLayout(new BorderLayout());
    sc = new JScrollPane(tab);
    add(menuPanel,BorderLayout.WEST);
    add(sc,BorderLayout.CENTER);
    add(tapeAndStatusBox,BorderLayout.SOUTH);    
//    getContentPane().add(mainBox);
    this.setVisible(true);
    try {
	    InputStream inputStream = getClass().getResourceAsStream("defaultRule/N_add_1.txt");
	    InputStreamReader defaultRule = new InputStreamReader(inputStream, "utf-8");
	    BufferedReader br = new BufferedReader(defaultRule);    
	    Decode(br);
    }catch(IOException e) {}
    tab.repaintTable(-1); 

	fileChooserWindow.setCurrentDirectory(new File("./rules"));
	
	fileChooserWindow.setFileFilter(new MyCustomFilter());
	
//	addComponentListener(new ComponentAdapter() {
//		public void componentShown() {
//		    repaint();
//		}
//		public void componentResized() {
//		    repaint();
//		}
//	});
    }
	
    ImageIcon makeButton(JButton b,ImageIcon ic) {
//    	b.setPreferredSize(new Dimension(40,50));
//    	ic = new ImageIcon(ic.getImage().getScaledInstance(40,40,ic.getImage().SCALE_DEFAULT));
       	b.setIcon(ic);
//       	b.setFocusPainted(false);
       	b.setContentAreaFilled(false);
//       	b.setBackground(new Color(240,240,240));
       	return ic;
    }
    
	void setGBC(int x,int y,int w,int h,Component comp,Insets ins,boolean horizontal) {
		gbc.insets= ins;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weighty = 1;
//		gbc.weightx = 0;
		if(horizontal) {
//			gbc.anchor=GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
		}
		else {
//			gbc.anchor=GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.NONE;
		}
//		comp.setFont(chianeseFont);
		gbl.setConstraints(comp, gbc);
		menuPanel.add(comp);
	}
      
 	public static void repaintTapeArea() {
	tapeArea.repaint();
	}

 	public static void check() {
 		boolean right = true;
 		int i,j;
 		Rule ri=null,rj;
 		for(i = 0; i < ruleVet.size(); i++) {
 			ri = ruleVet.get(i);
 			if(ri.nt == -1) {
 				right = false;
 				break;
 			}else if(ri.nt < -1) 
 				continue;
 			
 			for(j = 0; j < ruleVet.size(); j++) {
 				rj = ruleVet.get(j);
 				if(ri.nt == rj.ot) {
 					break;
 				}
 			}
 			if(j >= ruleVet.size()) {
 				right= false;
 				break;
 			}
 		}
 		
 		if(!right && ri!= null) {
 			if(ri.nt != -1)
 				//tip = "警告：存在未定义的状态" + String.valueOf(ri.nt);
 				tip = "Warn: Status" + String.valueOf(ri.nt) +"is not defined";
 			else
 				tip = "Warn: Status" + String.valueOf(ri.ot) + "is not complete";
 		}else
 			tip = "";
		printStatus(); 		
 	}
 	
	static void setPaused() {
		runButton.setIcon(runIcon);
		runButton.setToolTipText("Run");
		frozen = true;
		stepTimer.stop();
		tapeArea.machineIsRunning = false;
		halted = Rule.NOT_HALTED;
		statusString = "Stop";
		printStatus();
    }
	
    static void setHalted() {
	runButton.setIcon(runIcon);
	runButton.setToolTipText("Run");
	frozen = true;
	stepTimer.stop();
    }

    static void setReady() {
	runButton.setIcon(runIcon);
	runButton.setToolTipText("Run");
	frozen = true;
	stepTimer.stop();
	halted = Rule.NOT_HALTED;
	statusString = "Ready";
	printStatus();
    }

    static void setRunning() {
	runButton.setIcon(pauseIcon);
	runButton.setToolTipText("Stop");
	frozen = false;
	halted = Rule.NOT_HALTED;
	statusString = "Running";
	process.setTape(tape);
	stepTimer.start();
    }

    static void printStatus() {
	if (process != null) {
            // statusTextArea.setText(TuringMain.class.getResource("turing.jpg").toString() + ": " + statusString);
	    statusTextArea.setText(" Status: " + statusString);
	    for(int i=0; i < 13 - statusString.length(); i++)
//		statusTextArea.append(" ");
//	    if (tip != "")
		tipTextArea.setText(tip);
	}
    }

    void doStep() {
	if(process != null) {
	    if(halted == Rule.NOT_HALTED) {
//		    for(int i = 0; i < n; i++) {
			halted = process.shiftAndGetHaltStatus();
			tapeArea.repaint();
//			process.updateMachine();
			if(halted != Rule.NOT_HALTED) {
			    setHalted();
			    if(halted == Rule.ERROR) {
			    	statusString = "Error";
			    	tip = "Warn: status is not defined";
			    	 printStatus();
			    }else {
				    switch(halted) {
				    case Rule.ACCEPT:
					statusString = "Accept";
					break;
				    case Rule.REJECT:
					statusString = "Reject";
					break;
				    default:
					statusString = "Halt";
				    }
			    printStatus();	
			    }
			}
			currentRuleIndex = process.stepForward();
			if(debug) {
				if(breakPoint.indexOf(currentRuleIndex) != -1 && !frozen)
					setPaused();
		    }
	    }
	}
	}
      
    class AddButtonListener implements ActionListener{
    	public void actionPerformed(ActionEvent e) {
//    		check();    		
    		//inputComboBox.setEnabled(true);
    		for(int i=0;i<3;i++) 
    			ruleVet.addElement(new Rule(stateNum,i)); 
			tab.add(stateNum);
			stateNum++;
		}
    }
    
    class DelButtonListener implements ActionListener{
    	public void actionPerformed(ActionEvent e) {
    		if(stateNum > 0) {
	    		stateNum--;
	    		for(int i = 2; i >= 0; i--) {
		    		tab.del(stateNum*3+i);
	    			ruleVet.removeElementAt(stateNum*3+i);
	    		}
	    		check();
    		}
		}
    }
	
    class RunButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
    	check();
	    if(process != null) {
		if(halted != Rule.NOT_HALTED) {
		    resetButton.doClick();
		}
		if(frozen) {
		    setRunning();
		} else {
		    setPaused();
		}
	    }
	}	
    } 
    
    class BackButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if(process != null) {
//		if(!animationTimer.isRunning()) {
		    if(process.stepBack()) {
//			oldTapeLength = tape.getSize();
			setPaused();
			repaintTapeArea();
//			repaintMachineArea();
		    }
//		}
	    }
	}
    }
    
    class StepButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
    	check();
	    if(halted == Rule.NOT_HALTED)
		setPaused();
//	    if(!animationTimer.isRunning()) {
		doStep();
//	    }
	}
    }
    
    class StepTimerListener implements ActionListener {
	public void actionPerformed (ActionEvent e) {
		if(inputString == null || inputString.equals(""))
	    	tipTextArea.setText("Please enter tape");
		else
			doStep();
	}
    }
    
    class ResetButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent e){
		check();
	    stepTimer.stop();
//	    animationTimer.stop();
	    tapeArea.resetColors();
	    tapeArea.removePartialCells();
	    tapeArea.tapeInit = false;
//	    machineArea.resetColors();
	    tape = new Tape(originalTape);
	    oldTapeLength = tape.getSize();
//	    fade = FADE_OUT;
	    tapeArea.setTape(tape);
	    process.setTape(tape);
	    process.reset(initState);
	    setReady();
	    tapeArea.repaint();
	    
	    currentRuleIndex = process.stepForward();
		if(debug) {
			if(breakPoint.indexOf(currentRuleIndex) != -1)
				frozen = false;
	    }
	}
    }
    
    class SpeedSliderListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    int fps = (int)speedSlider.getValue();
	    if(fps == 0) {   
		stepTimer.stop();
	    } else {
		delay = 
		    (int)((Math.exp(MAX_SPEED/10.0) / Math.exp(speedSlider.getValue()/10.0))/3.0);
		stepTimer.setInitialDelay(delay);
		stepTimer.setDelay(delay);
		if(!frozen) {
		    stepTimer.start();
		}
	    }
	}
    }
    
    class inputListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    	    int i, len;
    	    boolean match = false;
    	    String s;
    	    inputString = (String)inputComboBox.getSelectedItem();

    	    if(inputString == null || inputString.equals("")) {
    	    	tipTextArea.setText("Please enter tape");
    	    	return;
    	    }    	    	
    	    
    	    DefaultComboBoxModel model = (DefaultComboBoxModel)inputComboBox.getModel();
    	    if(!inputString.equals(originalTape)) {
    		originalTape = new Tape(initHeadPosition,inputString);
    		tape = new Tape(originalTape);
    		oldTapeLength = tape.getSize();
    		tapeArea.repaint();

    		len = inputComboBox.getItemCount();
    		for(i = 0; i < len; i++) {
    		    s = (String)model.getElementAt(i);
    		    if(inputString.equals(s)) {
    			match = true;
    		    }
    		}
    		resetButton.doClick();
    		if(!match) {
    		    model.insertElementAt(inputString, 0);
    		}
    	    }
    	}
        }

    class MyCustomFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "Text documents (*.txt)";
        }
    }
    
    class FileInput implements ActionListener{
    public void actionPerformed(ActionEvent evt) {
        int returnVal = fileChooserWindow.showOpenDialog(TMUI.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooserWindow.getSelectedFile();
            try(BufferedReader br = new BufferedReader(new FileReader(file))){//release br
            	delAllButton.doClick();
            	tab.delAll();
            	Decode(br);        		
                tab.repaintTable(-1);
                check();
            }catch (IOException e) {}
            
        } else {
            System.out.println("File access cancelled by user.");
        }
    }
    }
    
    void Decode(BufferedReader br) throws IOException {	   
	   String line = br.readLine();
	    Rule r = null;
	    String[] aLine= {"","","","","",""};
	    while (line != null) {
	        line = line.replaceAll("(?m)^[ \t]*\r?\n", "");
	        if (line != "") {
	        	stateNum++;
	        	StringTokenizer stok = new StringTokenizer(line,",");
	        	r = new Rule();
	        	
	    		aLine[0] = stok.nextToken();
	    		r.ot = Integer.parseInt(aLine[0]);
	    		aLine[1] = stok.nextToken();
	    		r.oy = (aLine[1].equals("B")) ? " " : aLine[1]; 
	    		aLine[2] = stok.nextToken();
	    		r.nt = Integer.parseInt(aLine[2]);
	    		aLine[3] = stok.nextToken();
	    		r.ny = (aLine[3].equals("B")) ? " " : aLine[3];
	    		aLine[4] = stok.nextToken();
	    		r.dir = Integer.parseInt(aLine[4]);
	        	 
	            ruleVet.addElement(r);
	        }
	        
	        switch(r.nt) {
	//            case Rule.HALTED: aLine[2] = "Halt";break;
	        case Rule.ACCEPT: aLine[2] = "Accept";break;
	        case Rule.REJECT: aLine[2] = "Reject";break;                    
	        }
	        switch(r.dir) {
	        case Rule.STOP: aLine[4] = "Stop";break;
	        case Rule.LEFT: aLine[4] = "Left";break;
	        case Rule.RIGHT: aLine[4] = "Right";break;
	        }
	        aLine[1] = aLine[1].equals(" ") ? "B" : aLine[1];
	        aLine[3] = aLine[3].equals(" ") ? "B" : aLine[3];
	//            model.addRow(aLine);
	        for(int i = 5; i > 0; i--)
	        	aLine[i] = aLine[i-1];
	        aLine[0] = "";
	        tab.addRow(aLine);
	        line = br.readLine();
	    }
	    stateNum /=3;
   
    }
    
    class FileOutput implements ActionListener{
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = fileChooserWindow.showOpenDialog(TMUI.this);
        String outputString;
        String lineSeparator = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        Rule r;
        for (int i = 0; i < ruleVet.size(); i++) {
        	r = ruleVet.get(i);
        	sb.append(String.valueOf(r.ot));
        	sb.append(",");
//    		r.oy = r.oy=="B" ? " " : r.oy; 
        	sb.append(r.oy);
        	sb.append(",");
        	sb.append(String.valueOf(r.nt)); 
        	sb.append(",");
//        	r.ny = r.ny=="B" ? " " : r.ny; 
            sb.append(r.ny);
            sb.append(",");
            sb.append(String.valueOf(r.dir));
            if (i != ruleVet.size() - 1) {
                sb.append(lineSeparator);
            }
        }
        outputString = sb.toString();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooserWindow.getSelectedFile();
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                w.write(outputString);
            } catch (IOException e) {
               
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }
    }
}

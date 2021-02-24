
import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

class RuleTable extends JTable{
	//static final long serialVersionUID = 1L;
	static final String[] SYMBOLSET = {"0","1","B"},
    		ACTIONSET = {"Left", "Right", "Stop"}, STATESET = {"Accept","Reject"},
//    		COLUMNNAMES = {"","当前状态", "当前符号","新状态","新符号","移动"};
    		COLUMNNAMES = {"","Current State", "Current Symbol","New State","New Symbol","Move"};
    JComboBox<String> stateComboBox;
	DefaultCellEditor actionDefaultCellEditor,stateDefaultCellEditor,symbolDefaultCellEditor;
	static DefaultTableModel model = new DefaultTableModel(null,COLUMNNAMES);
	DefaultTableCellRenderer dtcr;
	
	public RuleTable() {
		// TODO Auto-generated constructor stub		
		super(model);
		model.addTableModelListener(new TableModelListener() {
	    	public void tableChanged(TableModelEvent e) {
	    		int row,column,type;
	            type = e.getType();//获取事件类型(增、删、改等)
	            row = e.getFirstRow();//获取触发事件的行索引
	            column = e.getColumn();//获取触发事件的列索引
	            Rule r;
	            if(type == TableModelEvent.UPDATE) {
	            	r = TMUI.ruleVet.get(row);
	            	r = edit(row,column,r);
	            	TMUI.ruleVet.setElementAt(r,row);
	            }
	    		TMUI.check();
	        }		    
		});//修改表格单元格的值
	    stateComboBox = new JComboBox<String>( STATESET );
	    stateComboBox.setEditable(true);
		actionDefaultCellEditor = new DefaultCellEditor(new JComboBox<String>(ACTIONSET));	 
	    symbolDefaultCellEditor = new DefaultCellEditor( new JComboBox<String>( SYMBOLSET ));
	    stateDefaultCellEditor = new DefaultCellEditor( stateComboBox );    
	    
	}
	
	public void reset() {
		model.setRowCount(0);
	}
	
	public void add(int stateNum) {
		String rowData[] = {"",String.valueOf(stateNum),"","","",""};
		for(int i=0;i<3;i++) {
			rowData[2] = rowData[4] = SYMBOLSET[i];
			model.addRow(rowData);
	        this.getColumnModel().getColumn(5).setCellEditor(actionDefaultCellEditor);
	        this.getColumnModel().getColumn(4).setCellEditor(symbolDefaultCellEditor);
	        this.getColumnModel().getColumn(3).setCellEditor(stateDefaultCellEditor);			
		}
		repaintTable(-1);
	}
	
	public void addRow(String[] aLine) {
		model.addRow(aLine);
        this.getColumnModel().getColumn(5).setCellEditor(actionDefaultCellEditor);
        this.getColumnModel().getColumn(4).setCellEditor(symbolDefaultCellEditor);
        this.getColumnModel().getColumn(3).setCellEditor(stateDefaultCellEditor);
	}
	
	public void del(int row) {
		model.removeRow(row);
	}
	
	public void delAll() {
		model.setRowCount(0);
	}
	public Rule edit(int row,int column,Rule r) {
		String StringChanged = (String)this.getValueAt(row, column);
    	switch(column-1){
		case 0: r.ot = Integer.parseInt(StringChanged); break;
		case 1: {
			if(StringChanged.equals("B"))
				r.oy = " ";
			else
				r.oy = StringChanged;
			}break;
		case 2: {
			switch(StringChanged) {
    			case "Accept": r.nt = Rule.ACCEPT; break;
    			case "Reject": r.nt = Rule.REJECT; break;
//    			case "Halt": r.nt = Rule.HALTED; break;
    			case "": r.nt = -1; break;
    			default:
        			r.nt = Integer.parseInt(StringChanged);
			}break;
		}
		case 3: {
			if(StringChanged.equals("B"))
				r.ny = " ";
			else
				r.ny = StringChanged;
			}break;
		case 4: {
			switch(StringChanged) {
			case "L": r.dir = Rule.LEFT; break;
			case "R": r.dir = Rule.RIGHT; break;
			case "S": r.dir = Rule.STOP; break;
			}
		}
    	}
    	return r;
	}
	
    public void repaintTable(int currentRuleIndex) {
    	dtcr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,int row, int column) {
            	 if (row % 2 == 0) {
                     setBackground(Color.white);
                 }
                 else if (row % 2 == 1) {
                     setBackground(new Color(230, 230, 230));
                 }
                 if(row == currentRuleIndex && currentRuleIndex >= 0 ) {
                	 setBackground(new Color(255,255,0));
                 }
                 return super.getTableCellRendererComponent(table, value,
                         isSelected, hasFocus, row, column);
            }
        };
        dtcr.setHorizontalAlignment(JLabel.CENTER);
        this.setDefaultRenderer(Object.class,dtcr);
    	this.clearSelection();
    	this.setShowGrid(false);
//    	this.repaint();
    }
    
    void setBreakPoint(int r) {
    	setValueAt("*", r, 0);
    	repaint();
    }
    
    void clearBreakPoint(int r) {
    	setValueAt("", r, 0);
    	repaint();
    }

}


import java.awt.event.*;

public class TM {
    public static void main(String[] args) {

        
        System.setProperty("apple.laf.useScreenMenuBar", "true");

	TMUI program = new TMUI("Turing Machine");
	program.addWindowListener(new WindowAdapter() {
	    public void windowClosed(WindowEvent e) {
		System.exit(0);
	    }
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	});
	program.setVisible(true);
	program.setUI();
    }
}

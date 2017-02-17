package vision;

import javax.swing.JFrame;


public class Main {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("contours");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainLoop screen = new MainLoop();
		frame.add(screen);
		frame.setSize(800,640);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}

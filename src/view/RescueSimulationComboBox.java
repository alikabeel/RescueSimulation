package view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class RescueSimulationComboBox extends JComboBox {
	public boolean first = true;

	public RescueSimulationComboBox() {
		//this.setSelectedIndex(0);
	}
	//This was the way we removed the Arrow Buttons and credits goes to StackOverFlow Answers for this part
	 @Override public void updateUI() {
		    super.updateUI();
		    setUI(new BasicComboBoxUI() {
		      @Override protected JButton createArrowButton() {
		        return new JButton() {
		          @Override public int getWidth() {
		            return 0;
		          }
		        };
		      }
		    });
		    setBorder(BorderFactory.createLineBorder(Color.GRAY));
		  }

//	 public void addItem(Object x)
//	 {
//		 this.first = false;
//		 super.addItem(x);
//	 }
}

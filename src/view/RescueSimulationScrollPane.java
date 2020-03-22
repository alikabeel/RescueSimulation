package view;

import java.awt.TextArea;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RescueSimulationScrollPane extends JScrollPane  {
private JTextArea info;
	public String getInfo() {
	return info.getText();
}
public void setInfo(String s) {
	this.info.setText(s);
}
public void addInfo(String s) {
	this.info.setText(this.info.getText()+s);
}
	
	public RescueSimulationScrollPane() {
info=new JTextArea("");
info.setEditable(false);
this.setViewportView(info);

	}
	

}

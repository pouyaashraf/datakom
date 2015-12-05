package datakom;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class EditWindow extends JFrame implements WindowListener, MouseListener, ActionListener  {
    
    private EditPane lockView;
    private JTextArea editView;
    private JPanel middle;
    private JLabel status;
    private node nod;
    int offset, length;
    
    public EditWindow(node network) throws HeadlessException {
	super();
	
	addWindowListener(this);
	
	nod = network;
	lockView = new EditPane();
	lockView.setEditable(false);
	lockView.addMouseListener(this);
	editView = new JTextArea(30, 60);
	
	setLayout(new BorderLayout());
	middle = new JPanel();
	status = new JLabel(" ");
	
	//JLabel blank = new JLabel("");
	middle.setLayout(new GridLayout(2, 3));
	middle.add(new JLabel(""));
	middle.add(lockView);
	middle.add(new JLabel(""));
	
	JButton b = new JButton("Cancel");
	b.addActionListener(this);
	middle.add(b);
	middle.add(editView);
	b = new JButton("Submit");
	b.addActionListener(this);
	middle.add(b);
	
	
	add(middle, BorderLayout.CENTER);
	add(status, BorderLayout.SOUTH);
	
	setSize(600, 350);
	// TODO Auto-generated constructor stub
    }
    
    public void setStatus(String txt) {
	status.setText(txt);
    }
    
    @Override
	public void windowActivated(WindowEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void windowClosed(WindowEvent e) {
	// TODO Auto-generated method stub
	System.out.println("Window closed");
    }
    
    @Override
	public void windowClosing(WindowEvent e) {
	// TODO Auto-generated method stub
	System.out.println("Window closing ...");
	System.exit(0);
    }
    
    @Override
	public void windowDeactivated(WindowEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void windowDeiconified(WindowEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void windowIconified(WindowEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void windowOpened(WindowEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void mouseClicked(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void mouseReleased(MouseEvent e) {
	if(e.getButton() == MouseEvent.BUTTON1) {
	    offset = lockView.getSelectionStart();
	    length = lockView.getSelectionEnd() - offset;
	    if(length == 0) {
		return;
	    }
	    status.setText("Requesting lock ...");
	    try {
		nod.requestLock(offset, length);
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		status.setText("I/O error");
		e1.printStackTrace();
		return;
	    }
	    status.setText("Lock aquired");
	    editView.setText(lockView.getSelectedText());
	    System.out.println("off=" + offset + ", len=" + length + ", txt='" + lockView.getSelectedText() + "'");
	}
	// TODO Auto-generated method stub
	
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
	String s = e.getActionCommand();
	//System.out.println("Action: " + s);
	if(s.equals("Cancel")) {
	    //System.out.println("Cancel");
	    editView.setText("");
	}
	
	if(s.equals("Submit")) {
	    String str = editView.getText();
	    try {
		nod.requestChange(str, offset, str.length());
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	}
	// TODO Auto-generated method stub
	
    }
    
    public void updateText(String str, int first, int last) {
	lockView.replaceRange(str, first, last);
    }	
}

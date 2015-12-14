package datakom;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.SocketAddress;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class EditWindow extends JFrame implements WindowListener, MouseListener, ActionListener  {
    
    private EditPane lockView;
    private JTextArea editView;
    private JPanel middle;
    private JLabel status;
    private JButton cancel, submit;
    private List nodes, locks;
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
		
		nodes = new List();
		nodes.add("List of nodes:");
		middle.add(nodes);
		middle.add(lockView);
		locks = new List();
		locks.add("List of locks:");
		middle.add(locks);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setEnabled(false);
		middle.add(cancel);
		middle.add(editView);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		submit.setEnabled(false);
		middle.add(submit);
		
		
		add(middle, BorderLayout.CENTER);
		add(status, BorderLayout.SOUTH);
		
		setSize(600, 350);
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
    	System.out.println("Window closed");
    }
    
    @Override
	public void windowClosing(WindowEvent e) {
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
				status.setText("I/O error");
				e1.printStackTrace();
				return;
		    }
		    status.setText("Lock aquired");
		    editView.setText(lockView.getSelectedText());
		    cancel.setEnabled(true);
		    submit.setEnabled(true);
		    //System.out.println("off=" + offset + ", len=" + length + ", txt='" + lockView.getSelectedText() + "'");
		}
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		//System.out.println("Action: " + s);
		if(s.equals("Cancel")) {
		    //System.out.println("Cancel");
		    editView.setText("");
		    cancel.setEnabled(false);
		    submit.setEnabled(false);
		}
		
		if(s.equals("Submit")) {
		    String str = editView.getText();
		    try {
		    	nod.requestChange(str, offset, str.length());
		    } catch (IOException e1) {
		    	e1.printStackTrace();
		    }
		    cancel.setEnabled(false);
		    submit.setEnabled(false);
		}
    }
    
    public void updateText(String str, int first, int last) {
    	lockView.replaceRange(str, first, last);
    }
    
    public void addNode(SocketAddress sock) {
    	nodes.add(sock.toString());
    }
    
    public void addLock(LockList l) {
    	locks.add("" + l.getOffset() + ":" + l.getLength());
    }
}

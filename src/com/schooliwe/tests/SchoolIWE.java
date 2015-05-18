package com.schooliwe.tests;

/**
 * This application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.schooliwe.action.ActionObjectManager;
import com.schooliwe.agenda.DailyAgenda;
import com.schooliwe.conf.ConfManager;
import com.schooliwe.tree.TopicTree;

@SuppressWarnings("serial")
public class SchoolIWE extends JPanel implements ComponentListener, WindowListener {

	private static final Logger logger = Logger.getLogger(SchoolIWE.class);

   //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";
    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    private static JFrame frame = null; 
    
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;

    public SchoolIWE() {
    	
        super(new GridLayout(1,0));

        //Create a tree that allows one selection at a time.
        if (playWithLineStyle) {
            logger.info("line style = " + lineStyle);
            TopicTree.getInstance(splitPane).putClientProperty("JTree.lineStyle", lineStyle);
        }

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(TopicTree.getInstance(splitPane));

        //Add the scroll panes to a split pane.
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(ActionObjectManager.getInstance().getActionObject("timetable"));
        splitPane.setOneTouchExpandable(true);

        treeView.setMinimumSize(ActionObjectManager.minimumSize);
        splitPane.setDividerLocation(200); 
        splitPane.setPreferredSize(new Dimension(1000, 700));

        //Add the split pane to this panel.
        add(splitPane);
    }

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(SplashScreen splash) {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                UIManager.setLookAndFeel(com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName());
//                UIManager.setLookAndFeel(com.sun.java.swing.plaf.motif.MotifLookAndFeel.class.getName());
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } catch (Exception e) {
                logger.error("Couldn't use system look and feel.",e);
            }
        }

        //Create and set up the window.
        if (frame == null) {
        	frame = new JFrame("SchoolIWE");
        	Toolkit kit = Toolkit.getDefaultToolkit();
        	frame.setIconImage(kit.createImage("images/school.png"));
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        SchoolIWE topicTreeDemo = new SchoolIWE(); 
        frame.add(topicTreeDemo);
        frame.addComponentListener(topicTreeDemo);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	frame.addWindowListener(topicTreeDemo);
        
        //Display the window.
        frame.pack();
        splash.close();
        frame.setVisible(true);
    }

    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"messages", "resources", "drivers","softwares","actions"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(315,270,200,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading "+comps[(frame/5)%5]+"...", 315, 280);
    }

    public static void main(String[] args) {
    	// Set log4j configuration
    	File logDir = new File("log");
    	if (!logDir.exists())
    		logDir.mkdir();
    	DOMConfigurator.configure("conf/log4j.xml");
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
    	logger.info("STARTING...");
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            logger.error("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            logger.error("g is null");
            return;
        }
        for(int i=0; i<100; i++) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(10);
            }
            catch(InterruptedException e) {
            }
        }
//        splash.close();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	System.setProperty("path.separator", "/");
                createAndShowGUI(splash);
            }
        });
    }

//	@Override
	public void componentHidden(ComponentEvent e) {
	}

//	@Override
	public void componentMoved(ComponentEvent e) {
	}

//	@Override
	public void componentResized(ComponentEvent e) {
		if (splitPane.getBottomComponent()==ActionObjectManager.getInstance().getActionObject("agenda")) {
			DailyAgenda agenda = (DailyAgenda)ActionObjectManager.getInstance().getActionObject("agenda");
			agenda.resizePanels();
		}
	}

//	@Override
	public void componentShown(ComponentEvent e) {
	}
	
	public static JFrame getMainFrame() {
		return frame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int resp = JOptionPane.showConfirmDialog(SchoolIWE.getMainFrame(), 
				ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "realyExit"),
				ConfManager.getMessage(ConfManager.MESSAGES_FILE_ID, "confirm"),
				JOptionPane.YES_NO_OPTION);
		if (resp==0) {
			SchoolIWE.getMainFrame().dispose();
			System.exit(0);
		}
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
    
}

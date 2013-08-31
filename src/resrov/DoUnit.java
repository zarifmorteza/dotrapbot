package resrov;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JFrame;

import com.sun.jna.Pointer;

public class DoUnit extends JFrame {
	/**
	 * Unit1 - TrapDoor Java Test
	 */
	private static final long serialVersionUID = 1L;
	Image image = null;
	JPanel cards = null, paintBox = null;
	String server = null;
    final JButton btnLogin = new JButton("Login");
    final JButton btnStart = new JButton("Start");
    final JTextField txtUser = new JTextField("");
    final JPasswordField txtPass = new JPasswordField(""); 
	static int currentTodo = 0;
	int sessionId, formWidth = (int) (820*1.25), formHeight = (int) (600*1.25);
	
	@SuppressWarnings("serial")
	public DoUnit() throws Exception {
		this.setUndecorated(true);		
		this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		this.setLocationRelativeTo(null);
		this.setSize(formWidth/2 + 30, formHeight/2);
		//this.setResizable(false);
		//JFrame.setDefaultLookAndFeelDecorated(false);
		this.setTitle("[TrapDoor] DarkOrbit PixelBot");
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);	
        
        paintBox = new JPanel () {
        	@Override public void paintComponent(Graphics g) {
        	    int w = getSize().width;
        	    int h = getSize().height;
        	   
        	    if (updateImage())
        	    	g.drawImage(image, 0, 10, w, h, this);
        	    else
        	    	g.drawString("TrapDoor is loading...", 5, 15);
            }
        };
        paintBox.setSize(formWidth, formHeight);
        
        addComponentToPane(getContentPane());
        System.out.println("Bot window initiated.");
        System.out.println("Connecting to TrapDoor server...");
        if (init())
        	btnLogin.setEnabled(true);
        //pack();
	} 	
	public void addComponentToPane(Container pane) {
        JPanel comboBoxPane = new JPanel();
        String comboBoxItems[] = { "Control", "View" };
        JComboBox<String> cb = new JComboBox<String>(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                CardLayout cl = (CardLayout)(cards.getLayout());
                cl.show(cards, (String)evt.getItem());
            }
        });
        comboBoxPane.add(cb);        
        
        btnLogin.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				currentTodo = 1;									
				btnLogin.setEnabled(false);
			}
        });     
        btnStart.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				currentTodo = 2;
			}
        });   
        btnStart.setEnabled(false);
        btnLogin.setEnabled(false);      
      
        txtUser.setHorizontalAlignment(JTextField.CENTER);
        txtPass.setHorizontalAlignment(JTextField.CENTER);
        JPanel btnPanel = new JPanel(new GridLayout(2,2));
        btnPanel.add(btnLogin);
        btnPanel.add(btnStart);      
        btnPanel.add(txtUser);
        btnPanel.add(txtPass);
        
        JPanel card1 = new JPanel(new BorderLayout(3, 3));    
        card1.add(btnPanel, BorderLayout.BEFORE_FIRST_LINE);
        JTextArea textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setEditable(false);
        System.setOut(new PrintStream(new TextAreaOutputStream(textArea)));
        card1.add(textArea, BorderLayout.CENTER);
         
        cards = new JPanel(new CardLayout());       
        cards.add(card1, "Control");
        cards.add(paintBox, "View");
         
        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }
	private boolean init() {
		sessionId = Trapi.trapi_session_start();
		if (sessionId > 0)
		{
			Trapi.trapi_set_size(sessionId, formWidth, formHeight);
			System.out.println("TrapDoor loaded.");
            Trapi.trapi_load_image(sessionId, "res\\minimap.bmp", "MINIMAP");
            Trapi.trapi_load_image(sessionId, "res\\minimap_top_left.bmp", "MM_T_L");
            Trapi.trapi_load_image(sessionId, "res\\minimap_bottom_right.bmp", "MM_B_R");
            Trapi.trapi_load_image(sessionId, "res\\bbox.bmp", "BBOX");
            Trapi.trapi_load_image(sessionId, "res\\reconnect.bmp", "RECONNECT");
            System.out.println("Resources loaded.");
		} else 
			System.out.println("TrapDoor failed to load.");
		return sessionId > 0;
	}
	private Point parseLocation(String str) {
		 String[] vst = str.split("\\|");
		 if (vst.length == 0 || vst[0].charAt(0) == '0')
		    return null;
		 return new Point(Integer.parseInt(vst[1]), Integer.parseInt(vst[2]));		 
	}
	private void mouseClick(Point loc) {
		Trapi.trapi_mouse_move(sessionId, loc.x, loc.y);
		Trapi.trapi_mouse_down(sessionId, loc.x, loc.y);
		Trapi.trapi_mouse_up(sessionId, loc.x, loc.y);
	}
	private boolean randomMove(Rectangle mm) {
		int x = (int) (mm.x + (Math.random() * (mm.width))), 
				y = (int) (mm.y + (Math.random() * (mm.height)));
		mouseClick(new Point(x, y));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) { }
		return true;
	}
	private boolean start() throws InterruptedException {
		String result = null;
		Point loc = null;
		/*Trapi.trapi_type_key(sessionId, 'h');
		Thread.sleep(100);
		
		result = Trapi.trapi_image_search(sessionId, "MINIMAP", 5, true);
		Point loc = parseLocation(result);
		if (loc == null) {
			System.out.println("Minimap ICON could not be found.");
			return false;
		}
		mouseClick(loc);
		Thread.sleep(100);	*/	
		
		Rectangle minimap;
		Point lt, rb;
		result = Trapi.trapi_image_search(sessionId, "MM_T_L", 15, true);
		lt = parseLocation(result);
		result = Trapi.trapi_image_search(sessionId, "MM_B_R", 15, true); 
		rb = parseLocation(result);
		if (lt == null || rb == null) {
			System.out.println("Minimap top/left, bottom/right could not be found.");
			return false;
		}
		minimap = new Rectangle(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
		
				
		System.out.println("Looking for bonusboxes...");
		
	    short j = 0, k = 0, r = 0;
	    while (true) {
	    	Thread.sleep(150);
	    	
	        if (r++ >= 500)
	        { 
	        	r = 0;
	        	Trapi.trapi_image_save(sessionId, "ScreenShot.bmp");
	        	result = Trapi.trapi_image_search(sessionId, "RECONNECT", 15, true);
	        	loc = parseLocation(result);
	        	if (loc != null) {
	        		System.out.println("Reconnect window recognized.");
	        		System.out.println("Reloading spacemap...");
	        		Trapi.trapi_get(sessionId, "http://" + server + ".darkorbit.bigpoint.com/indexInternal.es?action=internalMapRevolution", "");
	        		return false;
	        	}
	        }	
	    	
	        result = Trapi.trapi_image_search(sessionId, "BBOX", 20, true);
	        loc = parseLocation(result);
	        if (loc == null)
	        {
	        	k = 0;
	        	if (++j >= 35) {
	        		randomMove(minimap); 	        	
	        		j = 0;  
	        	}
	        	continue;
	        }
	        if (k++ == 0) {
	        	loc.y += 5;
	        	loc.x += 3;
	        	mouseClick(loc);
	        } else
	        	if (k >= 8)
	        		k = 0;       	        
	    }		
	}
	private boolean loginProc(String user, String pass) {
        System.out.println("Logging in user " + user + " on the last chosen server.");
		String src = Trapi.trapi_get(sessionId, "http://www.darkorbit.com/", "");
		/*try {
			FileWriter fw = new FileWriter(new File("LOG.txt"));
			fw.write(src);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} //*/
		Pattern p = Pattern.compile(".*bgcdw_login_form\"? action=\"?([^\" >]*).*", Pattern.MULTILINE);
    	Matcher matcher = p.matcher(src);
    	if (matcher.find()) {
    		String url = matcher.group(1).replaceAll("&amp;", "&");
    		src = Trapi.trapi_get(sessionId, url, "username=" + user + "&password=" + pass);
    		Pattern srvp = Pattern.compile("([a-z0-9]*)\\.darkorbit\\.bigpoint\\.com", Pattern.MULTILINE);
        	Matcher mp = srvp.matcher(src);
        	if (mp.find()) 
        	{
        		server = mp.group(1);
        		System.out.println("Found server: " + server);
        		Trapi.trapi_get(sessionId, "http://" + server + ".darkorbit.bigpoint.com/indexInternal.es?action=internalMapRevolution", "");
    			//Trapi.trapi_dll_cleanup();
    			System.out.println("Login successful.");
    			System.out.println("Loading spacemap...");
    			return true;
        	} else 
        		System.out.println("Server not found.");
    		return false;
    	}
    	System.out.println("Login failed.");
		return false;
	}
    private boolean updateImage() {
        try {
        	if (sessionId <= 0)
        		return false;
        	Pointer ptr = Trapi.trapi_screenshot(sessionId);
        	String str = ptr.getString(0);
        	int size = Integer.parseInt(str);
        	byte[] ba = ptr.getByteArray(str.length() + 1, size);
        	InputStream bais = new ByteArrayInputStream(ba);
        	image = ImageIO.read(bais);
        	//ImageIO.write((RenderedImage) image, "JPEG", new File("Output.jpg"));
        	Trapi.trapi_dll_cleanup();
        	return image != null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
	public static void main(String[] args) {
		try {
			final DoUnit unit = new DoUnit(); 
			Timer timer = new Timer(150, new ActionListener() {
			    public void actionPerformed(ActionEvent e) { unit.repaint(); } } );
			timer.start();
			
			while (true)
			{
				Thread.sleep(100);
				switch (DoUnit.currentTodo) {
				case 1:
					if (!unit.loginProc(URLEncoder.encode(unit.txtUser.getText(), "UTF-8"), URLEncoder.encode(unit.txtPass.getDocument().getText(0, unit.txtPass.getDocument().getLength()), "UTF-8")))
						unit.btnLogin.setEnabled(true);
					else
						unit.btnStart.setEnabled(true);
					break;
				case 2:
					if (!unit.start())
						unit.btnStart.setEnabled(true);
					break;
				default:
					break;
				}
				currentTodo = 0;
			}
			
		} catch (Exception e) {
		}
	}

}

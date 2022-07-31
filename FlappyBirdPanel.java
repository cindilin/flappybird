
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.Random;
import java.util.ArrayList;
import java.awt.image.BufferedImage;


public class FlappyBirdPanel extends JPanel implements ActionListener {

    // view
    private static final int WIDE = 640;                   // the width of the panel
    private static final int HIGH = 480;                   // the height of the panel
    private BufferedImage background;                      // the background of the panel 
    private int frameCount;                                // how many times refresh the viewer
    private JButton startButton;                           // the button to start the game
    private ImageIcon bulidingImageIcon;                   // the image of buildings 
    private ImageIcon ceilingImageIcon;                    // the image of top ceiling
    private ImageIcon birdImageIcon;                       // the image of bird
    private ImageIcon landImageIcon;                       // the image of land
    private ImageIcon pipeImageIcon;                       // the image of pipes

    // model
    private Bird bird;                                      // a bird
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();  // a list of pipes
    private boolean crash = false;                          // whether the bird hits a pipe
    private int score;                                      // the score 

    // control
    private final Timer timer = new Timer(20, this);        // do someyhing every 20 miliseconds
    private boolean paused = true;                          // whether the game is paused


    /*********************************
     *
     * constructor
     *
     *********************************/
    public FlappyBirdPanel() {

        super(true);
	this.frameCount = 100;                               // speed up showing the first pipe                               
	this.score = 0;                                      // initial score
        this.setOpaque(false);                               // some pixels may be outof its bound 
        this.setPreferredSize(new Dimension(WIDE, HIGH));    // initial dimension
        this.addMouseListener(new MouseHandler(this));       // listen to the mouse events
        this.addComponentListener(new ComponentHandler());   // listen to the events from the timer 

	// create the start button
        startButton = new JButton("Start");                  
        startButton.setSize(110, 55);
        startButton.setLocation(WIDTH/2-50, HIGH/2-50);
	startButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    paused = false;                                           
		    startButton.setVisible(false);         
		}
	    });      
        this.add(startButton);

    }

    /*********************************
     *
     * create a JFrame with the menu and add itself into the frame. 
     *
     *********************************/
    private void create() {

        JFrame f = new JFrame("Flappy Bird");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// add a menubar
	JMenuBar menuBar = new JMenuBar();

	// add the menu File 
	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	// add the menu item "New Game" into the File menu
	JMenuItem newMenuItem = new JMenuItem("New Game");
	newMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		pipes.clear();                       // delete all pipes
		bird = new Bird(80, HIGH/2);         // initial the bird
		crash = false;                       
		paused = true;
		score = 0;
		frameCount = 100;
		f.repaint();
		startButton.setVisible(true);
            }
	});
	fileMenu.add(newMenuItem);

	// add the menu item "Exit" into the File menu
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	exitMenuItem.addActionListener(new ActionListener() {
            @Override
	    public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
	});
	fileMenu.add(exitMenuItem);

	f.setJMenuBar(menuBar);

	try {
	    // preload the images
	    File file = new File("img/sky.png");
	    bulidingImageIcon = new ImageIcon(ImageIO.read(file));
	   
	    file = new File("img/ceiling.png");
	    ceilingImageIcon = new ImageIcon(ImageIO.read(file));

	    file = new File("img/bird.png");
	    birdImageIcon = new ImageIcon(ImageIO.read(file));

	    file = new File("img/land.png");
	    landImageIcon = new ImageIcon(ImageIO.read(file));

	    file = new File("img/pipe.png");
	    pipeImageIcon = new ImageIcon(ImageIO.read(file));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	// initialize the bird
	bird = new Bird(80, HIGH/2);

	// make and show the frame
        f.add(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

	// start the timer
        timer.start();
    }


    // paint the panel based on the model of the bird and pipes
    protected void paintComponent(Graphics g) {

	// paint background
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();

	// paint the background color
	g.setColor(new Color(78, 192, 202));
	g.fillRect(0, 0, w, h);

	// paint the land
	for (int i=0; i<1+w/335; i++) {
	    g.drawImage(landImageIcon.getImage(), i*335, h-50, this);
	}

	// paint the building
	for (int i=0; i<1+w/276; i++) {
	    g.drawImage(bulidingImageIcon.getImage(), i*276, h-150, this); 
	}

	// paint the ceiling
	for (int i=0; i<1+w/64; i++) {
	    g.drawImage(ceilingImageIcon.getImage(), i*64, 40, this);
	}

	// check whether crashed
	if (!crash) {
	    for (Pipe pipe : pipes) {
		if (bird.getX() <= pipe.getX()  && pipe.getX() <= bird.getX() + 33) {
		    if (bird.getY() < pipe.getOpenY() || bird.getY() >= pipe.getOpenY()+110) {
			crash = true;
			break;
		    }
		} else if (bird.getX() <= pipe.getX() + 30  && pipe.getX() + 30 <= bird.getX() + 33) {
                    if (bird.getY() < pipe.getOpenY() || bird.getY() >= pipe.getOpenY()+110) {
                        crash = true;
                        break;
                    }
                } 
	    }
	}

	// move the bird
	if (!paused) {
	    if (bird.getY() < h-25-40) { 
		if (crash) {
		    bird.drop();
		} else {
		    bird.down();
		}
	    } else {
		crash = true;
	    }
	}

	// paint the bird
	g.drawImage(birdImageIcon.getImage(), bird.getX(), bird.getY(), this);

        if (paused) {
	    // don't move pipes
	    return;
	}

	// check whether a pipe should be added
	if (frameCount > 10 && frameCount % 70 == 0 ) {
	    Pipe pipe = new Pipe(w,  (int) (Math.random() * (h - 200 - 150)) + 150);
	    pipes.add(pipe);
	}

	// paint the pipes
	for (Pipe pipe : pipes) {
	    if (!crash) pipe.move();
	    if (pipe.getX() < 0) continue;

	    // paint the two parts of the pipe
	    Image img = pipeImageIcon.getImage();
	    Image newimg = img.getScaledInstance(30, pipe.getOpenY()-50,  java.awt.Image.SCALE_SMOOTH);
	    g.drawImage(newimg, pipe.getX(), 56, 30, pipe.getOpenY()-50, null);
	    g.drawImage(newimg, pipe.getX(), pipe.getOpenY()+110, 30, h-(57 + pipe.getOpenY()+135)+40, null);
	}

        // paint the score
	if (!crash) {
	    frameCount++;
	    score = score + 10;
	}
	g.setColor(Color.white);
	g.drawString("Score: "+score, 5, 16);

    }

    // what to do when get a timer event
    public void actionPerformed(ActionEvent e) {
        this.repaint();
    }

    // check if the bird crashed into a pipe
    public boolean isCrashed() {
	return this.crash;
    }

    // check if the game is paused
    public boolean isPaused() {
	return this.paused;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
	    public void run() {
                new FlappyBirdPanel().create();
            }
	});
    }

    
    /***********************************************
     *
     * a private class for handling mouse clicking
     *
     ************************************************/
    private class MouseHandler extends MouseAdapter {

	private FlappyBirdPanel panel;
	
	// constructor
	public MouseHandler(FlappyBirdPanel panel) {
	    this.panel = panel;
	}

        @Override
	public void mousePressed(MouseEvent e) {
	    if (paused) {
		return;
	    }

            super.mousePressed(e);	  
	    if (!panel.isCrashed()) {
		if (bird.getY() < panel.getHeight() - 25 - 50) {
		    // move 50 pixels up
		    for (int i=0; i<50; i++) {
			bird.up();
		    }
		}
	    } 

        }
    }


    // inital action
    private class ComponentHandler extends ComponentAdapter {

        private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        private final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        private final Random r = new Random();

	public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            int w = getWidth();
            int h = getHeight();
            background = gc.createCompatibleImage(w, h, Transparency.OPAQUE);
            Graphics2D g = background.createGraphics();
            g.clearRect(0, 0, w, h);

            g.setColor(Color.RED);
	    g.fillRect(0, 0, w, h);
            g.dispose();
        }
    }
}

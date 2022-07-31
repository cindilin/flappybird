

public class Pipe {

    private static double WIDTH;  // the width of the pipe

    private int x;
    private int openY;

    public Pipe(int x, int openY) {
	this.x = x;
	this.openY = openY;
    }

    public int getX() {
	return this.x;
    }

    public int getOpenY() {
	return this.openY;
    }

    public void move() {
	this.x--;
	this.x--;
    }
    
}
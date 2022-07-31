

public class Bird {

    private int x;
    private int y;

    public Bird(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public int getX() {
	return this.x;
    }

    public int getY() {
	return this.y;
    }

    public void down() {
	this.y++;
    }

    public void drop() {
	this.y = this.y+20;
    }

    public void up() {
	if (y> 50)
	    this.y--;
    }
    
}
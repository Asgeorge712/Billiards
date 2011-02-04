package bounce;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Pocket {
	public String name = "";
	public Color color;
	public int size;
	public int x;
	public int y;
	public boolean active;

	public static final int STANDARD_SIZE = 48;
	public static final Color STANDARD_COLOR = Color.black;

	public Pocket(String name, Color color, int x, int y, int size) {
		this.active = true;
		this.color = color;
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public void toggleActive() { this.active = !this.active;}
}
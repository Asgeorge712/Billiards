package Billiards;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Circle {
	public String name = "";
	public Color color;
	public int size;

	public double x;
	public double y;
	public double dx;
	public double dy;
	public double ddx;
	public double ddy;
	public boolean canMove = false;
	public boolean beingDragged = false;


	/**************************************************************
	*
	*
	*
	***************************************************************/
	public Circle(String name, Color color, double x, double y, int speed, double direction, int size) {
		this.name = name;
		this.color = color;
		this.size = size;
		this.x = x;
		this.y = y;

		System.out.println("I created the " + name );
		//System.out.println("dx = " + dx + ", dy =" + dy);
		//System.out.println("ddx = " + ddx + ", ddy =" + ddy);
	}

	/**************************************************************
	*
	*
	*
	***************************************************************/
	public String toString() {
		String info = "Name: " + name;
		info += "\nx: " + x;
		info += "\ny: " + y;
		info += "\ndx: " + dx;
		info += "\ndy: " + dy;
		info += "\nddx: " + ddx;
		info += "\nddy: " + ddy;
		return info;
	}
}
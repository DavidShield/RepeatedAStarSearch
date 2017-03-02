import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

public class Maze extends JFrame {
	
	//0.Variables
	//
	public int x_length;
	public int y_length;
	public BinaryMinHeap openList;
	Cell[][] cells;
	public Random random = new Random();
	Stack<Cell> pathlist = new Stack<>();
	public JPanel grid;
	public long startTime;
	public long endTime;
	public long elapsedTime;
	Cell start;
	//1 Initialization
	//1.1 Constructor
	public Maze(int x, int y) {
		this.x_length = x;
		this.y_length = y;
	}
	
	//1.2 Initialization
	public void initial() {
		cells = new Cell[x_length][y_length];
		
		//
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				cells[i][j] = new Cell(i, j);
			}
		}
		
		cells[x_length - 1][y_length - 1].visited = true;
		openList = new BinaryMinHeap();
		start = cells[0][0];
	}
	
	//1.3 Set all cells as unvisited after generating the maze
	public void setVisited() {
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				cells[i][j].visited = false;
			}
		}
	}
	
	//1.4
	public void clearPath() {
		openList.removeAll();
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				cells[i][j].inPath = false;
				cells[i][j].parent = null;
				cells[i][j].visited = false;
			}
		}
	}
	
	
	//2
	//2. Get cell by x and y coordinates
	public Cell getCell(int x, int y) {
		try {
			return cells[x][y];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	//2. Generate all the cells in maze
	public void generate() {
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				if (cells[i][j].visited == false && cells[i][j].blocked == false) {
					gen_path(cells[i][j]);
				}
			}
		}
	}
	
	//2. Generate path by traveling through all the maze
	private void gen_path(Cell start){
		pathlist.push(start);
		while (!pathlist.isEmpty()){			
			searchNeighbor(pathlist.peek());
		}
	}
	
	//2. Search all the neighbours of current cell
	//if a neighbor is neither blocked nor visited, then it 
	//can be visited(add to "pathlist" for next move)
	//This function is used in generate path
	private void searchNeighbor(Cell current){
		current.visited = true;
		ArrayList<Cell> valid_neigh = new ArrayList<>();
			
		//Add potential neighbors here
		if(getCell(current.x,current.y - 1) != null && getCell(current.x,current.y - 1).visited == false
				&& getCell(current.x,current.y - 1).blocked == false)
		valid_neigh.add(getCell(current.x,current.y - 1));		
		if(getCell(current.x,current.y + 1) != null && getCell(current.x,current.y + 1).visited == false
				&& getCell(current.x,current.y + 1).blocked == false)
		valid_neigh.add(getCell(current.x,current.y+1));
		if(getCell(current.x - 1,current.y) != null && getCell(current.x - 1,current.y).visited == false
				&&getCell(current.x - 1,current.y).blocked == false)
		valid_neigh.add(getCell(current.x - 1,current.y));
		if(getCell(current.x + 1,current.y) != null && getCell(current.x + 1,current.y).visited == false
				&&getCell(current.x + 1,current.y).blocked == false)
		valid_neigh.add(getCell(current.x + 1,current.y));

		if(valid_neigh.size() > 0) {
			int ran = random.nextInt(valid_neigh.size());	//random number to decide which one to access
			Cell choose = valid_neigh.get(ran);
				if (random.nextInt(10) >= 2) {				//0.7 set it as unblocked
					pathlist.push(choose);					//start from "choose" point
				}
				else {
					choose.blocked = true;
				}			
			}
		else pathlist.pop();
	}	
	
	//3 Search
	//3. Run forward search algorithm
	public void forwardSearch() {
		setVisited();
		startTime = System.nanoTime();
		findPath(cells[0][0]);
		endTime = System.nanoTime();
		elapsedTime = endTime - startTime;
		System.out.println("Running time of Forward Search:" + elapsedTime + "ns");
	}	
	
	//3. "openlist" is the potential path, 
	public void findPath(Cell cell) {
		cell.visited = true;
		cell.g = 0;
		cell.h = x_length + y_length;
		cell.f = cell.g + cell.h;
		openList.add(cell);
		
		while (openList.size() != 0) {
			calculateNeighbours(openList.remove());
			if (openList.size() != 0) {
				if (openList.peek() == cells[x_length - 1][y_length - 1]) {
					System.out.println("Reach Target by Forward Search!");
					setPath();
					return;
				}
			}
		}
		System.out.println("Can't Reach!");
	}
	
	//3.
	public void forwardRepeatedSearch() {
		setVisited();
		findRepeatedPath(cells[0][0]);
	}	
	
	//3.
	public void findRepeatedPath(Cell cell) {
		//cell.inPath = true;
		cell.g = 0;
		cell.h = x_length + y_length;
		cell.f = cell.g + cell.h;
		openList.add(cell);
		
		while (openList.size() != 0) {
			Cell element = openList.remove();
			element.inPath = true;
			calculateRepeatedNeighbours(element);
			if (openList.size() != 0) {
				if (openList.peek() == cells[x_length - 1][y_length - 1]) {
					System.out.println("Reach Target by Forward Repeated Search!");
					setPath();
					return;
				}
			}
		}
		System.out.println("Can't Reach!");
	}	
	
	//3.
	public void calculateRepeatedNeighbours(Cell cell) {
		//cell.visited = true;
		ArrayList<Cell> neighbours = new ArrayList<>();
		int x = cell.x, y = cell.y;		
		for (int i = 0; i < openList.size(); i++) {
			openList.get(i).f += 2;
		}		
		if (getCell(x - 1, y) != null && getCell(x - 1, y).visited == false && getCell(x - 1, y).blocked == false)
		{
			neighbours.add(getCell(x - 1, y));
			getCell(x - 1, y).g = cell.g + 1;
			getCell(x - 1, y).h = calculateH(getCell(x - 1, y));
			getCell(x - 1, y).parent = cell;
		}
		if (getCell(x, y - 1) != null && getCell(x, y - 1).visited == false && getCell(x, y - 1).blocked == false)
		{
			neighbours.add(getCell(x, y - 1));
			getCell(x, y - 1).g = cell.g + 1;
			getCell(x, y - 1).h = calculateH(getCell(x, y - 1));
			getCell(x, y - 1).parent = cell;
		}
		if (getCell(x + 1, y) != null && getCell(x + 1, y).visited == false && getCell(x + 1, y).blocked == false)
		{
			neighbours.add(getCell(x + 1, y));
			getCell(x + 1, y).g = cell.g + 1;
			getCell(x + 1, y).h = calculateH(getCell(x + 1, y));
			getCell(x + 1, y).parent = cell;
		}
		if (getCell(x, y + 1) != null && getCell(x, y + 1).visited == false && getCell(x, y + 1).blocked == false)
		{
			neighbours.add(getCell(x, y + 1));
			getCell(x, y + 1).g = cell.g + 1;
			getCell(x, y + 1).h = calculateH(getCell(x, y + 1));
			getCell(x, y + 1).parent = cell;
		}
		if (neighbours.size() != 0) {
			for (int i = 0; i < neighbours.size(); i++) {
				neighbours.get(i).f = neighbours.get(i).g + neighbours.get(i).h;
				neighbours.get(i).visited = true;
				openList.add(neighbours.get(i));
			}
		}
	}
	//3.
	public void backwardSearch() {
		setVisited();
		startTime = System.nanoTime();
		findPathBackward(cells[x_length - 1][y_length - 1]);
		endTime = System.nanoTime();
		elapsedTime = endTime - startTime;
		System.out.println("Running time of Backward Search:" + elapsedTime + "ns");
	}
	
	//3.
	public void findPathBackward(Cell cell) {
		cell.visited = true;
		cell.g = 0;
		cell.h = (x_length - cell.x) + (y_length - cell.y);
		cell.f = cell.g + cell.h;
		openList.add(cell);
		
		try {
			while (openList.size() != 0) {
				calculateNeighbours(openList.remove());
				if (openList.size() != 0) {
					if (openList.peek() == cells[0][0]) {
						System.out.println("Reach Target by Backward Search!");
						setBackwardPath();
						return;
					}
				}
			}
		}
		catch (OutOfMemoryError e) {
			System.out.println("Can't Reach!");
		}
	}
	
	//3. Explore all neighbours, return a list that contains
	//all the legal(not null) neighbours.
	public ArrayList<Cell> explore(Cell currCell) {
		ArrayList<Cell> neighbours = new ArrayList<>();
		int x = currCell.x, y = currCell.y;
		
		neighbours.add(getCell(x - 1, y));
		neighbours.add(getCell(x, y - 1));
		neighbours.add(getCell(x + 1, y));
		neighbours.add(getCell(x, y + 1));
		
		for (int i = 0; i < neighbours.size(); i++) {
			if (neighbours.get(i) == null) neighbours.remove(i);	//remove does not deduct the size//wrong
		}
		
		return neighbours;
	}
	
	//3.
	public void calculateNeighbours(Cell cell) {
		cell.visited = true;
		ArrayList<Cell> neighbours = new ArrayList<>();
		int x = cell.x, y = cell.y;
		if (getCell(x - 1, y) != null && getCell(x - 1, y).visited == false && getCell(x - 1, y).blocked == false)
		{
			neighbours.add(getCell(x - 1, y));
			getCell(x - 1, y).g = cell.g + 1;
			getCell(x - 1, y).h = calculateH(getCell(x - 1, y));
			getCell(x - 1, y).parent = cell;
		}
		if (getCell(x, y - 1) != null && getCell(x, y - 1).visited == false && getCell(x, y - 1).blocked == false)
		{
			neighbours.add(getCell(x, y - 1));
			getCell(x, y - 1).g = cell.g + 1;
			getCell(x, y - 1).h = calculateH(getCell(x, y - 1));
			getCell(x, y - 1).parent = cell;
		}
		if (getCell(x + 1, y) != null && getCell(x + 1, y).visited == false && getCell(x + 1, y).blocked == false)
		{
			neighbours.add(getCell(x + 1, y));
			getCell(x + 1, y).g = cell.g + 1;
			getCell(x + 1, y).h = calculateH(getCell(x + 1, y));
			getCell(x + 1, y).parent = cell;
		}
		if (getCell(x, y + 1) != null && getCell(x, y + 1).visited == false && getCell(x, y + 1).blocked == false)
		{
			neighbours.add(getCell(x, y + 1));
			getCell(x, y + 1).g = cell.g + 1;
			getCell(x, y + 1).h = calculateH(getCell(x, y + 1));
			getCell(x, y + 1).parent = cell;
		}
		if (neighbours.size() != 0) {
			for (int i = 0; i < neighbours.size(); i++) {
				neighbours.get(i).f = neighbours.get(i).g + neighbours.get(i).h;
				openList.add(neighbours.get(i));
			}
		}
	}
	
	//3. Mark all cells in path as visited
	private void setPath() {
		Cell pointer = cells[x_length - 1][y_length - 1];
		while(pointer != cells[0][0]) {
			if (pointer == null) return;
			pointer.inPath = true;
			pointer = pointer.parent;
		}
	}
	
	//3. 
	private void setBackwardPath() {
		Cell pointer = cells[0][0];
		while (pointer != cells[x_length - 1][y_length - 1]) {
			if (pointer == null) return;
			pointer.inPath = true;
			pointer = pointer.parent;
		}
	}
	
	//3.
	public int calculateH(Cell cell) {
		return (x_length - cell.x) + (y_length - cell.y);
	}	

	//4 Draw
	//4. Draw the maze only
	public void drawMaze() {
		//final Maze that = this;
		if (grid != null) grid.removeAll();
		else grid = new JPanel();
		
		getContentPane().add(grid, BorderLayout.CENTER);
		grid.setLayout(new GridLayout(x_length, y_length));
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				JPanel draw = new JPanel();
				draw.setBorder(BorderFactory.createLineBorder(Color.black));
				Cell current = cells[i][j];
				if (current.blocked) draw.setBackground(Color.black);
				else draw.setBackground(Color.white);
				if (i == 0 && j == 0) draw.setBackground(Color.green);
				if (i == x_length - 1 && j == y_length - 1) draw.setBackground(Color.red);
				grid.add(draw);
			}
		}
		this.setContentPane(getContentPane());
		this.setVisible(true);
		this.setSize(800, 800);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	//4. Draw the path
	public void drawPath() {
		//final Maze that = this;
		if (grid != null) grid.removeAll();
		else grid = new JPanel();
		
		getContentPane().add(grid, BorderLayout.CENTER);
		grid.setLayout(new GridLayout(x_length, y_length));
		for (int i = 0; i < x_length; i++) {
			for (int j = 0; j < y_length; j++) {
				JPanel draw = new JPanel();
				draw.setBorder(BorderFactory.createLineBorder(Color.black));
				Cell current = cells[i][j];
				if (current.blocked) draw.setBackground(Color.black);
				else if (current.visited) draw.setBackground(Color.gray);
				else draw.setBackground(Color.white);
				if (current.inPath) draw.setBackground(Color.yellow);
				if (i == 0 && j == 0) draw.setBackground(Color.green);
				if (i == x_length - 1 && j == y_length - 1) draw.setBackground(Color.red);
				grid.add(draw);
			}
		}
		this.setContentPane(getContentPane());
		this.setVisible(true);
		this.setSize(800, 800);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
}

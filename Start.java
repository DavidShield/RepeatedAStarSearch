/******************************/
//Auther: Dawei Wang, Tianzhe Wang
//Date: Feb. 2017
//
/******************************/
public class Start {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Maze maze = new Maze(50, 50);
		maze.initial();
		maze.generate();
		maze.forwardRepeatedSearch();
		//maze.backwardSearch();
		//maze.drawMaze();
		maze.drawPath();
	}
}

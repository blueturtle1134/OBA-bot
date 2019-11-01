package oba.war.sudoku;

import java.util.Arrays;

public class SudokuState {
	private int[][] grid;
	private boolean[][][] possibles;
	
	private SudokuState() {
		
	}

	public int[][] getGrid() {
		return grid;
	}

	private void setGrid(int[][] grid) {
		this.grid = grid;
	}

	public boolean[][][] getPossibles() {
		return possibles;
	}

	private void setPossibles(boolean[][][] possibles) {
		this.possibles = possibles;
	}
	
	public String printCell(int x, int y) {
		if(grid[x][y]!=0){
			return grid[x][y]+"";
		}
		else {
			for(int i = 0; i<9; i++) {
				if(possibles[x][y][i]) {
					return "·";
				}
			}
			return "X";
		}
	}
	
	public String printGrid() {
		String result = "  123|456|789";
		for(int i = 0; i<9; i++) {
			if(i%3==0&&i>0) {
				result+="\n-----+---+---";
			}
			result += "\n"+(i+1)+" ";
			for(int j = 0; j<3; j++) {
				result += printCell(i,j);
			}
			result += "|";
			for(int j = 3; j<6; j++) {
				result += printCell(i,j);
			}
			result += "|";
			for(int j = 6; j<9; j++) {
				result += printCell(i,j);
			}
		}
		return result;
	}
	
	public static SudokuState getBlankState() {
		SudokuState state = new SudokuState();
		int[][] grid = new int[9][9];
		boolean[][][] possibles = new boolean[9][9][9];
		for(int i = 0; i<9; i++) {
			Arrays.fill(grid[i], 0);
			for(int j = 0; j<9; j++) {
				Arrays.fill(possibles[i][j], true);
			}
		}
		state.setGrid(grid);
		state.setPossibles(possibles);
		return state;
	}
	
	public boolean addValue(int x, int y, int value) {
		if(possibles[x][y][value-1]) {
			grid[x][y] = value;
			for(int i = 0; i<9; i++) {
				possibles[x][y][i] = false;
			}
			for(int x1 = 0; x1<9; x1++) {
				for(int y1 = 0; y1<9; y1++) {
					if((x1==x)||(y1==y)||(x1/3==x/3&&y1/3==y/3)) {
						possibles[x1][y1][value-1] = false;
					}
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isFull() {
		for(int i = 0; i<9; i++) {
			for(int j = 0; j<9; j++) {
				for(int k = 0; k<9; k++) {
					if(possibles[i][j][k]) {
						return false;
					}
				}
			}
		}
		return true;
	}
}

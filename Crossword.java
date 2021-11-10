/* Anna Murray, amm498
Assignment 1: Crossword.java for CS1501 Fall 2021*/

import java.io.*;
import java.util.*;

public class Crossword
{
	private static DictInterface D; //initialize dictionary
	private StringBuilder currentSolution;
	private static int n, end;
	private static StringBuilder rowStr[];
	private static StringBuilder colStr[];
	private static char[][] crossword;

	public static void main(String [] args) throws IOException
	{
		new Crossword(args);
	}

	public Crossword(String[] args) throws IOException{
		//Read the dictionary
		//Scanner fileScan = new Scanner(new FileInputStream("dict8.txt")); //scan in the dictionary
		Scanner fileScan = new Scanner(new FileInputStream(args[0])); //scan in the dictionary
		String st;
		D = new MyDictionary();

		while (fileScan.hasNext()) //add all the words to the dictionary
		{
			st = fileScan.nextLine();
			D.add(st);
		}
		fileScan.close();

		Scanner inScan = new Scanner(new FileInputStream(args[1])); //take in the board
		/*Scanner fReader;
		File fName;
		String fString = "";

		// Make sure the file name for the Boggle board is valid
		while (true)
		{
			try
			{
				System.out.println("Please enter Crossword filename:");
				fString = inScan.nextLine();
				fName = new File(fString);
				fReader = new Scanner(fName);

				break;
			}
			catch (IOException e)
			{
				System.out.println("Problem: " + e);
			}
		}*/


		String line1 = inScan.nextLine();
		n = Integer.parseInt(line1); //board size
		rowStr = new StringBuilder[n]; 
		colStr = new StringBuilder[n];
		end = n-1;

		crossword = new char[n][n]; //create a board that is the size input from the file
		rowStr = new StringBuilder[n];
		colStr = new StringBuilder[n];
		for(int i = 0; i < n; i++)
		{
			rowStr[i] = new StringBuilder(); //initialize the string builder
			colStr[i] = new StringBuilder(); //initialize the string builder
		}

		for (int i = 0; i < n ; i++ ) 
		{
			String rowString = inScan.nextLine();
			for (int j = 0; j < n ; j++ ) 
			{
					crossword[i][j] = rowString.charAt(j); //read the file into the board
			}
		}
		inScan.close();

		/*// Show user the board
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				System.out.print(crossword[i][j] + " "); //print each square of the board
			}
			System.out.println();
		}*/

		solve(0, 0); //call the solve method starting at the first square
	}

	private void solve(int row, int col) //solve the board
	{
		switch(crossword[row][col])
		{
			case '-': //when the board has a - indicating no letter goes there
				rowStr[row].append('-'); //add to the row
				colStr[col].append('-'); //add to the column
				if(row == end && col == end) //if you are at the end of the board
				{
					printCrossword(); //print the solution
					System.out.println("Score: " + puzzleScore()); //Calculate and print the letter score
					System.exit(0); //exit the program
				}
				else //not at the end of the board
				{
					if(col < end) //if not at the edge of the board
						solve(row, col+1); //move to the next space abd solve for the next space
					else //if at the edge of the board
						solve(row+1, 0); //move to the next row and start solving for that row
				}
				rowStr[row].deleteCharAt(rowStr[row].length() - 1);  //delete the last char
				colStr[col].deleteCharAt(colStr[col].length() - 1); //delete the last char
				break; //end of case

			case '+': //when the board has a + indicating a letter goes there
			for(char c = 'a'; c<= 'z'; c++) //iterate through the alphabet
			{
				if(isValid(row, col, c, rowStr, colStr)) //if this letter is valid in this spot in both row and column directions
				{
					rowStr[row].append(c); //add the char to the row
					colStr[col].append(c); //add the char to the column
					if(row == end && col == end) //if at the last square of the board
					{
						printCrossword(); //print the solution
						System.out.println("Score: " + puzzleScore()); //print the calculated letter score
						System.exit(0); //exit the program
					}
					else //if the board is not complete
					{
						if(col < end) //check if at the edge of the board
							solve(row, col + 1); //if not then move over one square and solve
						else //at the edge of the board
							solve(row+1, 0); //move to the next row and solve there
					}
					rowStr[row].deleteCharAt(rowStr[row].length() - 1); //delete the last char
					colStr[col].deleteCharAt(colStr[col].length() - 1); //delete the last char

				}
			}
			break; //end of case
			
			default: //when there is a predetermined letter in that board space
			if(isValid(row, col, crossword[row][col],rowStr, colStr)) //if this letter is valid in this spot in both row and column directions
			{
				rowStr[row].append(crossword[row][col]); //add to the row
				colStr[col].append(crossword[row][col]); //add to the column
				if(row == end && col == end) //if the board is filled
				{
					printCrossword(); //print the solution
					System.out.println("Score: " + puzzleScore()); //calculate the puzzle score and print
					System.exit(0); //exit
				}
				else //the board is not filled
				{
					if(col < end) //if not at the edge of the board
						solve(row, col + 1); //move to the next square and call solve
					else //if at the edge of the board
						solve(row+1, 0); //move down to the next row and solve
				}
				rowStr[row].deleteCharAt(rowStr[row].length() - 1); //delete the last char
				colStr[col].deleteCharAt(colStr[col].length() - 1); //delete the last char
			}
			break; //end of case
		}

	}

	public boolean isValid(int row, int col, char c, StringBuilder[] rows, StringBuilder[] cols) //check if a char is valid
	{
		if(crossword[row][col] != '+' && crossword[row][col] != c) //if the square has neither a char or a +
		{ 
            return false; 
        }

        if(row < n && col < n) //if the row and column are not going over the board size
        {
            rows[row].append(c); //add the char to the row
            cols[col].append(c); //add the char to the column
        } 
        else //would result in index out of bounds so not valid if larger then board size
        {
            return false;
        }

        if(col == end && row == end) //if the board is done
        {
            int rowNext = D.searchPrefix(rows[row], rows[row].lastIndexOf("-")+1, rows[row].length()-1); //make sure the row is a word
            int colNext = D.searchPrefix(cols[col], cols[col].lastIndexOf("-")+1, cols[col].length()-1); //make sure the column is a word
            rows[row].deleteCharAt(rows[row].length()-1); //remove the added char
            cols[col].deleteCharAt(cols[col].length()-1); //remove the added char
            return (rowNext == 2 || rowNext == 3) && (colNext == 2 || colNext == 3); //returns true if row and column are words and false if not
        }

        else if(col == n-1 || crossword[row][col+1] == '-') //if at the end of the row
        {
            int rowNext = D.searchPrefix(rows[row], rows[row].lastIndexOf("-")+1, rows[row].length()-1); //make sure the row is a word
            int colNext = D.searchPrefix(cols[col], cols[col].lastIndexOf("-")+1, cols[col].length()-1); //make sure the column is a prefix or word
            rows[row].deleteCharAt(rows[row].length()-1); //remove the added char
            cols[col].deleteCharAt(cols[col].length()-1); //remove the added char
            return (rowNext == 2 || rowNext == 3) && (colNext == 1 || colNext == 3); //row must be word but column can be prefix or word to return true
        }

        else if(row == n-1 || crossword[row+1][col] == '-')  //if at the end of the column
        {      
            int rowNext = D.searchPrefix(rows[row], rows[row].lastIndexOf("-")+1, rows[row].length()-1); //make sure row is word or prefix
            int colNext = D.searchPrefix(cols[col], cols[col].lastIndexOf("-")+1, cols[col].length()-1); //make sure the column is a word
            rows[row].deleteCharAt(rows[row].length()-1); //remove the added char
            cols[col].deleteCharAt(cols[col].length()-1); //remove the added char
            return (rowNext == 1 || rowNext == 3) && (colNext == 2 || colNext == 3); //column must be a word and row must be a prefix or word to return true 
        } 
        
        else //not at the edge of the board
        { 
            int rowNext = D.searchPrefix(rows[row], rows[row].lastIndexOf("-")+1, rows[row].length()-1); //make sure row is word or prefix
            int colNext = D.searchPrefix(cols[col], cols[col].lastIndexOf("-")+1, cols[col].length()-1); //make sure row is a word or prefix
            rows[row].deleteCharAt(rows[row].length()-1); //remove added char
            cols[col].deleteCharAt(cols[col].length()-1); //remove added char
            return (rowNext == 1 || rowNext == 3) && (colNext == 1 || colNext == 3); //returns true if both column and row are words or prefixes
        }

    }

    private int puzzleScore() //calculate the word score
    {
    	int score = 0; //initialize counter
    	for(int i = 0; i < n; i++) //iterate through rows
    	{
    		for(int j = 0; j <n; j++) //iterate through squares
    		{
    			if(rowStr[i].charAt(j) == 'a' || rowStr[i].charAt(j) == 'e' || rowStr[i].charAt(j) == 'i' || rowStr[i].charAt(j) == 'o' || rowStr[i].charAt(j) == 'u' ||
    				rowStr[i].charAt(j) == 'l' || rowStr[i].charAt(j) == 'n' || rowStr[i].charAt(j) == 's' || rowStr[i].charAt(j) == 't' || rowStr[i].charAt(j) == 'r')
    				score++; //if the char in this square equals these letters add one
    			else if(rowStr[i].charAt(j) == 'd' ||rowStr[i].charAt(j) == 'g')
    				score += 2; //if the char in these sqaures equals these letters add 2
    			else if(rowStr[i].charAt(j) == 'b' ||rowStr[i].charAt(j) == 'c' ||rowStr[i].charAt(j) == 'm' ||rowStr[i].charAt(j) == 'p')
    				score += 3; //if the char in this square equals these letters add 3
    			else if(rowStr[i].charAt(j) == 'f' ||rowStr[i].charAt(j) == 'h' ||rowStr[i].charAt(j) == 'v' ||rowStr[i].charAt(j) == 'w' ||rowStr[i].charAt(j) == 'y')
    				score += 4; //if the char in this square equals these letters add 4
    			else if(rowStr[i].charAt(j) == 'k')
    				score +=5; //if the char in this square equals these letters add 5
    			else if(rowStr[i].charAt(j) == 'j' ||rowStr[i].charAt(j) == 'x')
    				score +=8; //if the char in this square equals these letters add 8
    			else if(rowStr[i].charAt(j) == 'q' ||rowStr[i].charAt(j) == 'z')
    				score +=10; //if the char in this square equals these letters add 10

    		}
    	}
    	return score; //return the score that was calculated
    }

	private static void printCrossword() //prints solved board
	{
		for(int i = 0; i < rowStr.length; i++)
		{
			System.out.println(rowStr[i].toString()); //print out the solution
		}
		//System.out.println();
	}

}


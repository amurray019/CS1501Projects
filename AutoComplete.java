//TO-DO Add necessary imports
import java.io.*;
import java.util.*;

public class AutoComplete{

  //TO-DO: Add instance variable: you should have at least the tree root
  private DLBNode root;


  public AutoComplete(String dictFile) throws java.io.IOException {
    //TO-DO Initialize the instance variables 
    root = null; 
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      add(word);
      //TO-DO call the public add method or the private helper method if you have one
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){ //add recursive helper method
    //TO-DO Implement this method
    if(word == null) throw new IllegalArgumentException ("calls add() with a null key"); //throws argument if word is null
    root = add(root, word, 0); //calling the recursive method
  }

  private DLBNode add(DLBNode x, StringBuilder key, int pos)
  {
    DLBNode result = x; //makes a pointer to the node x
    if (x == null){ //if the node is empty (empty tree)
      result = new DLBNode(key.charAt(pos), 0); //add a new node to begin the tree at root
      if(pos < key.length()-1){ //if you have not added all the characters of the word
        result.child = add(result.child, key, pos + 1); //add a child node using a recusive call on next character
        } else { //you added the full word
            result.data = key.charAt(pos); //update the data
            result.isWord = true; //mark it as a word
            }
        } else if(x.data == key.charAt(pos)) { 
            if(pos < key.length()-1){ //if not added the full word
              result.child = add(result.child, key, pos + 1); //add a child by recursively calling
            } else {
              result.data = key.charAt(pos); //update the data
              result.isWord = true; //mark the key as a word
            }
        } else {
          result.sibling = add(x.sibling, key, pos); //add a sibling by recursively calling (adding to linked list)
        }
        return result; //return the resulting Tree
  }

  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
    root = notifyWordSelected(root,word,0); //calling the recursive method starting at root
  }

  public DLBNode notifyWordSelected(DLBNode x, StringBuilder key, int pos)
  {
    if(x.data == key.charAt(pos)) //if the data is equal to the node we are at
    {
      if(pos < key.length() - 1) //if we have not looked for the whole word
      {
        notifyWordSelected(x.child, key, pos + 1); //recursively call moving down the tree
      }
      else{
        x.score = x.score + 1; //we have found the whole word and we are adding to the score
      }
    }
      else{ //we are not at the right character
        notifyWordSelected(x.sibling, key, pos); //move across the tree looking for the character
      }
    return x; //return the node
  }
  
  //get the score of word
  public int getScore(StringBuilder word){
    int score = getScore(root, word, 0); //call recursive method
    return score; //return the score
  }

  private int getScore(DLBNode x, StringBuilder key, int pos)
  {
    int score = 0; //set initial score to zero
    if(x.data == key.charAt(pos)) //if the node data is equal to key at that position
    {
      if(pos < key.length() - 1) //if we have not gone through the whole word
      {
        score = getScore(x.child, key, pos+1); //recursively call on the child node
      }
      else{ //at the end of the word
        score = x.score; //return score if it was found
      }
    }
    else{ //that position is not equal to the char
      score = getScore(x.sibling, key, pos); //must move through the linked list looking for it
    }
    return score; //return the calculated score
  }
 
  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
  
  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  

  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    
    ArrayList<Suggestion> res = new ArrayList<Suggestion>(); //initialize an array list of suggestions
    DLBNode getNode = getNode(root, word.toString(), 0); //get node starting from the prefix
    Suggestion x = new Suggestion(word, root.score); //initiaize a suggestion with string builder and root score
    if(getNode != null && getNode.isWord == true && getNode.child != null)
      res.add(x); //if the root is a word add to the arraylist
  
    if(getNode != null) //if there is a non null value (prefix in the tree)
    {
      retrieveWords(getNode.child, word, res); //recursively call moving down the tree
    }
    if(getNode != null && getNode.child == null && getNode.isWord == true) //if the leaf is a word
      res.add(new Suggestion(word, getScore(word))); //add the word to the arraylist
    Collections.sort(res, Collections.reverseOrder()); //sort alphabetically and from highest to lowest
    return res; //return the arraylist
  }

  private void retrieveWords(DLBNode x, StringBuilder current, ArrayList<Suggestion> traverse)
  {
        if(x != null){ //if the node is non null
          current.append(x.data); //append the data to the string builder
          if(x.isWord == true){ //if that is a word
            StringBuilder word = new StringBuilder(current); //make a cope of the string builder
            Suggestion res = new Suggestion(word, x.score); //make a suggestion with the stringbuilder and its score in the tree
            traverse.add(res); //add it to the arraylist
          }
          retrieveWords(x.child, current, traverse); //otherwise move down the tree looking for the word
          current.deleteCharAt(current.length()-1); //remove the chars rom the stringbuilder
          if(x.sibling != null) //if the node has a sibling
          {
            retrieveWords(x.sibling, current, traverse); //move across the tree
          }
        }
  }
    

  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair. 
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion>{

    StringBuilder word; //initialize stringbuilder
    int score; //initialize score for suggestions

    private Suggestion(StringBuilder word, int score)
    {
      this.word = word; 
      this.score = score;
    }

    //@Override
    public int compareTo(Suggestion sug)
    {
      return Integer.compare(this.score, sug.score); //compare the scores and return value
    }
    //TO-DO Fill in the fields and methods for this class. Make sure to have them public as they will be accessed from the test program A2Test.java.
  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }

  }
}
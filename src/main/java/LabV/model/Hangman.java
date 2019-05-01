package LabV.model;

public class Hangman {

    private char sharedGuess;
    private char[] sharedPartialWord;
    private Boolean isFull = false;
    private int triesLeft;

    public Hangman(int triesLeft) {
        if(triesLeft>26)
            this.triesLeft = 26;
        else this.triesLeft = triesLeft;
    }

    public synchronized char getSharedGuess(){
        while(!isFull){
            try {
                wait();
            }
            catch (InterruptedException ex){
                System.out.println("InterruptedException en getSharedGuess"+ex.getMessage()+"\n - Contacte a su administrador");
            }
        }
        isFull = false;
        notify();
        return sharedGuess;
    }

    public synchronized void setSharedGuess(char guess){
        while(isFull){
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException en setSharedGuess"+ex.getMessage()+"\n - Contacte a su administrador");
            }
        }
        sharedGuess = guess;
        isFull = true;
        notify();
    }

    public char[] getSharedPartialWord() {
        return sharedPartialWord;
    }
    public void setSharedPartialWord(char[] sharedPartialWord) {
        this.sharedPartialWord = sharedPartialWord;
    }

    public int getTriesLeft() {
        //System.out.println("There are "+triesLeft+" tries left");
        return triesLeft;
    }
    public void setTriesLeft(int triesLeft){
        if(triesLeft>26)
            this.triesLeft = 26;
        else this.triesLeft = triesLeft;
    }

    public Player tellWinner(Player p1,Player p2){
        if(p1.getScore() > p2.getScore())
            return p1;
        else if(p1.getScore() < p2.getScore())
            return p2;
        else {
            System.out.println("Empate");
            return null;
        }
    }
}

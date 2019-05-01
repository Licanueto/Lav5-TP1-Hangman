package LabV.model;

import LabV.dao.ConnectionDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class Player implements Runnable{
    private String name;
    private Integer attempts;
    private Integer score;
    private boolean isGuessing;
    private char[] hiddenWord;
    private Hangman game;
    private Player opponent;
    private List<Character> leftFromAlphabet;

    public Player(String name, boolean isGuessing,Hangman game) {
        this.name = name;
        this.attempts = 0;
        this.score = 0;
        this.isGuessing = isGuessing;
        this.game = game;
        chooseWord();
        this.leftFromAlphabet = loadAlphabet();
    }

    public char[] getHiddenWord() {
        return hiddenWord;
    }
    public void setHiddenWord(char[] hiddenWord) {
        this.hiddenWord = hiddenWord;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAttempts() {
        return attempts;
    }
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public boolean isGuessing() {
        return isGuessing;
    }
    public void setGuessing(boolean guessing) {
        isGuessing = guessing;
    }
    public Player getOpponent() {
        return opponent;
    }
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    private List<Character> loadAlphabet(){
        char[] alph = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        List<Character> alphabetList = new ArrayList<>();
        for(int i=0;i<26;i++)
            alphabetList.add(alph[i]);
        return alphabetList;
    }

    public void switchRoles(){
        if(isGuessing)
            this.isGuessing = false;
        else this.isGuessing = true;
    }
    public void chooseWord() {
        ConnectionDAO conn = new ConnectionDAO();
        // Pido una palabra a la base de datos, la convierto a mayusculas y elimino todo lo que no sea una letra -> por alguna razon
        //  esa eliminación/reemplazo no funciona y despues cuando hago el getHiddenWord() tengo que volver a hacer literalmente el mismo replaceAll()
        setHiddenWord(conn.getRandomWord().toUpperCase().replaceAll("[^A-Z]", "").toCharArray());
    }

    public synchronized char guess(){
        char c = ' ';
//        System.out.println("leftFromAlphabet.size() = "+leftFromAlphabet.size());
        if(!leftFromAlphabet.isEmpty())// si quedan letras por probar
            c = leftFromAlphabet.remove(new Random().nextInt(leftFromAlphabet.size())); // se selecciona elige una letra random y se la saca de las letras por probar
//        System.out.println("Se intenta adivinar "+c+"");
        return c;
    }

    public String getNiceStringFromCharArray(char[] charArray){
        return Arrays.toString(charArray).replaceAll("["+Pattern.quote("[],")+"]","");
    }

    public void processGuess(){
        // Le sumo el intento al oponente
        this.opponent.setAttempts(this.opponent.getAttempts()+1);
        // Disminuyo un intentoRestante al juego
        game.setTriesLeft(game.getTriesLeft()-1);
        //System.out.println("Complete Word: "+ Arrays.toString(completeWord));
        char guess = game.getSharedGuess();
        //System.out.println("Guess: "+guess);

        // Recorro la palabra y en caso de que haya adivinado alguna letra se la asigno
        for(int i=0;i<this.hiddenWord.length;i++) {
            if (this.hiddenWord[i] == guess) {
                char[] localPartialWord = game.getSharedPartialWord();
                localPartialWord[i] = guess;
                game.setSharedPartialWord(localPartialWord);
            }
        }System.out.println("Word after guessing: "+ getNiceStringFromCharArray(game.getSharedPartialWord()));
        if(!Arrays.toString(game.getSharedPartialWord()).contains("_")) {
            game.setTriesLeft(0); // Si la palabra se encuentra (es decir no quedan más guiones) el juego se detiene.
            this.opponent.setScore(this.opponent.getScore()+1);
            System.out.println("The player "+this.opponent.getName()+" has won.");
        }
    }

    @Override
    public void run(){
        if(!isGuessing) // Seteo la "palabra" (reemplazada por guiones bajos) a la que ambos jugadores acceden con la que eligió el jugador que no adivina.
        {
            game.setSharedPartialWord(Arrays.toString(hiddenWord).replaceAll("[^A-Z]", "").replaceAll("[A-Z]", "_").toCharArray());
            System.out.println("Chosen word: "+ Arrays.toString(this.hiddenWord));//getNiceStringFromCharArray(this.hiddenWord));
        }while (game.getTriesLeft() > 0) {
            if (this.isGuessing) {
                game.setSharedGuess(this.guess());
            }else this.processGuess();
        }
        // Se invierten los roles
        this.switchRoles();
        game.setTriesLeft(15);

        if(!isGuessing) // Seteo la nueva "palabra" (reemplazada por guiones bajos) a la que ambos jugadores acceden con la que eligió el que ahora es el jugador que no adivina.
            game.setSharedPartialWord(Arrays.toString(hiddenWord).replaceAll("[^A-Z]","").replaceAll("[A-Z]", "_").toCharArray());

        while (game.getTriesLeft() > 0) {
            if (this.isGuessing) {
                game.setSharedGuess(this.guess());
            }else this.processGuess();
        }
    }

}

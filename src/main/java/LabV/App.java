package LabV;

import LabV.dao.ConnectionDAO;
import LabV.model.Hangman;
import LabV.model.Player;

public class App 
{
    public static void main( String[] args )
    {
        ConnectionDAO conn = new ConnectionDAO();
        // En caso de que no haya sido cargado se carga el diccionario a la base de datos.
        conn.loadDictionaryIntoDatabase();

        Hangman game = new Hangman(26);
        Player player1 = new Player("FirstGuesser",true,game);
        Player player2 = new Player("SecondGuesser",false,game);
        player1.setOpponent(player2);
        player2.setOpponent(player1);

        Thread t1 = new Thread(player1);
        Thread t2 = new Thread(player2);

        t1.start();
        t2.start();

        try {
            t2.join();
            t1.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("Murió joineando a los threads\n Contacte a su administrador");
        }

        System.out.println("\nPlayer 1 Score: "+player1.getScore());
        System.out.println("Player 2 Score: "+player2.getScore());

        // Persisto al ganador
        Player winner = (game.tellWinner(player1,player2));
        if(winner!=null) new ConnectionDAO().persistWinner(winner);
    }


}

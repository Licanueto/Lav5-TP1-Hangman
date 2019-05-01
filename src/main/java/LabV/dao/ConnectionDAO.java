package LabV.dao;

import LabV.model.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

public class ConnectionDAO {


    private Connection conn;
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3307/tp2_ahorcado";
    static final String DB_USER = "root";
    static final String DB_PASS = "1234";
    static final String DIC_PATH = "/en_GB.dic"; // ruta del diccionario
    public ConnectionDAO() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("There was a problem connecting with the database.\n" +
                    " Please contact your IT people and give them a really vague and distorted reproduction of what actually went on.");
            ex.printStackTrace();
        }
    }

    public boolean isDictionaryLoaded() {
        try {
            PreparedStatement pst = this.conn.prepareStatement("SELECT * FROM words LIMIT 1");
            ResultSet rs = pst.executeQuery();
            return rs.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int saveDictionary(List<String> wordList) {
        int updatedRows = 0;
        if(!(wordList.isEmpty())){
            try {
                this.conn.setAutoCommit(false);
                PreparedStatement pst = this.conn.prepareStatement("INSERT INTO words (word) values (?)");
                Iterator<String> it = wordList.iterator();
                StringBuilder str;
                while(it.hasNext()){
                    str = new StringBuilder(it.next());
                    pst.setString(1,str.toString());
                    pst.addBatch();
                }
                int [] updatedRowsArray = pst.executeBatch();
                this.conn.setAutoCommit(true);
                updatedRows = updatedRowsArray.length;
            } catch (SQLException ex) {
                System.out.println("There was a problem writing to the database.\n" +
                        " Please contact your IT people and give them a really vague and distorted reproduction of what actually went on.");
                ex.printStackTrace();
            }
        }else{
            System.out.println("Lista del diccionario vacia, contacte a su administrador.");
        }
        return updatedRows;
    }

    public void loadDictionaryIntoDatabase(){
        if(!isDictionaryLoaded()){
            File file = new File(DIC_PATH);
            List<String> localWordList = new ArrayList<String>();
            try {
                Scanner sc = new Scanner(file);
                sc.useDelimiter("/n"); // El diccionario tiene una palabra por linea
                while(sc.hasNextLine()){
                    localWordList.add(sc.nextLine().split("/",2)[0]); // Algunas palabras terminan en /algo asi que ante una eventual '/' divido la palabra y tomo solo la primer parte
                }
                ConnectionDAO conn = new ConnectionDAO();
                conn.saveDictionary(localWordList);
            }catch (FileNotFoundException e) {
                System.out.println("Error al acceder al archivo del diccionario.\nComprobar que la ruta sea \"C:\\Users\\Li\\Documents\\UTN\\TSSI\\Lab 5\\Tp2-Ahorcado\\en_GB.dic\" o cambiarla");
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRandomWord(){
        try {
            PreparedStatement pst = this.conn.prepareStatement("SELECT word FROM words ORDER BY RAND() LIMIT 1");
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "unbreakable"; // couldn't hold mysqlf, sorry
    }

    public void persistWinner(Player winner){
        try {
            PreparedStatement pst = this.conn.prepareStatement("SELECT id_word FROM words WHERE word LIKE (?)");
            pst.setString(1, winner.getNiceStringFromCharArray(winner.getOpponent().getHiddenWord()).replaceAll("\\s",""));
            ResultSet rsIdWord = pst.executeQuery();
            if(rsIdWord.next()){
                PreparedStatement pst2 = this.conn.prepareStatement("INSERT INTO winners (id_winning_word,score,name,date) values (?,?,?,?)");
                pst2.setInt(1,rsIdWord.getInt(1));
                pst2.setInt(2,winner.getScore());
                pst2.setString(3,winner.getName());
                pst2.setDate(4, Date.valueOf(LocalDate.now()));
                pst2.executeQuery();
            }
        } catch (SQLException ex) {
            System.out.println("There was a problem writing to the database.\n" +
                    " Please contact your IT people and give them a really vague and distorted reproduction of what actually went on.");
            ex.printStackTrace();
        }
    }

    // Ejercicio 5 del parcial viejo de Avanzada 1

    // Avivada 1: Puede haber muchos id_titular por auto? tengo que chequear todos contra la base de datos?
    /*public boolean persistCar(Car car){
        try{
/*            PreparedStatement pst = this.conn.prepareStatement("SELECT t.id_titular,m.id_marca " +
                    "                                               FROM titulares t,marcas m" +
                    "                                               WHERE t.id_titular = (?) AND m.id_marca = ?");

            boolean flag = true;
            pst.setInt(1,car.getTitulares());
            ResultSet rs = pst.executeQuery();

            PreparedStatement pst = this.conn.prepareStatement("Select m.id_marca from marcas where id_marca = ?");
            pst.setInt(1,car.getMarca().getId());
            pst.execute()

            foreach(Titular t : car.getTitulares()){
                PreparedStatement pst2 = this.conn.prepareStatement("Select t.id_titular from titulares where id_titular = ?");
                pst2.setInt(1,car.getMarca().getId());

            }
        }
    }
    */
    public Connection getConn() {
        return conn;
    }


}






















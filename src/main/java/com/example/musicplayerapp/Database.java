package com.example.musicplayerapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    public static Connection connect;
    public static Connection connectDb(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost/musicapp", "root", "root");

        }catch(Exception e){e.printStackTrace();}

        return connect;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
/**
 *
 * @author Usuario
 */
public class Conexion {
    
    Connection conectar = null;
    public Connection conexion(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conectar=(Connection) DriverManager.getConnection("jdbc:mysql://datos.mysql.database.azure.com/control","datos@datos","Marlon12345");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de conexion! "+e.getMessage());
        }
        return conectar;
    }
}

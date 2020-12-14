package control;

import Conexion.Conexion;
import com.barcodelib.barcode.QRCode;
import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VPrincipal extends javax.swing.JFrame {
    String Nombre = "";
    String Apellido = "";
    String tipo = "";
    String Correo = "";
    String codigo;
    Date now;
    SimpleDateFormat date;
    int seleccionado;
    DefaultTableModel modelo;
    Conexion cc = new Conexion();
    Connection con= cc.conexion();
    int udm=0, resol=72, rot=0;
    float mi=0.000f, md=0.000f, ms=0.000f, min=0.000f, tam=20.00f;
    public VPrincipal() {
        initComponents();
        Invitado.setVisible(false);
        Invitado2.setVisible(false);
        Usuario.setVisible(false);
        Consulta.setVisible(false);
        
    }
    public void horactual(){
    now = new Date(System.currentTimeMillis());
    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
    
   
    }
    public void correo(String correo, String id){
       
        File ruta = new File("Qrs");
        String archivo = ruta.getAbsolutePath()+"/"+id+".png";
        Properties propiedad = new Properties();
        propiedad.setProperty("mail.smtp.host", "smtp.gmail.com");
        propiedad.setProperty("mail.smtp.starttls.enable", "true");
        propiedad.setProperty("mail.smtp.port", "587");

        Session sesion = Session.getDefaultInstance(propiedad);
        String correoEnvia = "marlonthe154@gmail.com";
        String contrasena = "marlonJAJA3";
        String receptor = correo;
        String asunto = "Codigo Qr";
        String mensaje="Su codigo Qr es:";

        MimeMessage mail = new MimeMessage(sesion);
        try {
            mail.setFrom(new InternetAddress (correoEnvia));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress (receptor));
            mail.setSubject(asunto);
            
            
            BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);
                        
            BodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(new DataHandler(new FileDataSource(archivo)));
            adjunto.setFileName(id+".png");
            
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);

            mail.setContent(multiParte);
            
            
            Transport transportar = sesion.getTransport("smtp");
            transportar.connect(correoEnvia,contrasena);
            transportar.sendMessage(mail, mail.getRecipients(Message.RecipientType.TO));
            transportar.close();


        } catch (AddressException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void generarQR(String dato, String id){
        horactual();
        String string = date.format(now);
               String[] parts = string.split(" ");
               String part1 = parts[0]; 
               String part2 = parts[1]; 
        File ruta2 = new File("Qrs");
        
        try {
            QRCode c = new QRCode();
            c.setData(dato);
            c.setDataMode(QRCode.MODE_BYTE);
            c.setUOM(udm);
            c.setLeftMargin(mi);
            c.setRightMargin(md);
            c.setTopMargin(ms);
            c.setBottomMargin(min);
            c.setResolution(resol);
            c.setRotate(rot);
            c.setModuleSize(tam);
            
            String archivo = ruta2.getAbsolutePath()+"/"+id+" "+part1+".png";
            c.renderBarcode(archivo);

            Desktop d=Desktop.getDesktop();
            d.open(new File(archivo));
            
            
        } catch (Exception e) {
            System.out.println("Error" +e);
        }
        
    }
    public void generador(){
     if(identificacion1.getText().equals("")){
        JOptionPane.showMessageDialog(null, "El campo identificacion no puede estar vacio!");
     }else{
        consulta(identificacion1.getText());
     }
    }
    public void consulta(String id){
        try {
            Statement st = con.createStatement();
            ResultSet rs= st.executeQuery("select * from usuarios where identificacion="+id);
            rs.beforeFirst();
            rs.next();
            tipo = rs.getString("tipousuario_id"); 
            String tipoo = rs.getString("tarjeta");
            identificacion4.setText(identificacion1.getText());
            identificacion1.setText("");
            if(tipoo.equals("1")){
                Nombre = rs.getString("nombre1");
                Apellido = rs.getString("apellido1");
                identificacion4.setText(rs.getString("identificacion"));
                Correo4.setText(rs.getString("correo"));
                rs= st.executeQuery("SELECT idrazon, nombre FROM razones where tarjeta='1';");
                opcion4.removeAllItems();
                while(rs.next()){
                    opcion4.addItem(rs.getString(1)+": "+rs.getString(2));
                }
                Invitado2.setVisible(true);
                Consulta.setVisible(false);
            }else if(tipoo.equals("0")){
                Nombre = rs.getString("nombre1");
                Apellido = rs.getString("apellido1");
                identificacion4.setText(rs.getString("identificacion"));
                Correo4.setText(rs.getString("correo"));
                rs= st.executeQuery("SELECT idrazon, nombre FROM razones where tarjeta='0';");
                opcion4.removeAllItems();
                while(rs.next()){
                    opcion4.addItem(rs.getString(1)+": "+rs.getString(2));
                }
                Invitado2.setVisible(true);
                Consulta.setVisible(false);
            }
            JOptionPane.showMessageDialog(null, " Encontrado!\n" +
            "Nombre: "+ Nombre +" "+ Apellido);
        
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se encontró ningún usuario" );
            identificacionvisitante.setText(identificacion1.getText());
            identificacion1.setText("");
            razon();
            
            
        }
    }
    
    public void razon(){
        try {
            Statement st = con.createStatement();
            ResultSet rs= st.executeQuery("select * from usuarios");
            rs.beforeFirst();
            rs.next();
            rs= st.executeQuery("SELECT idrazon, nombre FROM razones where tarjeta='0';");
            opcion2.removeAllItems();
            while(rs.next()){
                opcion2.addItem(rs.getString(1)+": "+rs.getString(2));
            }            
            Invitado.setVisible(true);
            Consulta.setVisible(false);
        } catch (SQLException ex) {
            Logger.getLogger(VPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void registro(){
        if(identificacionvisitante.getText().equals("") || nombre1visitante.getText().equals("") || nombre2visitante.getText().equals("") || apellido1visitante.getText().equals("") || apellido2visitante.getText().equals("") || telefonovisitante.getText().equals("") || correovisitante.getText().equals("") ){
            JOptionPane.showMessageDialog(null, "Hay campos vacios!!");
        }else {
            try {
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO `control`.`usuarios` (`identificacion`, `nombre1`, `nombre2`, `apellido1`, `apellido2`, `correo`, `telefono`, `tarjeta`, `tipousuario_id`, `estado`) VALUES ('"+identificacionvisitante.getText()+"', '"+nombre1visitante.getText()+"', '"+nombre2visitante.getText()+"', '"+apellido1visitante.getText()+"', '"+apellido2visitante.getText()+"', '"+correovisitante.getText()+"', '"+telefonovisitante.getText()+"', '0', '100', '1')");
               
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Comprueba que los datos estén bien!");

            }
        }
    }
    
     public void control(String id, String tipousuario, String razon){
            try {
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO `control`.`control` (`id`, `fechaentrada`, `fechasalida`, `estado`, `usuarios_identificacion`, `tipousuario_id`, `razones_idrazon`) VALUES (null, null, null, '0', '"+id+"', '"+tipousuario+"', '"+razon+"')");
        
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Comprueba que los datos este bien!");

            }
    }
    
     public void verificar(String usuario, String password){
        try {
            Statement st = con.createStatement();
            ResultSet rs= st.executeQuery("SELECT * FROM perfil, usuarios u WHERE  usuario='"+usuario+"' AND clave='"+password+"' and estado ='1' and u.identificacion = usuarios_identificacion");
            rs.beforeFirst();
            rs.next();
            String nombre ="";
            nombre = rs.getString("nombre1");
           
                login.setVisible(false);
                Consulta.setVisible(true);

            JOptionPane.showMessageDialog(null, " Bienvenido "+ nombre);
           
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de autenticación" );
            
        }
    }
    /*
    public void filtro(String query){
        String[] titulos = {"Edentificacion", "Hora de entrada", "Razón"};
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(modelo);
        
        tabla.setRowSorter(tr);
        
        tr.setRowFilter(RowFilter.regexFilter(query));
    public void mostrardatos2(){
        String[] titulos = {"Placa", "Fecha inical", "Fecha final", "Tiempo", "Precio", "tipo"};
        String[] registros = new String[7];
        
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        
        String SQL= "SELECT * FROM historial";
        
        try {
            
            Statement st = con.createStatement();
            ResultSet rs= st.executeQuery(SQL);
            
            while (rs.next()){
            registros[0]=rs.getString("Placa").toUpperCase();
            registros[1]=rs.getString("FechaInicial");
            registros[2]=rs.getString("FechaFinal");
            registros[3]=rs.getString("Tiempom");
            registros[4]=rs.getString("Precio");
            registros[5]=rs.getString("tipo");
            
            
            modelo.addRow(registros);
            }
            tabla1.setModel(modelo);
            
        } catch (Exception e) {
            
        }
    }*/

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        login = new javax.swing.JPanel();
        NLogo14 = new javax.swing.JLabel();
        Ntitulo10 = new javax.swing.JLabel();
        NlabelPlaca14 = new javax.swing.JLabel();
        Nlabeltipo13 = new javax.swing.JLabel();
        Nbotonregistrar28 = new javax.swing.JButton();
        Nbotonregistrar29 = new javax.swing.JButton();
        usuario = new javax.swing.JTextField();
        Usuario3 = new javax.swing.JPanel();
        NLogo15 = new javax.swing.JLabel();
        Titulo4 = new javax.swing.JLabel();
        NlabelPlaca15 = new javax.swing.JLabel();
        Correo8 = new javax.swing.JTextField();
        Nlabeltipo15 = new javax.swing.JLabel();
        opcion8 = new javax.swing.JComboBox<>();
        Nbotonregistrar30 = new javax.swing.JButton();
        Nbotonregistrar31 = new javax.swing.JButton();
        identificacion16 = new javax.swing.JTextField();
        Nlabeltipo16 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        Invitado2 = new javax.swing.JPanel();
        Titulo1 = new javax.swing.JLabel();
        NlabelPlaca3 = new javax.swing.JLabel();
        Correo4 = new javax.swing.JTextField();
        Nlabeltipo6 = new javax.swing.JLabel();
        opcion4 = new javax.swing.JComboBox<>();
        Nbotonregistrar6 = new javax.swing.JButton();
        Nbotonregistrar7 = new javax.swing.JButton();
        identificacion4 = new javax.swing.JTextField();
        Nlabeltipo8 = new javax.swing.JLabel();
        NLogo4 = new javax.swing.JLabel();
        Usuario = new javax.swing.JPanel();
        NLogo2 = new javax.swing.JLabel();
        Titulo = new javax.swing.JLabel();
        NlabelPlaca2 = new javax.swing.JLabel();
        Correo3 = new javax.swing.JTextField();
        Nlabeltipo4 = new javax.swing.JLabel();
        opcion3 = new javax.swing.JComboBox<>();
        Nbotonregistrar4 = new javax.swing.JButton();
        Nbotonregistrar5 = new javax.swing.JButton();
        identificacion3 = new javax.swing.JTextField();
        Nlabeltipo5 = new javax.swing.JLabel();
        Invitado = new javax.swing.JPanel();
        NLogo1 = new javax.swing.JLabel();
        Ntitulo1 = new javax.swing.JLabel();
        NlabelPlaca1 = new javax.swing.JLabel();
        telefonovisitante = new javax.swing.JTextField();
        Nlabeltipo2 = new javax.swing.JLabel();
        opcion2 = new javax.swing.JComboBox<>();
        Nbotonregistrar2 = new javax.swing.JButton();
        Nbotonregistrar3 = new javax.swing.JButton();
        identificacionvisitante = new javax.swing.JTextField();
        Nlabeltipo3 = new javax.swing.JLabel();
        correovisitante = new javax.swing.JTextField();
        NlabelPlaca4 = new javax.swing.JLabel();
        nombre1visitante = new javax.swing.JTextField();
        nombre2visitante = new javax.swing.JTextField();
        Nlabeltipo9 = new javax.swing.JLabel();
        Nlabeltipo10 = new javax.swing.JLabel();
        apellido1visitante = new javax.swing.JTextField();
        NlabelPlaca6 = new javax.swing.JLabel();
        Nlabeltipo12 = new javax.swing.JLabel();
        apellido2visitante = new javax.swing.JTextField();
        tipovisitante = new javax.swing.JComboBox<>();
        Nlabeltipo7 = new javax.swing.JLabel();
        tarjetavisitante = new javax.swing.JComboBox<>();
        Nlabeltipo14 = new javax.swing.JLabel();
        Consulta = new javax.swing.JPanel();
        Ntitulo = new javax.swing.JLabel();
        NlabelPlaca = new javax.swing.JLabel();
        Nbotonregistrar = new javax.swing.JButton();
        Nbotonregistrar1 = new javax.swing.JButton();
        identificacion1 = new javax.swing.JTextField();
        NLogo5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        Menu = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(6);

        login.setBackground(new java.awt.Color(50, 68, 68));
        login.setForeground(new java.awt.Color(60, 63, 65));
        login.setMaximumSize(new java.awt.Dimension(32770, 32767));

        NLogo14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        Ntitulo10.setBackground(new java.awt.Color(255, 255, 255));
        Ntitulo10.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Ntitulo10.setForeground(new java.awt.Color(255, 255, 255));
        Ntitulo10.setText("Nuevo Ingreso");

        NlabelPlaca14.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca14.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca14.setText("Identificación");

        Nlabeltipo13.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo13.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo13.setText("Contraseña");

        Nbotonregistrar28.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar28.setText("Entrar");
        Nbotonregistrar28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar28ActionPerformed(evt);
            }
        });

        Nbotonregistrar29.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar29.setText("Salir");
        Nbotonregistrar29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar29ActionPerformed(evt);
            }
        });

        usuario.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usuarioActionPerformed(evt);
            }
        });

        Usuario3.setBackground(new java.awt.Color(50, 68, 68));
        Usuario3.setForeground(new java.awt.Color(60, 63, 65));

        NLogo15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        Titulo4.setBackground(new java.awt.Color(255, 255, 255));
        Titulo4.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Titulo4.setForeground(new java.awt.Color(255, 255, 255));
        Titulo4.setText("Nuevo Ingreso");

        NlabelPlaca15.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca15.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca15.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca15.setText("Identificacion");

        Correo8.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        Correo8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Correo8ActionPerformed(evt);
            }
        });

        Nlabeltipo15.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo15.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo15.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo15.setText("La razón de ingresos");

        opcion8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4", "5", "6" }));
        opcion8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcion8ActionPerformed(evt);
            }
        });

        Nbotonregistrar30.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar30.setText("Generar QR");
        Nbotonregistrar30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar30ActionPerformed(evt);
            }
        });

        Nbotonregistrar31.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar31.setText("Regresar");
        Nbotonregistrar31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar31ActionPerformed(evt);
            }
        });

        identificacion16.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        identificacion16.setEnabled(false);
        identificacion16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificacion16ActionPerformed(evt);
            }
        });

        Nlabeltipo16.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo16.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo16.setText("Correo");

        javax.swing.GroupLayout Usuario3Layout = new javax.swing.GroupLayout(Usuario3);
        Usuario3.setLayout(Usuario3Layout);
        Usuario3Layout.setHorizontalGroup(
            Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Usuario3Layout.createSequentialGroup()
                .addGroup(Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Usuario3Layout.createSequentialGroup()
                        .addGap(790, 790, 790)
                        .addGroup(Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Nbotonregistrar31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nbotonregistrar30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Correo8)
                            .addComponent(opcion8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nlabeltipo15, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(identificacion16)
                            .addComponent(NlabelPlaca15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(Usuario3Layout.createSequentialGroup()
                        .addGap(804, 804, 804)
                        .addComponent(Titulo4, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Usuario3Layout.createSequentialGroup()
                        .addGap(851, 851, 851)
                        .addComponent(NLogo15, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(3421, Short.MAX_VALUE))
            .addGroup(Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Usuario3Layout.createSequentialGroup()
                    .addGap(791, 791, 791)
                    .addComponent(Nlabeltipo16, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(2406, Short.MAX_VALUE)))
        );
        Usuario3Layout.setVerticalGroup(
            Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Usuario3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NLogo15, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Titulo4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(NlabelPlaca15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(identificacion16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nlabeltipo15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(opcion8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(Correo8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar30, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar31, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(305, 305, 305))
            .addGroup(Usuario3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Usuario3Layout.createSequentialGroup()
                    .addContainerGap(432, Short.MAX_VALUE)
                    .addComponent(Nlabeltipo16, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(466, 466, 466)))
        );

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup()
                .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginLayout.createSequentialGroup()
                        .addGap(804, 804, 804)
                        .addComponent(Ntitulo10, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(loginLayout.createSequentialGroup()
                        .addGap(851, 851, 851)
                        .addComponent(NLogo14, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(loginLayout.createSequentialGroup()
                        .addGap(790, 790, 790)
                        .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(Nbotonregistrar29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nbotonregistrar28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nlabeltipo13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usuario)
                            .addComponent(NlabelPlaca14, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Usuario3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(950, 950, 950))
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                        .addComponent(NLogo14, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Ntitulo10, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(NlabelPlaca14, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Nlabeltipo13)
                        .addGap(18, 18, 18)
                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Nbotonregistrar28, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Nbotonregistrar29, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(381, 381, 381))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginLayout.createSequentialGroup()
                        .addComponent(Usuario3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        Invitado2.setBackground(new java.awt.Color(50, 68, 68));
        Invitado2.setForeground(new java.awt.Color(60, 63, 65));

        Titulo1.setBackground(new java.awt.Color(255, 255, 255));
        Titulo1.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Titulo1.setForeground(new java.awt.Color(255, 255, 255));
        Titulo1.setText("Nuevo Ingreso");

        NlabelPlaca3.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca3.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca3.setText("Identificación");

        Correo4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        Correo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Correo4ActionPerformed(evt);
            }
        });

        Nlabeltipo6.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo6.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo6.setText("La razón de ingresos");

        opcion4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));
        opcion4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcion4ActionPerformed(evt);
            }
        });

        Nbotonregistrar6.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar6.setText("Generar QR");
        Nbotonregistrar6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar6ActionPerformed(evt);
            }
        });

        Nbotonregistrar7.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar7.setText("Regresar");
        Nbotonregistrar7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar7ActionPerformed(evt);
            }
        });

        identificacion4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        identificacion4.setEnabled(false);
        identificacion4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificacion4ActionPerformed(evt);
            }
        });

        Nlabeltipo8.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo8.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo8.setText("Correo");

        NLogo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        javax.swing.GroupLayout Invitado2Layout = new javax.swing.GroupLayout(Invitado2);
        Invitado2.setLayout(Invitado2Layout);
        Invitado2Layout.setHorizontalGroup(
            Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Invitado2Layout.createSequentialGroup()
                .addGap(791, 791, 791)
                .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Nbotonregistrar7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Nbotonregistrar6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Correo4)
                        .addComponent(opcion4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Nlabeltipo6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(identificacion4)
                        .addComponent(NlabelPlaca3, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Titulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(4840, Short.MAX_VALUE))
            .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Invitado2Layout.createSequentialGroup()
                    .addGap(791, 791, 791)
                    .addComponent(Nlabeltipo8, javax.swing.GroupLayout.DEFAULT_SIZE, 4529, Short.MAX_VALUE)
                    .addGap(649, 649, 649)))
            .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Invitado2Layout.createSequentialGroup()
                    .addGap(840, 840, 840)
                    .addComponent(NLogo4, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(4890, Short.MAX_VALUE)))
        );
        Invitado2Layout.setVerticalGroup(
            Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Invitado2Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(Titulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(NlabelPlaca3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(identificacion4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nlabeltipo6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(opcion4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(Correo4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(306, Short.MAX_VALUE))
            .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Invitado2Layout.createSequentialGroup()
                    .addGap(458, 458, 458)
                    .addComponent(Nlabeltipo8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(448, Short.MAX_VALUE)))
            .addGroup(Invitado2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Invitado2Layout.createSequentialGroup()
                    .addGap(71, 71, 71)
                    .addComponent(NLogo4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(727, Short.MAX_VALUE)))
        );

        Usuario.setBackground(new java.awt.Color(50, 68, 68));
        Usuario.setForeground(new java.awt.Color(60, 63, 65));

        NLogo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        Titulo.setBackground(new java.awt.Color(255, 255, 255));
        Titulo.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Titulo.setForeground(new java.awt.Color(255, 255, 255));
        Titulo.setText("Nuevo Ingreso");

        NlabelPlaca2.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca2.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca2.setText("Identificación");

        Correo3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        Correo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Correo3ActionPerformed(evt);
            }
        });

        Nlabeltipo4.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo4.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo4.setText("La razón de ingresos");

        opcion3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "4", "5", "6" }));
        opcion3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcion3ActionPerformed(evt);
            }
        });

        Nbotonregistrar4.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar4.setText("Generar QR");
        Nbotonregistrar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar4ActionPerformed(evt);
            }
        });

        Nbotonregistrar5.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar5.setText("Regresar");
        Nbotonregistrar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar5ActionPerformed(evt);
            }
        });

        identificacion3.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        identificacion3.setEnabled(false);
        identificacion3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificacion3ActionPerformed(evt);
            }
        });

        Nlabeltipo5.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo5.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo5.setText("Correo");

        javax.swing.GroupLayout UsuarioLayout = new javax.swing.GroupLayout(Usuario);
        Usuario.setLayout(UsuarioLayout);
        UsuarioLayout.setHorizontalGroup(
            UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsuarioLayout.createSequentialGroup()
                .addGap(761, 761, 761)
                .addGroup(UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Nbotonregistrar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Nbotonregistrar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Correo3)
                        .addComponent(opcion3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Nlabeltipo4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(identificacion3)
                        .addComponent(NlabelPlaca2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NLogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6616, 6616, 6616))
            .addGroup(UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(UsuarioLayout.createSequentialGroup()
                    .addGap(791, 791, 791)
                    .addComponent(Nlabeltipo5, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(6615, Short.MAX_VALUE)))
        );
        UsuarioLayout.setVerticalGroup(
            UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsuarioLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(NLogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(NlabelPlaca2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(identificacion3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nlabeltipo4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(opcion3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(Correo3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(297, Short.MAX_VALUE))
            .addGroup(UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UsuarioLayout.createSequentialGroup()
                    .addContainerGap(432, Short.MAX_VALUE)
                    .addComponent(Nlabeltipo5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(466, 466, 466)))
        );

        Invitado.setBackground(new java.awt.Color(50, 68, 68));
        Invitado.setForeground(new java.awt.Color(60, 63, 65));

        NLogo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        Ntitulo1.setBackground(new java.awt.Color(255, 255, 255));
        Ntitulo1.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Ntitulo1.setForeground(new java.awt.Color(255, 255, 255));
        Ntitulo1.setText("Nuevo Ingreso");

        NlabelPlaca1.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca1.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca1.setText("Identificación");

        telefonovisitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        telefonovisitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                telefonovisitanteActionPerformed(evt);
            }
        });

        Nlabeltipo2.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo2.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo2.setText("La razón de ingresos");

        opcion2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));
        opcion2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcion2ActionPerformed(evt);
            }
        });

        Nbotonregistrar2.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar2.setText("Registrar y generar QR");
        Nbotonregistrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar2ActionPerformed(evt);
            }
        });

        Nbotonregistrar3.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar3.setText("Regresar");
        Nbotonregistrar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar3ActionPerformed(evt);
            }
        });

        identificacionvisitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        identificacionvisitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificacionvisitanteActionPerformed(evt);
            }
        });

        Nlabeltipo3.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo3.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo3.setText("Teléfono");

        correovisitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        correovisitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                correovisitanteActionPerformed(evt);
            }
        });

        NlabelPlaca4.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca4.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca4.setText("Primer nombre");

        nombre1visitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        nombre1visitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombre1visitanteActionPerformed(evt);
            }
        });

        nombre2visitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        nombre2visitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombre2visitanteActionPerformed(evt);
            }
        });

        Nlabeltipo9.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo9.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo9.setText("Segundo nombre");

        Nlabeltipo10.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo10.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo10.setText("Correo");

        apellido1visitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        apellido1visitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apellido1visitanteActionPerformed(evt);
            }
        });

        NlabelPlaca6.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca6.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca6.setText("Primer apellido");

        Nlabeltipo12.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo12.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo12.setText("Segundo apellido");

        apellido2visitante.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        apellido2visitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apellido2visitanteActionPerformed(evt);
            }
        });

        tipovisitante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Visitante" }));
        tipovisitante.setToolTipText("");
        tipovisitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipovisitanteActionPerformed(evt);
            }
        });

        Nlabeltipo7.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo7.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo7.setText("Tipo de usuario");

        tarjetavisitante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No" }));
        tarjetavisitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tarjetavisitanteActionPerformed(evt);
            }
        });

        Nlabeltipo14.setBackground(new java.awt.Color(255, 255, 255));
        Nlabeltipo14.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Nlabeltipo14.setForeground(new java.awt.Color(255, 255, 255));
        Nlabeltipo14.setText("Tarjeta");

        javax.swing.GroupLayout InvitadoLayout = new javax.swing.GroupLayout(Invitado);
        Invitado.setLayout(InvitadoLayout);
        InvitadoLayout.setHorizontalGroup(
            InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InvitadoLayout.createSequentialGroup()
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addGap(804, 804, 804)
                        .addComponent(Ntitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addGap(851, 851, 851)
                        .addComponent(NLogo1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addGap(616, 616, 616)
                        .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InvitadoLayout.createSequentialGroup()
                                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(identificacionvisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(NlabelPlaca1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(telefonovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Nlabeltipo3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(InvitadoLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(Nlabeltipo10, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(correovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(InvitadoLayout.createSequentialGroup()
                                    .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(apellido1visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(NlabelPlaca6, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(27, 27, 27)
                                    .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(apellido2visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Nlabeltipo12, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(InvitadoLayout.createSequentialGroup()
                                    .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(nombre1visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(NlabelPlaca4, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(opcion2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Nlabeltipo2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(27, 27, 27)
                                    .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nombre2visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Nlabeltipo9, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(tipovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Nlabeltipo7, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tarjetavisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Nlabeltipo14, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addGap(784, 784, 784)
                        .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Nbotonregistrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Nbotonregistrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(6444, Short.MAX_VALUE))
        );
        InvitadoLayout.setVerticalGroup(
            InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InvitadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NLogo1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Ntitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NlabelPlaca1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Nlabeltipo3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(identificacionvisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(telefonovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NlabelPlaca4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Nlabeltipo9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombre1visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nombre2visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NlabelPlaca6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Nlabeltipo12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(apellido1visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(apellido2visitante, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addComponent(Nlabeltipo2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(opcion2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addComponent(Nlabeltipo7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tipovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(InvitadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addComponent(Nlabeltipo10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(correovisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(InvitadoLayout.createSequentialGroup()
                        .addComponent(Nlabeltipo14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tarjetavisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addComponent(Nbotonregistrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(177, 177, 177))
        );

        Consulta.setBackground(new java.awt.Color(50, 68, 68));
        Consulta.setForeground(new java.awt.Color(60, 63, 65));

        Ntitulo.setBackground(new java.awt.Color(255, 255, 255));
        Ntitulo.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 36)); // NOI18N
        Ntitulo.setForeground(new java.awt.Color(255, 255, 255));
        Ntitulo.setText("Consultar Usuario");

        NlabelPlaca.setBackground(new java.awt.Color(255, 255, 255));
        NlabelPlaca.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        NlabelPlaca.setForeground(new java.awt.Color(255, 255, 255));
        NlabelPlaca.setText("Identificación");

        Nbotonregistrar.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar.setText("Cerrar Sesión");
        Nbotonregistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NbotonregistrarActionPerformed(evt);
            }
        });

        Nbotonregistrar1.setBackground(new java.awt.Color(0, 102, 102));
        Nbotonregistrar1.setText("Consultar");
        Nbotonregistrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nbotonregistrar1ActionPerformed(evt);
            }
        });

        identificacion1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        identificacion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificacion1ActionPerformed(evt);
            }
        });

        NLogo5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/1200px-U._Cooperativa_de_Colombia_logo.svg_.png"))); // NOI18N

        javax.swing.GroupLayout ConsultaLayout = new javax.swing.GroupLayout(Consulta);
        Consulta.setLayout(ConsultaLayout);
        ConsultaLayout.setHorizontalGroup(
            ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConsultaLayout.createSequentialGroup()
                .addGap(750, 750, 750)
                .addGroup(ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ConsultaLayout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(identificacion1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Nbotonregistrar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Nbotonregistrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(NlabelPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addComponent(Ntitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConsultaLayout.createSequentialGroup()
                        .addComponent(NLogo5, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(136, 136, 136)))
                .addGap(545, 545, 545))
        );
        ConsultaLayout.setVerticalGroup(
            ConsultaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ConsultaLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(NLogo5, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Ntitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(NlabelPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(identificacion1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Nbotonregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Menu.setText("Menú");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Salir");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        Menu.add(jMenuItem4);

        jMenuBar1.add(Menu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Invitado2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Consulta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Invitado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(12, 12, 12)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 1064, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Consulta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Invitado2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(Invitado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NbotonregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NbotonregistrarActionPerformed
        login.setVisible(true);
        Consulta.setVisible(false);
    }//GEN-LAST:event_NbotonregistrarActionPerformed
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    private void Nbotonregistrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar1ActionPerformed
        if(identificacion1.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Hay campos vacios!!");
        }else{
            
            consulta(identificacion1.getText());
            
        }
        
    }//GEN-LAST:event_Nbotonregistrar1ActionPerformed
    private void identificacion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificacion1ActionPerformed
    }//GEN-LAST:event_identificacion1ActionPerformed

    private void identificacion3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificacion3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificacion3ActionPerformed

    private void Nbotonregistrar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar5ActionPerformed
        Usuario.setVisible(false);
        Consulta.setVisible(true);
    }//GEN-LAST:event_Nbotonregistrar5ActionPerformed

    private void Nbotonregistrar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar4ActionPerformed
       
       
    }//GEN-LAST:event_Nbotonregistrar4ActionPerformed

    private void opcion3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcion3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_opcion3ActionPerformed

    private void Correo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Correo3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Correo3ActionPerformed

    private void identificacionvisitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificacionvisitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificacionvisitanteActionPerformed

    private void Nbotonregistrar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar3ActionPerformed
        Invitado.setVisible(false);
        Consulta.setVisible(true);
    }//GEN-LAST:event_Nbotonregistrar3ActionPerformed

    private void Nbotonregistrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar2ActionPerformed
       if(identificacionvisitante.getText().equals("") || nombre1visitante.getText().equals("") || nombre2visitante.getText().equals("") || apellido1visitante.getText().equals("") || apellido2visitante.getText().equals("") || telefonovisitante.getText().equals("") || correovisitante.getText().equals("") ){
            JOptionPane.showMessageDialog(null, "Hay campos vacios!!");
        }else { 
        String opcion = (String) opcion2.getSelectedItem();
        String[] parts = opcion.split(":");
        String part1 = parts[0];  
        registro();
        control(identificacionvisitante.getText(), "100", part1);
        generarQR("UCC "+identificacionvisitante.getText()+" "+part1+ " "+tipo, identificacionvisitante.getText());
        correo(correovisitante.getText(), identificacionvisitante.getText());
        JOptionPane.showMessageDialog(null, "Correo enviado y Qr generado con éxito! " );
        Invitado.setVisible(false);
        Consulta.setVisible(true);
       }
    }//GEN-LAST:event_Nbotonregistrar2ActionPerformed

    private void opcion2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcion2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_opcion2ActionPerformed

    private void telefonovisitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telefonovisitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_telefonovisitanteActionPerformed

    private void correovisitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_correovisitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_correovisitanteActionPerformed

    private void Correo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Correo4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Correo4ActionPerformed

    private void opcion4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcion4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_opcion4ActionPerformed

    private void Nbotonregistrar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar6ActionPerformed
        // metodos usuario
        if(Correo4.getText().equals("")){
             JOptionPane.showMessageDialog(null, "El campo del correo esta vacio!´" );
        }else {       
            
            String opcion = (String) opcion4.getSelectedItem();
            String[] parts = opcion.split(":");
            String part1 = parts[0];  
            control(identificacion4.getText(), tipo, part1);
            generarQR("UCC "+identificacion4.getText()+" "+part1+ " "+tipo, identificacion4.getText());
            correo(Correo4.getText(), identificacion4.getText());
            JOptionPane.showMessageDialog(null, "Correo enviado y Qr generado con éxito! " );
            Invitado2.setVisible(false);
            Consulta.setVisible(true);
            
        }
       
    }//GEN-LAST:event_Nbotonregistrar6ActionPerformed

    private void Nbotonregistrar7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar7ActionPerformed
        Invitado2.setVisible(false);
        Consulta.setVisible(true);
    }//GEN-LAST:event_Nbotonregistrar7ActionPerformed

    private void identificacion4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificacion4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificacion4ActionPerformed

    private void Nbotonregistrar28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar28ActionPerformed
      String pwd = new String(password.getPassword());
      verificar(usuario.getText(), pwd);  
        
    }//GEN-LAST:event_Nbotonregistrar28ActionPerformed

    private void Nbotonregistrar29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar29ActionPerformed
       dispose();
    }//GEN-LAST:event_Nbotonregistrar29ActionPerformed

    private void usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usuarioActionPerformed

    private void Correo8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Correo8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Correo8ActionPerformed

    private void opcion8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcion8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_opcion8ActionPerformed

    private void Nbotonregistrar30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nbotonregistrar30ActionPerformed

    private void Nbotonregistrar31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nbotonregistrar31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nbotonregistrar31ActionPerformed

    private void identificacion16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificacion16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_identificacion16ActionPerformed

    private void nombre1visitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombre1visitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre1visitanteActionPerformed

    private void nombre2visitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombre2visitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre2visitanteActionPerformed

    private void apellido1visitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apellido1visitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_apellido1visitanteActionPerformed

    private void apellido2visitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apellido2visitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_apellido2visitanteActionPerformed

    private void tipovisitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipovisitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipovisitanteActionPerformed

    private void tarjetavisitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tarjetavisitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tarjetavisitanteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Consulta;
    private javax.swing.JTextField Correo3;
    private javax.swing.JTextField Correo4;
    private javax.swing.JTextField Correo8;
    private javax.swing.JPanel Invitado;
    private javax.swing.JPanel Invitado2;
    private javax.swing.JMenu Menu;
    private javax.swing.JLabel NLogo1;
    private javax.swing.JLabel NLogo14;
    private javax.swing.JLabel NLogo15;
    private javax.swing.JLabel NLogo2;
    private javax.swing.JLabel NLogo4;
    private javax.swing.JLabel NLogo5;
    private javax.swing.JButton Nbotonregistrar;
    private javax.swing.JButton Nbotonregistrar1;
    private javax.swing.JButton Nbotonregistrar2;
    private javax.swing.JButton Nbotonregistrar28;
    private javax.swing.JButton Nbotonregistrar29;
    private javax.swing.JButton Nbotonregistrar3;
    private javax.swing.JButton Nbotonregistrar30;
    private javax.swing.JButton Nbotonregistrar31;
    private javax.swing.JButton Nbotonregistrar4;
    private javax.swing.JButton Nbotonregistrar5;
    private javax.swing.JButton Nbotonregistrar6;
    private javax.swing.JButton Nbotonregistrar7;
    private javax.swing.JLabel NlabelPlaca;
    private javax.swing.JLabel NlabelPlaca1;
    private javax.swing.JLabel NlabelPlaca14;
    private javax.swing.JLabel NlabelPlaca15;
    private javax.swing.JLabel NlabelPlaca2;
    private javax.swing.JLabel NlabelPlaca3;
    private javax.swing.JLabel NlabelPlaca4;
    private javax.swing.JLabel NlabelPlaca6;
    private javax.swing.JLabel Nlabeltipo10;
    private javax.swing.JLabel Nlabeltipo12;
    private javax.swing.JLabel Nlabeltipo13;
    private javax.swing.JLabel Nlabeltipo14;
    private javax.swing.JLabel Nlabeltipo15;
    private javax.swing.JLabel Nlabeltipo16;
    private javax.swing.JLabel Nlabeltipo2;
    private javax.swing.JLabel Nlabeltipo3;
    private javax.swing.JLabel Nlabeltipo4;
    private javax.swing.JLabel Nlabeltipo5;
    private javax.swing.JLabel Nlabeltipo6;
    private javax.swing.JLabel Nlabeltipo7;
    private javax.swing.JLabel Nlabeltipo8;
    private javax.swing.JLabel Nlabeltipo9;
    private javax.swing.JLabel Ntitulo;
    private javax.swing.JLabel Ntitulo1;
    private javax.swing.JLabel Ntitulo10;
    private javax.swing.JLabel Titulo;
    private javax.swing.JLabel Titulo1;
    private javax.swing.JLabel Titulo4;
    private javax.swing.JPanel Usuario;
    private javax.swing.JPanel Usuario3;
    private javax.swing.JTextField apellido1visitante;
    private javax.swing.JTextField apellido2visitante;
    private javax.swing.JTextField correovisitante;
    private javax.swing.JTextField identificacion1;
    private javax.swing.JTextField identificacion16;
    private javax.swing.JTextField identificacion3;
    private javax.swing.JTextField identificacion4;
    private javax.swing.JTextField identificacionvisitante;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel login;
    private javax.swing.JTextField nombre1visitante;
    private javax.swing.JTextField nombre2visitante;
    private javax.swing.JComboBox<String> opcion2;
    private javax.swing.JComboBox<String> opcion3;
    private javax.swing.JComboBox<String> opcion4;
    private javax.swing.JComboBox<String> opcion8;
    private javax.swing.JPasswordField password;
    private javax.swing.JComboBox<String> tarjetavisitante;
    private javax.swing.JTextField telefonovisitante;
    private javax.swing.JComboBox<String> tipovisitante;
    private javax.swing.JTextField usuario;
    // End of variables declaration//GEN-END:variables
}

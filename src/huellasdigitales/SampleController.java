/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package huellasdigitales;

import com.sun.glass.ui.Window;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author CarlosEscobar
 */
public class SampleController implements Initializable {    
    @FXML
    private Label descripcion;
    @FXML
    private Label nombre;
    @FXML
    private Pane firmar;
    @FXML
    private Pane verificar;
    @FXML
    private Pane documentos;
    @FXML
    private ImageView img;
    @FXML
    private Pane pan1;
    @FXML
    private Pane pan2;
    @FXML
    private Pane iniciar;
    @FXML
    private Pane nueva;
    @FXML
    private Pane dragdrop;
    @FXML
    private Pane sencillo;
    @FXML
    private TextField fila0x;
    @FXML
    private Pane paneconfirm;
    @FXML
    private Button completeReg;
    @FXML
    private TextField usuariotf;
    @FXML
    private PasswordField usuariopf;
    @FXML
    private PasswordField usuariopf1;   
    @FXML
    private TextField usuarioIjtf;
    @FXML
    private PasswordField usuarioIjpf;       
    @FXML
    private Label drop;
    
    boolean respuestaR = false;
    boolean respuestaI = false;
    Image imagen;
    private Conexion con = null;
    java.sql.ResultSet resultado;
    String id_usuario;
    @FXML
    public void entrar() throws SQLException {        
        if(encontrarUser()){
                this.nombre.setText(resultado.getString("user"));
                pan1.setVisible(false);
                pan2.setVisible(true); 
                drop.setOnDragOver(new EventHandler<DragEvent>(){
            @Override
            public void handle(DragEvent t) {
                Dragboard db = t.getDragboard();
                if(db.hasFiles()){
                    t.acceptTransferModes(TransferMode.COPY);
                }
                else{
                    t.consume();
                }
            }
            
        });
        drop.setOnDragDropped(new EventHandler<DragEvent>(){

            @Override
            public void handle(DragEvent t) {
                Dragboard db = t.getDragboard();
                if(db.hasFiles()){
                    for(File file: db.getFiles()){
                        try {
                            java.sql.ResultSet r;
                            BufferedImage foto = ImageIO.read(file);
                            r = con.buscar("SELECT * FROM practica.registros ORDER BY id DESC LIMIT 1");
                            if(r == null) {
                                System.out.println("entre");
                                ImageIO.write(foto, "png", new File("1.png"));
                                try {
                                    firmar_imagen();
                                } catch (SQLException ex) {
                                    Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else {
                                try {
                                    ImageIO.write(foto, "png", new File("src/"+(Integer.parseInt(r.getString("id"))+1) +".png"));
                                    firmar_imagen();
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Error, intente de nuevo");
                                }
                            }
                            
                            System.out.println("Ruta: "+ file.getAbsolutePath());
                        } catch ( IOException ex) {
                            Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                    }
                }
            }
            
        });
        }
        else{
            JOptionPane.showMessageDialog(null, "Error en el login");
        }

    }
    @FXML
    public void salir() {
        Platform.exit();
    }
    @FXML
    public void minimizar() {
        Window.getFocusedWindow().minimize(true);
    }
    @FXML
    public void firmar() {
        cleanall();
        descripcion.setText("Firma un documento con tus propios campos y contraseña para una gran seguridad.");
        firmar.setVisible(true);
        imagen = new Image("huellasdigitales/n1.png");
        img.setImage(imagen);
        dragdrop.setVisible(true);
        sencillo.setVisible(false);
    }
    @FXML
    public void verificar() {
        cleanall();
        descripcion.setText("Verifica que una imagen haya sido alguna vez firmada.");
        verificar.setVisible(true);
        imagen = new Image("huellasdigitales/n3.png");
        img.setImage(imagen);
    }
    @FXML
    public void documentos() throws SQLException {
        cleanall();
        descripcion.setText("Mira todos tus archivos expedidos y de tu propiedad.");
        documentos.setVisible(true);
        imagen = new Image("huellasdigitales/n2.png");
        img.setImage(imagen);
        java.sql.ResultSet r;
        r = con.buscar("SELECT id FROM registros LIMIT 5");
        int ent = 0;
        while(r.next()){
            ent++;
            ImageView i  = new ImageView();
            System.out.println(r.getString("id")+".png");
            i.setImage(new Image(r.getString("id")+".png"));
            i.setLayoutY(100);
            i.setLayoutX(20+ent*10);
            i.setFitHeight(100);
            documentos.getChildren().add(i);
        }
        
    }
    @FXML
    public void irnuevo() {
        iniciar.setVisible(false);
        nueva.setVisible(true);
    }
    @FXML
    public void iriniciar() {
        iniciar.setVisible(true);
        nueva.setVisible(false);
    }
    @FXML
    public void irmodosencillo() {
        sencillo.setVisible(true);
        dragdrop.setVisible(false);
        addfila();
        num=0;
    }
    @FXML
    public void perfil() {
        
    }
    int y=126;
    int num=0;
    TextField textField;
    TextField textFieldx;
    @FXML
    public void addfila() {
        textField = new TextField();
        textField.setLayoutX(106);
        textField.setLayoutY(y);
        textField.setId("fila"+num+"x");
        sencillo.getChildren().add(textField);
        textFieldx = new TextField();
        textFieldx.setLayoutX(256);
        textFieldx.setLayoutY(y);
        textFieldx.setId("fila"+num+"y");
        sencillo.getChildren().add(textFieldx);
        y=y+25;
        num++;
    }
    public void firmar_imagen() throws SQLException {
        
        if(con.insertar("INSERT INTO `practica`.`registros` (`firma_xml`, `id_usuario`) VALUES ('sad', '"+id_usuario+"')"))
            JOptionPane.showMessageDialog(null,"Subida exitosa");
        else
            JOptionPane.showMessageDialog(null,"Error en la subida, intente de nuevo");
        
    }
    @FXML
    public void enviarfirma() {
        textField.setText("holli");

    }
    public void cleanall() {
        firmar.setVisible(false);
        verificar.setVisible(false);
        documentos.setVisible(false);
    }
    
    @FXML   
    private void confirmar(){
        nueva.setVisible(false);
        paneconfirm.setVisible(true);
    }
    @FXML
    private void RegistroCompletado(ActionEvent event){
        if((usuariotf.getLength()<100)&&(usuariopf.getLength()<46)&&(usuariopf1.equals(usuariopf))){
            respuestaR = true;        
            paneconfirm.setVisible(false);
            iniciar.setVisible(true);
            con.insertar("INSERT INTO `usuarios` (`user`,`password`) VALUES("+usuariotf.getText()+",'"+usuariopf.getText()+"');");
        }
        else{
        
        }
    }
    @FXML
    private void RegistroIgnorado(ActionEvent event){
        respuestaR = false;
        paneconfirm.setVisible(false);
        nueva.setVisible(true);
    }    
    @FXML
    private boolean encontrarUser(){
        
        System.out.println("SELECT * FROM practica.usuarios WHERE user='"+usuarioIjtf.getText()+"' AND password='"+usuarioIjpf.getId()+"'");
        resultado = con.buscar("SELECT * FROM practica.usuarios WHERE user='"+usuarioIjtf.getText()+"' AND password='"+usuarioIjpf.getText()+"'");
        if(resultado == null) return false;
        try {
            if(resultado.first() == true){
                    id_usuario = resultado.getString("id");
                    return true;
            }
            else{
                    return false;
            }   
           
        } catch (SQLException ex) {
            
            return false;
        }        
    }        

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            con = new Conexion(Conexion.usrBD, Conexion.passBD, "practica");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
        }
        pan1.setVisible(true);
        pan2.setVisible(false);
        dragdrop.setVisible(true);
        sencillo.setVisible(false);
        /* Funcionalidad Drag&Drop */
         
        firmar();
        
    }    
    
}

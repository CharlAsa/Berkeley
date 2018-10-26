/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timesocket;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author Charles
 */
public class ServidorBerkeley extends javax.swing.JFrame {

    
    static int numClient = 0;
    static int total = 2;
    
    //media
    //static int hM, mM, sM;
    
    //servidor
    static int hS = 0, mS = 0, sS = 0;
    
    //limite
    static int limH = -1, limM = -1, limS = -1;
    
    //controlar loop nos cliente
    static boolean[] flag;
    
    //armazenar tempo do cliente para calcular
    static int[] temposCliente;
    //Colocar em outra thread para controlar numeros de clientes para conectar
    
    //Flag do limite
    static boolean[] flagL;

    static Socket cliente;
    
    static Thread[] clientes;
    
    private static class tratarCliente extends Thread{
        
        private Socket cliente;
        private int pos;
        
        public tratarCliente(Socket cliente, int pos){
            this.cliente = cliente;
            this.pos = pos;
        }
        
        @Override
        public void run(){
            try{
                //System.out.println("abbb\n\na");
                PrintStream enviar = new PrintStream(cliente.getOutputStream());

                Scanner receber = new Scanner(cliente.getInputStream());

                Date d = new Date(System.currentTimeMillis());
                Calendar c = Calendar.getInstance();
                
                String tempo_cliente = hS + " " + mS + " " + sS;
                
                enviar.println(tempo_cliente);

                tempo_cliente = receber.nextLine();
                
                String[] man = tempo_cliente.split(" ");
                
                temposCliente[pos] = Integer.parseInt(man[0]);
                temposCliente[pos + 1] = Integer.parseInt(man[1]);
                temposCliente[pos + 2] = Integer.parseInt(man[2]);
                
                if(temposCliente[pos] < 0 && temposCliente[pos + 1] > 1){
                    temposCliente[pos] += 1;
                    temposCliente[pos + 1] = (60 - temposCliente[pos + 1]) * -1;
                }
                if(temposCliente[pos + 1] < 0 && temposCliente[pos + 2] > 1){
                    temposCliente[pos + 1] += 1;
                    temposCliente[pos + 2] = (60 - temposCliente[pos + 2]) * -1;
                }
                
                if(temposCliente[pos] > 0 && temposCliente[pos + 1] < 0){
                    temposCliente[pos] -= 1;
                    temposCliente[pos + 1] = (temposCliente[pos + 1] + 60);
                }
                if(temposCliente[pos + 1] > 0 && temposCliente[pos + 2] < 0){
                    temposCliente[pos + 1] -= 1;
                    temposCliente[pos + 2] = (temposCliente[pos + 2] + 60);
                }
                
                flag[pos/3] = false;
                
                while(!flag[pos/3]){
                    Thread.sleep(1);
                }
                
                temposCliente[pos] = temposCliente[(total + 1) * 3 - 3] - temposCliente[pos];
                temposCliente[pos + 1] = temposCliente[(total + 1) * 3 - 2] - temposCliente[pos + 1];
                temposCliente[pos + 2] = temposCliente[(total + 1) * 3 - 1] - temposCliente[pos + 2];

                if(!flagL[pos/3]){
                    man[0] = "999";
                    man[1] = "999";
                    man[2] = "999";
                }
                else{
                    man[0] = Integer.toString(temposCliente[pos]);
                    man[1] = Integer.toString(temposCliente[pos + 1]);
                    man[2] = Integer.toString(temposCliente[pos + 2]);
                }
                
                enviar.println(man[0] + " " + man[1] + " " + man[2]);
                
                enviar.close();
                receber.close();
                cliente.close();
            }
            catch(IOException e){
                
            } catch (InterruptedException ex) {

            }
        }
    }

    public class con extends Thread{
        @Override
        public void run(){
            int pos = 0;
            try{
                ServerSocket servidor = new ServerSocket(60000);
                temposCliente = new int[(total + 1) * 3];
                clientes = new Thread[total];
                while(true){
                    cliente = servidor.accept();

                    Thread c = new tratarCliente(cliente, pos);

                    pos += 3;
                    
                    clientes[(pos/3) - 1] = c;

                    numClient++;
                    
                    if(numClient == total){
                        Date d = new Date(System.currentTimeMillis());
                        Calendar ca = Calendar.getInstance();
                        ca.setTime(d);

                        //Caso queira pegar o tempo do computador ao invés do simulador
                        hS = ca.get(Calendar.HOUR_OF_DAY);
                        mS = ca.get(Calendar.MINUTE);
                        sS = ca.get(Calendar.SECOND);

                        try{
                            hS = Integer.parseInt(txtH.getText());
                        }
                        catch(NumberFormatException e){

                        }

                        try{
                            mS = Integer.parseInt(txtM.getText());
                        }
                        catch(NumberFormatException e){
                            
                        }
                        
                        try{
                            sS = Integer.parseInt(txtS.getText());
                        }
                        catch(NumberFormatException e){
                            
                        }
                        
                        
                        
                        try{
                            limH = Integer.parseInt(txtHlim.getText());
                        }
                        catch(NumberFormatException e){
                            
                        }
                        
                        try{
                            limM = Integer.parseInt(txtMlim.getText());
                        }
                        catch(NumberFormatException e){
                            
                        }
                        
                        try{
                            limS = Integer.parseInt(txtSlim.getText());
                        }
                        catch(NumberFormatException e){
                            
                        }
                        
                        flag = new boolean[total];
                        flagL = new boolean[total];

                        for(int k = 0; k < total; k++){
                            flag[k] = true;
                            flagL[k] = true;
                        }
                                                
                        for(Thread t : clientes){
                            t.start();
                        }
                        
                        break;
                    }
                }
                
                //espera cliente mandar os tempos
                while(true){
                    boolean bandeira = true;
                    for(int k = 0; k < total; k++){
                        if(flag[k]){
                           bandeira = false;
                           break; 
                        }
                    }
                    if(bandeira){
                        break;
                    }
                    Thread.sleep(1);
                }

                int tot = total;
                int aux = 0;
                for(int num = 1; num <= total; num++){
                    if(temposCliente[aux] >= limH * -1 && temposCliente[aux+1] >= limM * -1 && temposCliente[aux+2] >= limS * -1 &&    temposCliente[aux] <= limH && temposCliente[aux + 1] <= limM && temposCliente[aux + 2] <= limS || limH < 0 || limM < 0 || limS < 0){
                        temposCliente[pos] = temposCliente[pos] + temposCliente[aux];
                        temposCliente[pos + 1] = temposCliente[pos + 1] + temposCliente[aux + 1];
                        temposCliente[pos + 2] = temposCliente[pos + 2] + temposCliente[aux + 2];  
                    }
                    else{
                        flagL[num - 1] = false;
                        tot = tot - 1;
                    }
                    aux+=3;
                }
                temposCliente[pos] /= tot + 1;
                temposCliente[pos + 1] /= tot + 1;
                temposCliente[pos + 2] /= tot + 1;

                for(int k = 0; k < total; k++){
                    flag[k] = true;
                }
                
                for(Thread t : clientes){
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                    }
                }
                servidor.close();

                hS = hS + temposCliente[pos];
                mS = mS + temposCliente[pos + 1];
                sS = sS + temposCliente[pos + 2];
                
                if(sS >= 60){
                    sS -= 60;
                    mS += 1;
                }
                if(mS >= 60){
                    mS -= 60;
                    hS += 1;
                }

                JOptionPane.showMessageDialog(null, "SERVIDOR - HORAS: " + hS + " MINUTOS: " + mS + " SEGUNDOS: " + sS);   
            }
            catch (IOException e){
                
            } catch (InterruptedException ex) {
                
            }
            numClient = 0;
            jButton1.setVisible(true);
        }
    }
    
    /**
     * Creates new form ServidorBerkeley
     */
    public ServidorBerkeley() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtQtd = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtHlim = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMlim = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSlim = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtH = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtM = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtS = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Quantidade de cliente:");

        jLabel2.setText("Limite:");

        jLabel3.setText("H");

        txtHlim.setText("-1");

        jLabel4.setText("M");

        txtMlim.setText("-1");

        jLabel5.setText("S");

        txtSlim.setText("-1");

        jButton1.setText("Inicializar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel6.setText("Horas:");

        jLabel7.setText("H");

        txtH.setText("-1");

        jLabel8.setText("M");

        txtM.setText("-1");

        jLabel9.setText("S");

        txtS.setText("-1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtQtd, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(147, 147, 147)
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtH, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtM, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtS, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButton1)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtHlim, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMlim, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtSlim, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHlim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtMlim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtSlim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(33, 33, 33))
        );

        jLabel6.getAccessibleContext().setAccessibleName("Horas");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try{
            total = Integer.parseInt(txtQtd.getText());
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Thread con = new con();
                    con.start();
                    jButton1.setVisible(false);
                }
            });
        }
        catch(NumberFormatException e){
                
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(ServidorBerkeley.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServidorBerkeley.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServidorBerkeley.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServidorBerkeley.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServidorBerkeley().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtH;
    private javax.swing.JTextField txtHlim;
    private javax.swing.JTextField txtM;
    private javax.swing.JTextField txtMlim;
    private javax.swing.JTextField txtQtd;
    private javax.swing.JTextField txtS;
    private javax.swing.JTextField txtSlim;
    // End of variables declaration//GEN-END:variables
}

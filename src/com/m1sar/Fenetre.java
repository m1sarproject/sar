package com.m1sar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Fenetre extends JFrame {

private JPanel container = new JPanel();
private JFormattedTextField jtf = new JFormattedTextField(NumberFormat.getIntegerInstance());
private JLabel label = new JLabel("Entrez le numéro de port de la bourse");
private JButton b = new JButton ("OK");
private int portnb;


public Fenetre(){
	
  this.setTitle("Simulation Boursière");
  this.setSize(600, 600);
  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  this.setLocationRelativeTo(null);
  
  container.setBackground(Color.white);
  container.setLayout(new BorderLayout());
  JPanel top = new JPanel();   
  
  Font police = new Font("Arial", Font.BOLD, 14);
  jtf.setFont(police);
  jtf.setPreferredSize(new Dimension(150, 30));
  jtf.setForeground(Color.BLUE);
  jtf.setValue(null);
  b.addActionListener(new BoutonListener()); //défini l'action à effectuer lors d'un clique
  top.add(label);
  top.add(jtf);
  top.add(b);
 
  this.setContentPane(top);
  this.setVisible(true);            
}       

public int getPortnb() {
	return portnb;
}

public void setPortnb(int portnb) {
	this.portnb = portnb;
}

class BoutonListener implements ActionListener{
 
	public void actionPerformed(ActionEvent e) {
	  
	  String res= jtf.getValue().toString();
	  portnb=Integer.parseInt(res);

  }
}
}
package org.ofbiz.designer.util;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class ApiSearch extends JPanel {
    JList list;
    JTextField field;
    static JFrame frame;

   public ApiSearch() {
	   list = new JList();
      field = new JTextField(80);
      frame = new JFrame("ApiSearch keyword:");
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
	  
      this.setLayout(new BorderLayout());
      field.setHorizontalAlignment(SwingConstants.LEFT);
      field.addActionListener(new ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent A) {
            BuildListFrom(field.getText());
         }
      });

	  //RefreshFrame();
		  
      JScrollPane pane = new JScrollPane(list);
      JButton button = new JButton("SHOW");
      button.addActionListener(new ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent A) {
            DisplayDocFor((String)list.getSelectedValue());
         }
      });
      add(pane, BorderLayout.CENTER);
      add(field, BorderLayout.NORTH);
      add(button, BorderLayout.SOUTH);
   }

   public  void BuildListFrom(String keyword) {
      try {
         Vector vec = new Vector();
         Process proc = Runtime.getRuntime().exec("ApiSearch.bat " + keyword);
         //DataInputStream ds = new DataInputStream(proc.getInputStream());
		 BufferedReader ds = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         while ( true ) {
            String str = ds.readLine();
            if ( str == null ) break;
            vec.addElement(str);
         }
         list = new JList(vec);
         RefreshFrame();
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   public static void DisplayDocFor(String htmlFile) {
      try {
         Runtime.getRuntime().exec("explorer " + htmlFile);
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   public  static void RefreshFrame() {
	   System.err.println("1");
      frame.setContentPane(new ApiSearch());
	  System.err.println("2");
      frame.setSize(800, 300);
      frame.setVisible(true);
	  System.err.println("done set visible");
   }

   public static void main(String s[]) {
	   System.err.println("start");
	   RefreshFrame();
   }
}

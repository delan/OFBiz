/**
 *	This subclass of JFrame saves frame bounds information in a local file and uses it to initialize in
 *	subsequent invocations.  Filename is a function of class and frame title;
 */

package org.ofbiz.designer.util;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;

public class WFFrame extends JFrame {
    public WFFrame(final String title) {
        super(title);       
        final String frameLocationFile = fix(title + "_" + getClass().getName() + "_frameLoc.ini");          
        LOG.println(title + "_" + getClass().getName() + "_frameLoc.ini");

        try {
            File file = new File(frameLocationFile);
            if(file.exists()) {
                BufferedReader ds = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String str = ds.readLine();
                StringTokenizer stk = new StringTokenizer(str);
                int x = Integer.parseInt(stk.nextToken());
                int y= Integer.parseInt(stk.nextToken());
                int width= Integer.parseInt(stk.nextToken());
                int height = Integer.parseInt(stk.nextToken());

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int screenWidth = screenSize.width;
                int screenHeight = screenSize.height;
                if(x+width>screenWidth)
                    x -= (x+width-screenWidth);
                if(y+height>screenHeight)
                    y -= (y+height-screenHeight);

                setBounds(x, y, width, height);
                ds.close();
            } else {
                LOG.println("ini file does not exist ");
                setBounds(getDefaultBounds());
            }
        } catch(Exception e) {
            e.printStackTrace();
            setBounds(getDefaultBounds());
        }

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  frames.remove(WFFrame.this);
                              }
                          });

        addComponentListener(new ComponentAdapter() {
                                 public void componentResized(ComponentEvent ee) {
                                     doit();
                                 }

                                 public void componentMoved(ComponentEvent ee) {
                                     doit();
                                 }

                                 private void doit() {
                                     try {
                                         //DataOutputStream ds = new DataOutputStream(new FileOutputStream(frameLocationFile));
                                         DataOutputStream ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(frameLocationFile)));
                                         Rectangle rect = WFFrame.this.getBounds();
                                         ds.writeBytes(" " + rect.x + " " + rect.y + " " + rect.width + " " + rect.height);
                                         ds.close();
                                     } catch(Exception e) {
                                         e.printStackTrace();
                                     }
                                 }
                             });

        frames.add(this);
    }

    public Rectangle getDefaultBounds() {
        WARNING.println("CALLING NON-OVERRIDDEN METHOD");
        return new Rectangle(0, 0, 400, 300);
    }

    private static String fix(String x) {
        //int index = 0;
        int index;
        while((index=x.indexOf(">")) != -1) {
            String prefix = x.substring(0, index);
            String suffix = x.substring(index+1, x.length());
            x = prefix + suffix;
            index = x.indexOf(">");
        }
        return x;
    }

    private static HashSet frames = new HashSet();
    public static int frameCount() {
        return frames.size();
    }
}

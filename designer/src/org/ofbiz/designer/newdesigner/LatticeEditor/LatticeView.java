package org.ofbiz.designer.newdesigner.LatticeEditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import org.ofbiz.designer.domainenv.*;
import org.ofbiz.designer.roledomain.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;

import javax.swing.event.*;

abstract public class LatticeView extends JFrame implements IView {

    public static String DATA_DIR;
    public static String ADD_BUTTON_NAME;
    public static String TITLE;
    public static String ROOT_XML_TAG;
    public static File DTD_FILE;
    public static File DEFAULT_XML;

    protected Hashtable openedModels;

    protected XmlWrapper theXml;

    private DropTarget dropTarget;

    protected Container content;

    protected JScrollPane leftScroll;
    protected JScrollPane rightScroll;

    protected JPanel workspace;

    protected JPanel topPanel;
    protected JMenuBar mainMenuBar;
    protected JToolBar mainToolBar; 

    protected LatticeToolButton currTool;

    protected LatticeNodeView possibleSource;

    protected LatticeToolButton newNodeTool, arcTool; 

    protected ILatticeModel theModel;



    protected int mouseX;
    protected int mouseY;

    JList fileList;
    File[] files;

    public LatticeView() {

        super(TITLE);

        openedModels = new Hashtable();

        currTool = null;
        possibleSource = null;

        initFrame();

        content = getContentPane();
        content.setLayout(new BorderLayout());

        initTopPanel();


        initFileList();

        leftScroll = new JScrollPane(fileList);
        leftScroll.setMinimumSize(new Dimension(100,100));

        workspace = new JPanel();
        initWorkspace();

        initNodeDialog();

        workspace.setPreferredSize(new Dimension(1100,900));
        rightScroll = new JScrollPane(workspace,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JSplitPane theSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);

        content.add(theSplitPane, BorderLayout.CENTER);


        show();
    }

    private void initFrame() {

        setSize(800,700);

        addWindowListener(new WindowAdapter() {
                              public void windowDeactivated(WindowEvent e) {
                                  unselectCurrTool();
                              }});

        addMouseListener(new MouseAdapter() {
                             public void mousePressed(MouseEvent e) {
                                 unselectCurrTool();
                             }});
    }

    public void synchronize() {

        workspace.removeAll();

        createLatticeNodeViews();

        ILatticeLinkModel currRelationship = null;
        LatticeLinkView newRView = null;

        for(int i=0; i<theModel.getLatticeLinkCount(); i++) {
            currRelationship = (ILatticeLinkModel)theModel.getLatticeLinkAt(i);
            newRView = new LatticeLinkView(currRelationship);
            workspace.add(newRView);
            currRelationship.setGui(newRView);
        }

        repaint();  
    }


    private void initTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        mainMenuBar = new JMenuBar();

        JMenu fileMenu =  new JMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        newItem.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                          new FileNameDialog(LatticeView.this,FileNameDialog.NEW_MODE);
                                      }
                                  });

        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);
        saveItem.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                           theXml.saveDocument(new File(DATA_DIR+theModel.getId()+".xml"));
                                           syncFileList();     
                                       }
                                   });


        JMenuItem saveAsItem = new JMenuItem("Save As");
        fileMenu.add(saveAsItem);
        saveAsItem.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                             new FileNameDialog(LatticeView.this,FileNameDialog.SAVEAS_MODE);
                                         }
                                     });



        topPanel.add(mainMenuBar,BorderLayout.NORTH);

        mainToolBar = new JToolBar();
        topPanel.add(mainToolBar, BorderLayout.SOUTH);


        content.add(topPanel,BorderLayout.NORTH);

        newNodeTool = new LatticeToolButton(ADD_BUTTON_NAME);
        arcTool = new LatticeToolButton("New Relationship");


        mainToolBar.add(newNodeTool);
        mainToolBar.add(arcTool);

    }

    private void initWorkspace() {

        workspace.setLayout(null);

        workspace.addMouseListener(new MouseAdapter() {
                                       public void mousePressed(MouseEvent e) {
                                           if(currTool == newNodeTool) {
                                               theModel.addLatticeNode(e.getPoint(), "New");
                                           }
                                           unselectCurrTool();
                                       }
                                   });

        workspace.addMouseMotionListener(new MouseMotionAdapter() {
                                             public void mouseMoved(MouseEvent e) {
                                                 if(possibleSource!=null) {
                                                     mouseX = e.getX();
                                                     mouseY = e.getY();
                                                     repaint();
                                                 }
                                             }
                                         });             

    }

    public void select(LatticeToolButton theButton) {
        unselectCurrTool();
        currTool = theButton;
        currTool.setEnabled(false);
    }

    public void unselectCurrTool() {
        possibleSource = null;

        if(currTool == null) return;
        currTool.setEnabled(true);
        currTool = null;
        repaint();
    }

    public LatticeNodeView getPossibleSource() {
        return possibleSource;
    }

    public void setPossibleSource(LatticeNodeView aSource) {
        possibleSource = aSource;
    }

    public LatticeToolButton getCurrTool() {
        return currTool;
    }

    public LatticeToolButton getNewNodeTool() {
        return newNodeTool;
    }

    public LatticeToolButton getArcTool() {
        return arcTool;
    }



    public void paint(Graphics g) {
        super.paint(g);
        if(possibleSource != null) {
            Graphics wg = workspace.getGraphics();
            wg.drawLine(mouseX, mouseY, (int)possibleSource.getBounds().getCenterX(),
                        (int)possibleSource.getBounds().getCenterY());
        }
    }

    public void setMouseX(int anX) {
        mouseX = anX;
    }

    public void setMouseY(int aY) {
        mouseY = aY;
    }

    public IModel getModel() {
        return theModel;
    }






    public void setModel(IModel modelIn){

        if(theModel == modelIn)
            return;
        if(modelIn != null && !(modelIn instanceof IModelProxySupportClass))
            throw new RuntimeException("Model is not a Proxy");
        if(theModel != null)
            theModel.setGui(null);
        theModel = (ILatticeModel)modelIn;
        theModel.setGui(this);

        synchronize();
        //if(!(openedModels.containsKey(theModel.getId()))) openedModels.put(theModel.getId(),theModel);
    }

    public void setXML(XmlWrapper xmlIn) {
        theXml = xmlIn;
    }

    private void initFileList() {


        files = (new File(DATA_DIR)).listFiles(new FileFilter() {
                                                   public boolean accept(File aFile) {
                                                       if(aFile.getName().endsWith(".xml"))
                                                           return true;
                                                       return false;
                                                   }
                                               });

        Object[] listKeys = new Object[files.length];
        for(int i=0;i<files.length;i++) {
            listKeys[i] = files[i].getName();
        }
        fileList = new JList(listKeys);

        fileList.addListSelectionListener(new ListSelectionListener() {
                                              public void valueChanged(ListSelectionEvent e) {
                                                  if(fileList.getSelectedIndex()<0) return;
                                                  handleFileChanged();
                                              }
                                          });


    }

    protected void syncFileList() {

        files = (new File(DATA_DIR)).listFiles(new FileFilter() {
                                                   public boolean accept(File aFile) {
                                                       if(aFile.getName().endsWith(".xml"))
                                                           return true;
                                                       return false;
                                                   }
                                               });

        Object[] listKeys = new Object[files.length];
        for(int i=0;i<files.length;i++) {
            listKeys[i] = files[i].getName();
        }

        DefaultListModel newListModel = new DefaultListModel();
        for(int i=0;i<listKeys.length;i++) {
            newListModel.addElement(listKeys[i]);
        }

        fileList.setModel(newListModel);
        fileList.repaint();
    }

    protected void handleFileChanged() {
        theXml = XmlWrapper.openDocument(files[fileList.getSelectedIndex()]);

        String fileId = getIdFromXml();

        if(openedModels.containsKey(fileId)) {
            setModel((ILatticeModel)openedModels.get(fileId));
        } else {
            ILatticeModel newModel = getNewModel();
            createAppropriateTranslator(newModel, BaseTranslator.UPDATE_MODEL);
            setModel(newModel);

        }
    }   

    protected void handleNew(String newName) {
        theXml = XmlWrapper.newDocument(DTD_FILE,ROOT_XML_TAG);
        ILatticeModel newModel = getNewModel();
        LOG.println("newName: "+newName);
        newModel.setId(newName);
        newModel.setName(newName);
        createAppropriateTranslator(newModel, BaseTranslator.UPDATE_DATA);
        setModel(newModel);
        openedModels.put(theModel.getId(),theModel);
        theXml.saveDocument(new File(DATA_DIR+newName+".xml"));
        syncFileList();
    }

    protected void handleSaveAs(String newName) {
        openedModels.remove(theModel.getId());
        loseTranslator();
        theXml = XmlWrapper.newDocument(DTD_FILE,ROOT_XML_TAG);
        theModel.setId(newName);
        theModel.setName(newName);
        createAppropriateTranslator(theModel, BaseTranslator.UPDATE_DATA);
        theXml.saveDocument(new File(DATA_DIR+newName+".xml"));
        openedModels.put(theModel.getId(),theModel);
        syncFileList();
    }

    abstract protected void createAppropriateTranslator(ILatticeModel newModel,String initialTranslationDirection);
    abstract protected String getIdFromXml();
    abstract protected ILatticeModel getNewModel();
    abstract protected void createLatticeNodeViews();
    abstract protected void loseTranslator();

    abstract public void doNodePropertyEdit(ILatticeNodeModel modelIn);

    abstract void initNodeDialog();

}


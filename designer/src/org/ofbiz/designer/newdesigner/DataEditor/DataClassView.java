package org.ofbiz.designer.newdesigner.DataEditor;

import org.ofbiz.designer.pattern.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.event.*;
import org.ofbiz.designer.dataclass.*;
import javax.swing.table.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.newdesigner.DataEditor.model.IDataClassModel;
import org.ofbiz.designer.newdesigner.DataEditor.model.DataClassModel;
import org.ofbiz.designer.newdesigner.DataEditor.model.IDataClassModelWrapper;

public class DataClassView extends JFrame implements IView {
    private static final String XML_DIR = System.getProperty("WF_XMLDIR");
    private static final String DTD_DIR = System.getProperty("WF_DTDDIR");

    private static final String DATA_DIR = XML_DIR + "/data/";
    private static final String TITLE = "Data Editor";
    private static final String ROOT_XML_TAG = "DataClass";
    private static final File DTD_FILE = new File(DTD_DIR+"\\DataClass.dtd");
    private static final File DEFAULT_XML = new File(DATA_DIR+"\\TargetInfo.xml");

    private static int LHEIGHT = 20;
    private static int LWIDTH = 100;

    private Hashtable openedModels;

    private XmlWrapper theXml;

    private JScrollPane leftScroll;

    private JPanel workspace;

    private JMenuBar mainMenuBar; 

    private IDataClassModel theModel;

    private JList fileList;
    private File[] files;

    private JLabel packageLabel;
    private JLabel packageField;
    private JLabel nameLabel;
    private JLabel nameField;
    private JLabel parentLabel;
    private JLabel parentField;
    private JLabel fieldLabel;
    private JScrollPane fieldPane;
    private JLabel methodLabel;
    private JScrollPane methodPane;
    private JLabel exceptionLabel;
    private JScrollPane exceptionPane;
    private JLabel paramLabel;
    private JScrollPane paramPane;
    private JTableWithDelete fieldTable;
    private JTableWithDelete methodTable;
    private JTableWithDelete exceptionTable;
    private JTableWithDelete paramTable;

    private DocumentListener docListen;

    public DataClassView() {
        super(TITLE);

        openedModels = new Hashtable();

        initMainMenu();

        initWorkspace();

        initFileList();

        initFrame();

    }

    private void initFrame() {
        setSize(525,700);
        getContentPane().setLayout(new BorderLayout());
        leftScroll = new JScrollPane(fileList);
        leftScroll.setMinimumSize(new Dimension(100,100));
        JSplitPane theSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, workspace);
        getContentPane().add(theSplitPane, BorderLayout.CENTER);
        getContentPane().add(mainMenuBar,BorderLayout.NORTH);

        workspace.addComponentListener(new ComponentAdapter() {
                                           public void componentResized(ComponentEvent e) {
                                               layoutWorkspace();
                                           }
                                       });

    }

    public void synchronize() {

        packageField.setText(theModel.getPackage());
        nameField.setText(theModel.getName());
        parentField.setText(theModel.getParent());
        fieldTable.setModel(theModel.getFieldList());
        methodTable.setModel(theModel.getMethodList());
        methodTable.changeSelection(0,0,false,false);

        repaint();  
    }


    private void initMainMenu() {
        mainMenuBar = new JMenuBar();


        JMenu fileMenu =  new JMenu("File");
        mainMenuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        newItem.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                          new DataFileNameDialog(DataClassView.this,DataFileNameDialog.NEW_MODE); 
                                      }
                                  });

        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);
        saveItem.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                           theXml.saveDocument(new File(DATA_DIR+theModel.getName()+".xml"));
                                           syncFileList();     
                                       }
                                   });


        JMenuItem saveAsItem = new JMenuItem("Save As");
        fileMenu.add(saveAsItem);
        saveAsItem.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                             new DataFileNameDialog(DataClassView.this,DataFileNameDialog.SAVEAS_MODE);
                                         }
                                     });



    }

    private void initWorkspace() {

        workspace = new JPanel();

        workspace.setLayout(null);

        fieldTable = new JTableWithDelete();
        methodTable = new JTableWithDelete();
        exceptionTable = new JTableWithDelete();
        paramTable = new JTableWithDelete();

        fieldPane = new JScrollPane(fieldTable);
        methodPane = new JScrollPane(methodTable);
        exceptionPane = new JScrollPane(exceptionTable);
        paramPane = new JScrollPane(paramTable);

        workspace.add(fieldPane);
        workspace.add(methodPane);
        workspace.add(exceptionPane);
        workspace.add(paramPane);

        fieldLabel = new JLabel("Fields");
        fieldLabel.setSize(LWIDTH,LHEIGHT);
        workspace.add(fieldLabel);
        methodLabel = new JLabel("Methods");
        methodLabel.setSize(LWIDTH,LHEIGHT);
        workspace.add(methodLabel);
        exceptionLabel = new JLabel("Exceptions");
        exceptionLabel.setSize(LWIDTH,LHEIGHT);
        workspace.add(exceptionLabel);
        paramLabel = new JLabel("Parameters");
        paramLabel.setSize(LWIDTH,LHEIGHT);
        workspace.add(paramLabel);

        methodTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                                                                     public void valueChanged(ListSelectionEvent e) {
                                                                         int selection = methodTable.getSelectedRow();
                                                                         if(selection>=0) {
                                                                             exceptionTable.setModel(theModel.getMethodList().getExceptionListAt(selection));
                                                                             paramTable.setModel(theModel.getMethodList().getParamListAt(selection));
                                                                         }
                                                                     }
                                                                 });


        packageLabel = new JLabel("Data Domain: ");
        workspace.add(packageLabel);
        packageField = new JLabel();
        workspace.add(packageField);

        nameLabel = new JLabel("Name: ");
        workspace.add(nameLabel);
        nameField = new JLabel();
        workspace.add(nameField);

        parentLabel = new JLabel("Parent: ");
        workspace.add(parentLabel);
        parentField = new JLabel();
        workspace.add(parentField);

        packageLabel.setBounds(0,5,LWIDTH,LHEIGHT);
        packageField.setBounds(packageLabel.getWidth()+10,packageLabel.getY(),100,packageLabel.getHeight());
        nameLabel.setBounds(0,packageLabel.getY()+packageLabel.getHeight()+5,LWIDTH,LHEIGHT);
        nameField.setBounds(nameLabel.getWidth()+10,nameLabel.getY(),100,nameLabel.getHeight());
        parentLabel.setBounds(0,nameLabel.getY()+nameLabel.getHeight()+5,LWIDTH,LHEIGHT);
        parentField.setBounds(parentLabel.getWidth()+10,parentLabel.getY(),100,parentLabel.getHeight());


        layoutWorkspace();



    }

    public IModel getModel() {
        return theModel;
    }


    public void setModel(IModel modelIn) {

        if(theModel == modelIn)
            return;
        if(modelIn != null && !(modelIn instanceof IModelProxySupportClass))
            throw new RuntimeException("Model is not a Proxy");
        if(theModel != null)
            theModel.setGui(null);
        theModel = (IDataClassModel)modelIn;
        theModel.setGui(this);



        synchronize();
        if(!(openedModels.containsKey(theModel.getName())))
            openedModels.put(theModel.getName(),theModel);
    }

    public void setXML(XmlWrapper xmlIn) {
        theXml = xmlIn;
    }

    private void initFileList() {

        fileList = new JList();
        //FileListDragAdapter myDragAdapter = new FileListDragAdapter(fileList, DATA_DIR);
        FileListDragAdapter myDragAdapter = new FileListDragAdapter(fileList);


        fileList.addListSelectionListener(new ListSelectionListener() {
                                              public void valueChanged(ListSelectionEvent e) {
                                                  if(fileList.getSelectedIndex()<0) return;
                                                  handleFileChanged();
                                              }
                                          });

        syncFileList();


    }

    public static String fileToType(File aFile) {
        String fullPath = null;
        try {
            fullPath = aFile.getAbsolutePath();
        } catch(Throwable t) {
            t.printStackTrace();
        }
        String thisDirName = fullPath.substring(DATA_DIR.length());
        int nameBeginIndex = thisDirName.lastIndexOf(aFile.separator)+1;
        String className = thisDirName.substring(nameBeginIndex, thisDirName.length()-4);
        String path = "";
        if(nameBeginIndex>0) {
            path = "("+thisDirName.substring(0,nameBeginIndex-1)+")";
        }
        return className+" "+path;
    }

    public static File typeToFile(String type) {
        int pathBeginIndex = type.indexOf(" ");
        String className = type.substring(0,pathBeginIndex);
        String path = "";
        if(pathBeginIndex < (type.length()-1)) {
            path = type.substring(pathBeginIndex+1);
            path = path.substring(1,path.length()-1);
        }
        return new File(DATA_DIR+path+"\\"+className+".xml");
    }

    protected void syncFileList() {

        Vector fileBag = new Vector();
        Vector dirBag = new Vector();

        dirBag.add(new File(DATA_DIR));

        Vector workingBag;
        File currItem;
        File[] subItems;

        while(!(dirBag.isEmpty())) {
            workingBag = dirBag;
            dirBag = new Vector();

            for(int i=0;i<workingBag.size();i++) {
                currItem = (File)workingBag.get(i);
                if(currItem.isDirectory()) {
                    subItems = currItem.listFiles();
                    for(int j=0;j<subItems.length;j++) {
                        dirBag.add(subItems[j]);
                    }
                } else {
                    if(currItem.getName().toLowerCase().endsWith(".xml")) {
                        fileBag.add(currItem);
                    }
                }
            }
        }

        DefaultListModel newListModel = new DefaultListModel();
        for(int i=0;i<fileBag.size();i++) {
            newListModel.addElement(fileToType((File)fileBag.get(i)));
        }

        fileList.setModel(newListModel);
        fileList.repaint();
    }

    protected void handleFileChanged() {
        theXml = XmlWrapper.openDocument(typeToFile((String)fileList.getSelectedValue()));

        String fileId = getIdFromXml();

        if(openedModels.containsKey(fileId)) {
            setModel((IDataClassModel)openedModels.get(fileId));
        } else {
            IDataClassModel newModel = getNewModel();
            createAppropriateTranslator(newModel, BaseTranslator.UPDATE_MODEL);
            setModel(newModel);

        }
    }   

    public void handleNew(String newName, String domain, String parent) {
        LOG.println("DTD_FILE is " + DTD_FILE);
        LOG.println("ROOT_XML_TAG is " + ROOT_XML_TAG);
        theXml = XmlWrapper.newDocument(DTD_FILE,ROOT_XML_TAG);
        IDataClassModel newModel = getNewModel();
        newModel.setName(newName);
        newModel.setPackage(domain);
        newModel.setParent(parent);
        createAppropriateTranslator(newModel, BaseTranslator.UPDATE_DATA);
        setModel(newModel);
        openedModels.put(theModel.getName(),theModel);
        String fullPath = createPath(domain);
        theXml.saveDocument(new File(fullPath+"\\"+newName+".xml"));
        syncFileList();
    }

    private String createPath(String domain) {
        String restDomain = domain;
        int firstPeriod;
        String topDir;
        String currPath = DATA_DIR;
        File currPathFile;
        while(restDomain!=null) {
            firstPeriod = restDomain.indexOf("\\");
            if(firstPeriod>=0) {
                topDir = restDomain.substring(0,firstPeriod);
                restDomain = restDomain.substring(firstPeriod+1);
            } else {
                topDir = restDomain;
                restDomain = null;
            }
            currPath = currPath+"\\"+topDir;
            currPathFile = new File(currPath);
            if(!(currPathFile.exists())) {
                currPathFile.mkdir();
            }
        }
        return currPath;
    }

    public void handleSaveAs(String newName, String domain, String parent) {
        openedModels.remove(theModel.getName());
        loseTranslator();
        theXml = XmlWrapper.newDocument(DTD_FILE,ROOT_XML_TAG);
        theModel.setName(newName);
        theModel.setPackage(domain);
        theModel.setParent(parent);
        createAppropriateTranslator(theModel, BaseTranslator.UPDATE_DATA);
        String fullPath = createPath(domain);
        theXml.saveDocument(new File(fullPath+"/"+newName+".xml"));
        openedModels.put(theModel.getName(),theModel);
        syncFileList();
    }

    public IDataClassModel getNewModel() {
        return DataClassModel.createModelProxy();
    }

    public static void main(String[] args) {
        DataClassView theView = new DataClassView();
        XmlWrapper newXml;

        if(args == null || args.length==0)
            newXml = XmlWrapper.openDocument(DEFAULT_XML);
        else
            newXml = XmlWrapper.openDocument(new File(args[0]));

        theView.setXML(newXml);

        IDataClassModel theModel = theView.getNewModel();
        theView.createAppropriateTranslator(theModel,BaseTranslator.UPDATE_MODEL);
        theView.setModel(theModel);
        theView.show();
    }

    public void createAppropriateTranslator(IDataClassModel newModel, String initialTranslationDirection) {
        new DataClassTranslator((IDataClassModelWrapper) newModel,(IDataClassWrapper)theXml.getRoot(),initialTranslationDirection);
    }

    public String getIdFromXml() {
        return((IDataClassWrapper)theXml.getRoot()).getName();
    }

    protected void loseTranslator() {
        ((IDataClassModelWrapper)theModel).getTranslator().close();
        ((IDataClassModelWrapper)theModel).setTranslator(null);
    }

    public void layoutWorkspace() {
        fieldLabel.setLocation(0,parentLabel.getY()+parentLabel.getHeight()+20);
        int tableDist = (int)((workspace.getHeight()-fieldLabel.getY())/3);
        methodLabel.setLocation(0,fieldLabel.getY()+tableDist);
        paramLabel.setLocation(0,methodLabel.getY()+tableDist);
        fieldPane.setLocation(0,fieldLabel.getY()+fieldLabel.getHeight()+5);
        fieldPane.setSize(300,methodLabel.getY()-fieldPane.getY()-10);
        fieldPane.validate();
        methodPane.setLocation(0,methodLabel.getY()+methodLabel.getHeight()+5);
        methodPane.setSize(200,paramLabel.getY()-methodPane.getY()-10);
        methodPane.validate();
        exceptionLabel.setLocation(methodPane.getX()+methodPane.getWidth()+10,methodPane.getY());
        exceptionPane.setLocation(exceptionLabel.getX(),exceptionLabel.getY()+exceptionLabel.getHeight()+5);
        exceptionPane.setSize(100,methodPane.getY()+methodPane.getHeight()-exceptionPane.getY());
        exceptionPane.validate();
        paramPane.setLocation(0,paramLabel.getY()+paramLabel.getHeight()+5);
        paramPane.setSize(300,workspace.getHeight()-paramPane.getY()-10);
        paramPane.validate();
    }


}

class FileListDragAdapter extends DragAdapter {
    private JList comp;
    FileListDragAdapter(JList _comp) {
        super(_comp);
        comp = _comp;
    }
    public void writeObjectOnDrag(ObjectOutputStream os, Point dragLocation) throws Exception{
        String value = (String)comp.getSelectedValue();
        value = typeToFile(value);
        if(value.endsWith(".xml"))
            value = value.substring(0, value.length()-".xml".length());
        os.writeObject(value);
    }

    public static String typeToFile(String type) {
        type = type.trim();
        int pathBeginIndex = type.indexOf(" ");
        if(pathBeginIndex == -1) 
            return type;
        String className = type.substring(0,pathBeginIndex);
        String path = type.substring(pathBeginIndex+1);
        path = path.substring(1,path.length()-1);
        if(path.length() == 0)
            return className + ".xml";
        else
            return path+"\\"+className+".xml";
    }
}


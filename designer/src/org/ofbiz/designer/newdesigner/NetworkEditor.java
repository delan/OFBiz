package org.ofbiz.designer.newdesigner;


import java.awt.*;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;

import org.ofbiz.designer.newdesigner.operatoreditor.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.RoleDomainView;
import org.ofbiz.designer.newdesigner.LatticeEditor.DomainEnvView;
import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.newdesigner.popup.ActionEvents;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.domainenv.IDomainEnvWrapper;
import org.ofbiz.designer.pattern.XmlWrapper;
import org.ofbiz.designer.pattern.BaseTranslator;
import org.ofbiz.designer.generic.DocSaver;
import javax.swing.*;


public class NetworkEditor extends ContainerView {
    private Point popupLocation = null;

    public NetworkEditor(INetworkEditorComponentModel model) {
        super(model);

        addMouseMotionListener(new MouseMotionAdapter() {
                                   public void mouseMoved(MouseEvent e) {
                                       NetworkEditor top = top();
                                       if(top.entityAddingArc != null) {
                                           Point arcHead = e.getPoint();
                                           if(top != NetworkEditor.this) {
                                               Rectangle myBounds = top.getBoundsOf(NetworkEditor.this);
                                               top.arcHead = new Point(myBounds.x + arcHead.x, myBounds.y + arcHead.y);

                                           } else top.arcHead = new Point(arcHead.x, arcHead.y);
                                           top.repaint();
                                       }
                                   }
                               });

        addMouseListener(new MouseAdapter() {
                             public void mouseClicked(MouseEvent e) {
                                 if(e.getClickCount()>=2) {
                                     INetworkEditorComponentModelWrapper innerModel = (INetworkEditorComponentModelWrapper)NetworkEditor.this.getModel();
                                     if(innerModel.getModelType().equals(innerModel.TASKTYPE))
                                         TaskEditor.launchTaskEditor(rootTask.getXml(), innerModel.getID());
                                     else if(innerModel.getModelType().equals(innerModel.DOMAINTYPE)) {
                                         boolean enabled = !innerModel.isPrimaryDomain();
                                         Point p = popupLocation;
                                         p.x += getLocationOnScreen().x;
                                         p.y += getLocationOnScreen().y;
                                         DomainProperties.launchEditor(rootTask.getXml(), innerModel.getID(), p, enabled);
                                     }
                                 }
                             }

                             public void mousePressed(MouseEvent e) {
                                 NetworkEditor top = top();
                                 INetworkEditorComponentModelWrapper  innerModel = (INetworkEditorComponentModelWrapper)NetworkEditor.this.getModel();
                                 if(innerModel.getModelType().equals(innerModel.TASKTYPE) && getParent() instanceof NetworkEditor && top.entityAddingArc != null) {
                                     if(top.entityAddingArc instanceof NetworkEditor) {
                                         INetworkEditorComponentModel source = (INetworkEditorComponentModel)((NetworkEditor)top.entityAddingArc).getModel();
                                         INetworkEditorComponentModel destination = (INetworkEditorComponentModel)getModel();
                                         if(top.successArc) destination.createIncomingArc(source.getID(), null, null);
                                         else
                                             destination.createIncomingArc(source.getID(), null, IArcModel.DASHED);
                                     } else {
                                         IArcModel source = (IArcModel)((ArcView)top.entityAddingArc).getModel();
                                         INetworkEditorComponentModel destination = (INetworkEditorComponentModel)getModel();
                                         if(top.successArc) {
                                             IArcModel newArc = destination.createIncomingArc(source.getID(), null, null);
                                             //source.setAlternativeTransition(newArc.getID());
                                             source.setOutgoingArc(newArc);
                                         }
                                         else {
                                             IArcModel newArc = destination.createIncomingArc(source.getID(), null, IArcModel.DASHED);
                                             source.setOutgoingArc(newArc);
                                         }
                                     }
                                     top.entityAddingArc = null;
                                 }
                                 popupLocation = e.getPoint();
                             }
                         });
    }

    public void setModel(IContainerModel modelIn) {
        super.setModel(modelIn);
        setForeground(((INetworkEditorComponentModel)getModel()).getColor());
        if(getForeground() == null)
            setForeground(Color.white);
    }


    public void paint(Graphics g) {
        super.paint(g);
        if(entityAddingArc != null && arcHead != null) {
            Rectangle sourceBounds = getBoundsOf((Container)entityAddingArc);
            Arrow line = new Arrow((int)sourceBounds.getCenterX(), (int)sourceBounds.getCenterY(), arcHead.x, arcHead.y);
            int length = (int)line.getLength();
            line.setLengthFromHead(length - 25);
            Color bkup = g.getColor();
            g.setColor(Color.black);
            if(successArc)
                line.paint(g);
            else {
                Polygon[] dashed = line.containingRectDashed(10);
                for(int i=0; i<dashed.length; i++)
                    g.drawPolygon(dashed[i]);
            }
            g.setColor(bkup);
        }
    }

    private Rectangle getBoundsOf(Container subChild) {
        if(!isAncestorOf(subChild)) throw new RuntimeException("Not an ancestor");
        Rectangle bounds = subChild.getBounds();
        Container parent = subChild.getParent();
        while(parent != this) {
            bounds.x += parent.getBounds().x;
            bounds.y += parent.getBounds().y;
            parent = parent.getParent();
        }
        return bounds;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        if(myIcon != null) {
            myIcon.paintIcon(this, g, 3, 3);
            return;
        } else
            super.paintComponent(g);

        g.setColor(Color.black);
        if(!(getParent() instanceof NetworkEditor) && getComponentCount() > 0) {
            INetworkEditorComponentModelWrapper model = (INetworkEditorComponentModelWrapper)this.getModel();
            Rectangle primaryDim = model.getChildContainerAt(0).getBounds();
            int w = 3;
            for(int i=1; i<=w; i++) {
                g.drawRect(primaryDim.x-i, primaryDim.y-i, primaryDim.width+2*i-1, primaryDim.height+2*i-1);
            }
        }
        NetworkEditor root = this;
        while(root.getParent() instanceof NetworkEditor)
            root = (NetworkEditor)root.getParent();
        for(int i=0; i<getComponentCount(); i++) {
            if(getComponent(i) instanceof NetworkEditor) {
                NetworkEditor child = (NetworkEditor)getComponent(i);
                String name = child.getName();
                Rectangle b = child.getBounds();
                g.drawString(name, b.x, b.y+b.height+15);
                if(child == root.startTask) {
                    g.drawRect(b.x-boxSpacing, b.y-boxSpacing, b.width+2*boxSpacing, b.height+2*boxSpacing);
                    g.setColor(Color.green);
                    g.fillRect(b.x-boxSpacing+1, b.y-boxSpacing+1, b.width+2*boxSpacing-1, b.height+2*boxSpacing-1);
                    g.setColor(Color.black);
                }
                if(child == root.endTask) {
                    g.drawRect(b.x-boxSpacing, b.y-boxSpacing, b.width+2*boxSpacing, b.height+2*boxSpacing);
                    g.setColor(Color.red);
                    g.fillRect(b.x-boxSpacing+1, b.y-boxSpacing+1, b.width+2*boxSpacing-1, b.height+2*boxSpacing-1);
                    g.setColor(Color.black);
                }
            }
        }
        g.setColor(getForeground());
    }
    private int boxSpacing = 3;

    public static INetworkEditorComponentModelWrapper launchNE(ITaskWrapper rootTask, IDomainEnvWrapper domainEnv) {
        XmlWrapper xml = rootTask.getXml();
        INetworkEditorComponentModelWrapper model = (INetworkEditorComponentModelWrapper)NetworkEditorComponentModel.createModelProxy(rootTask.getIdAttribute());
        model.setModelType(model.WORKFLOWTYPE);
        model.setDomainURL(rootTask.getSecuritydomainurlAttribute());
        INetworkTaskRealizationWrapper realization = (INetworkTaskRealizationWrapper)rootTask.getRealization().getNetworkTaskRealization();
        String domainURL = rootTask.getSecuritydomainurlAttribute();
        new WorkflowTranslator( model, realization, domainURL, BaseTranslator.UPDATE_MODEL);
        return model;
    }

    private WFFrame frame = null;
    private void addButton(String gif, String actionEvent, JToolBar toolBar) {
        JButton button;
        if(gif.endsWith(".gif")) {
            String gifDir = System.getProperty("GIFDIR");
            button = (JButton)toolBar.add(new JButton(new ImageIcon(gifDir + "/" + gif)));
        } else button = (JButton)toolBar.add(new KButton(gif));
        button.setActionCommand(actionEvent);
        button.addActionListener(this);
    }


    static int x = 0;
    public static NetworkEditor showNE(final INetworkEditorComponentModelWrapper model, XmlWrapper taskXml, String taskName) {
        NEFrame frame = new NEFrame("Network Realization for " + taskName);
        final NetworkEditor view = new NetworkEditor(model);

        frame.setTitle(frame.getTitle() + " " + x++);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(view, BorderLayout.CENTER);
        view.frame = frame;
        WFMenuBar menuBar = new WFMenuBar();
        JToolBar toolBar = new JToolBar();

        view.addButton("open.gif", ActionEvents.OPEN, toolBar);
        view.addButton("new.gif", ActionEvents.NEW, toolBar);
        view.addButton("save.gif", ActionEvents.SAVE, toolBar);
        view.addButton("TaskEditor", ActionEvents.TASK_ED, toolBar);
        view.addButton("DataEditor", ActionEvents.DATA_ED, toolBar);
        view.addButton("DomainEditor", ActionEvents.DOMAIN_ED, toolBar);
        view.addButton("RoleDomainEditor", ActionEvents.ROLE_ED, toolBar);
        view.addButton("Split!", ActionEvents.SPLIT_DESIGN, toolBar);

        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        menuBar.addActionListener(view);
        frame.setJMenuBar(menuBar);
        model.setSize(frame.getSize());
        view.synchronize();

        // to enable "spacing" in the output console
        org.ofbiz.designer.util.ConsoleSpacer.init();

        DocSaver.add(taskXml, frame);
        frame.setVisible(true);
        return view;
    }

    private static IDomainEnvWrapper domainEnv = null;
    private static ITaskWrapper rootTask = null;

    public static void main(String[] args) throws Exception{
        if(args.length != 3) {
            WARNING.println("enter <filename> <roottask>  <domainEnvironment> as parameters");
            WARNING.println("ensure both files are in current directory");
            return;
        }
        String fileName = args[0];
        String rootTaskName = args[1];
        String domainEnvFile = args[2];

        if(!fileName.endsWith(".xml")) fileName += ".xml";
        if(!domainEnvFile.endsWith(".xml")) domainEnvFile += ".xml";

        XmlWrapper taskXml = XmlWrapper.openDocument(new File(fileName));
        XmlWrapper domainXml = XmlWrapper.openDocument(new File(domainEnvFile));

        rootTask =  (ITaskWrapper)taskXml.getIdRef(rootTaskName);
        domainEnv = (IDomainEnvWrapper)domainXml.getRoot();

        INetworkEditorComponentModelWrapper model1  = launchNE(rootTask, domainEnv);
        showNE(model1, taskXml, rootTask.getNameAttribute());
        //INetworkEditorComponentModelWrapper model2  = launchNE(rootTask, domainEnv);
        //showNE(model2, taskXml, rootTask.getNameAttribute());
    }

    private void autoMap() {
        // automap for all arcs
        XmlWrapper xml = rootTask.getXml();
        INetworkDesign context = (INetworkDesign)xml.getRoot();
        int count = context.getArcCount();
        for(int i=0;i<count;i++) {
            IArcWrapper arc = (IArcWrapper)context.getArcAt(i);
            autoMapArc(arc);
        }

        // automap for all network tasks
        count = context.getTaskCount();
        for(int i=0;i<count;i++) {
            ITaskWrapper task = (ITaskWrapper)context.getTaskAt(i);
            if(task.getRealization().getNetworkTaskRealization() != null)
                autoMapNetworkTask(task);
        }
    }

    private void autoMapArc(IArcWrapper arc) {
        XmlWrapper xml = rootTask.getXml();
        ITask source = (ITask)xml.getIdRef(arc.getSourceTaskAttribute());
        ITask destination = (ITask)xml.getIdRef(arc.getDestinationAttribute());
        if(arc.getMappingCount() > 0) {
            WARNING.println("WARNING !  " + source.getNameAttribute() + "-->" + destination.getNameAttribute() + " ALREADY HAS MAPPINGS");
            WARNING.println("NO MODIFICATION MADE\n");
            return;
        }
        int scount = source.getOutputCount();
        for(int i=0;i<scount;i++) {
            IOutput output = source.getOutputAt(i);
            int dcount = destination.getInvocationAt(0).getParameterCount();
            for(int j=0;j<dcount;j++) {
                IParameter parameter = destination.getInvocationAt(0).getParameterAt(j);
                if(output.getDatatypeurlAttribute().equals(parameter.getDatatypeurlAttribute()) &&
                   output.getVariablenameAttribute().equals(parameter.getVariablenameAttribute()))
                    arc.addMappingByParameterID(output.getIdAttribute(), parameter.getIdAttribute());
            }
        }
    }

    private void autoMapNetworkTask(ITaskWrapper task) {
        INetworkTaskRealization nr = task.getRealization().getNetworkTaskRealization();
        IInputMappingListWrapper ilist = (IInputMappingListWrapper)nr.getInputMappingList();
        IOutputMappingListWrapper olist = (IOutputMappingListWrapper)nr.getOutputMappingList();
        XmlWrapper xml = task.getXml();
        if(ilist.getMappingCount() > 0) {
            WARNING.println("WARNING !  NETWORK TASK " + task.getNameAttribute() + " ALREADY HAS INPUT MAPPINGS");
            WARNING.println("NO MODIFICATION MADE TO INPUT MAPPINGS\n");
        }
        if(olist.getMappingCount() > 0) {
            WARNING.println("WARNING !  NETWORK TASK " + task.getNameAttribute() + " ALREADY HAS OUTPUT MAPPINGS");
            WARNING.println("NO MODIFICATION MADE TO OUTPUT MAPPINGS\n");
        }

        ITask source = task;
        ITask destination = (ITask)xml.getIdRef(nr.getFirsttaskAttribute());
        int scount = 0;
        if(source.getInvocationCount() != 0)
            scount = source.getInvocationAt(0).getParameterCount();
        for(int i=0;i<scount;i++) {
            IParameter sourceParam = source.getInvocationAt(0).getParameterAt(i);
            int dcount = destination.getInvocationAt(0).getParameterCount();
            for(int j=0;j<dcount;j++) {
                IParameter destinationParam = destination.getInvocationAt(0).getParameterAt(j);
                if(sourceParam.getDatatypeurlAttribute().equals(destinationParam.getDatatypeurlAttribute()) &&
                   sourceParam.getVariablenameAttribute().equals(destinationParam.getVariablenameAttribute()))
                    ilist.addMappingByParameterID(sourceParam.getIdAttribute(), destinationParam.getIdAttribute());
            }
        }

        source = (ITask)xml.getIdRef(nr.getLasttaskAttribute());
        destination = task;
        scount = source.getOutputCount();
        for(int i=0;i<scount;i++) {
            IOutput sourceParam = source.getOutputAt(i);
            int dcount = destination.getOutputCount();
            for(int j=0;j<dcount;j++) {
                IOutput destinationParam = destination.getOutputAt(j);
                if(sourceParam.getDatatypeurlAttribute().equals(destinationParam.getDatatypeurlAttribute()) &&
                   sourceParam.getVariablenameAttribute().equals(destinationParam.getVariablenameAttribute()))
                    olist.addMappingByParameterID(sourceParam.getIdAttribute(), destinationParam.getIdAttribute());
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        INetworkEditorComponentModelWrapper model = (INetworkEditorComponentModelWrapper)this.getModel();
        if(e.getActionCommand().equals(ActionEvents.TASK_ED))
            TaskEditor.launchTaskEditor(rootTask.getXml(), model.getID());
        else if(e.getActionCommand().equals(ActionEvents.NEW))
            newFile();
        else if(e.getActionCommand().equals(ActionEvents.AUTOMAP_PARAMETERS)) {
            autoMap();
        }
        else if(e.getActionCommand().equals(ActionEvents.GENERATE_ORBWORK_RUNTIME)) {
            String xmlName = ((URL)rootTask.getXml().getKey()).toString();
            xmlName = xmlName.substring(xmlName.lastIndexOf("/"), xmlName.lastIndexOf(".xml"));
            String targetDir = "c:\\workflow\\public_html\\wflows";
            targetDir = GetStringDialog.getString(null, targetDir, getLocationOnScreen());
            if(targetDir != null)
                ORBWorkRTGenerator.generate(xmlName, rootTask.getNameAttribute(), targetDir);
        }
        else if(e.getActionCommand().equals(ActionEvents.GENERATE_NRL_RUNTIME)) {
            String xmlName = ((URL)rootTask.getXml().getKey()).toString();
            xmlName = xmlName.substring(xmlName.lastIndexOf("/"), xmlName.lastIndexOf(".xml"));
            String targetDir = "c:\\workflow\\public_html\\specfiles";
            targetDir = GetStringDialog.getString(null, targetDir, getLocationOnScreen());
            if(targetDir != null)
                NRLRTGenerator.genSpecs(xmlName, rootTask.getNameAttribute(), targetDir);
        }
        else if(e.getActionCommand().equals(ActionEvents.LEVEL_MAPPING_INPUTS)) {
            INetworkEditorComponentModelWrapper parentModel = (INetworkEditorComponentModelWrapper)model.getTopLevelContainer();
            ITaskWrapper parent = (ITaskWrapper)rootTask.getXml().getIdRef(parentModel.getID());
            XmlWrapper xml = parent.getXml();
            String source = parent.getIdAttribute();
            String destination = parent.getRealization().getNetworkTaskRealization().getFirsttaskAttribute();
            if(destination != null)
                VerticalMappingEditor.launchEditor(xml, source, destination, VerticalMappingEditor.INPUT);
        } else if(e.getActionCommand().equals(ActionEvents.LEVEL_MAPPING_OUTPUTS)) {
            INetworkEditorComponentModelWrapper parentModel = (INetworkEditorComponentModelWrapper)model.getTopLevelContainer();
            ITaskWrapper parent = (ITaskWrapper)rootTask.getXml().getIdRef(parentModel.getID());
            XmlWrapper xml = parent.getXml();
            String source = parent.getRealization().getNetworkTaskRealization().getLasttaskAttribute();
            String destination = parent.getIdAttribute();
            if(source != null)
                VerticalMappingEditor.launchEditor(xml, source, destination, VerticalMappingEditor.OUTPUT);
        } else if(e.getActionCommand().equals(ActionEvents.OPEN))
            openFile();
        else if(e.getActionCommand().equals(ActionEvents.OPERATOR_ED_SUCCESS)) {
            if((model.getOutgoingArcCount() > 1) || (model.getOutgoingArcCount()>0 && ((INetworkEditorComponentModel)top().getModel()).getEndTask().equals(model.getID())))
                OperatorEditor.launchOperatorEditor(rootTask.getXml(), model.getID(), OperatorEditorType.OUTPUT_OPERATOR);
            else
                WARNING.println("NEED 2 OR MORE OUTPUT ARCS FOR OPERATOR");
        } else if(e.getActionCommand().equals(ActionEvents.OPERATOR_ED_INPUTS)) {
            if((model.getIncomingArcCount() > 1) || (model.getIncomingArcCount()>0 && ((INetworkEditorComponentModel)top().getModel()).getStartTask().equals(model.getID())))
                OperatorEditor.launchOperatorEditor(rootTask.getXml(), model.getID(), OperatorEditorType.INPUT_OPERATOR);
            else
                WARNING.println("NEED 2 OR MORE INPUT ARCS FOR OPERATOR");
        } else if(e.getActionCommand().equals(ActionEvents.DELETE)) {
            if(model.getID().equals(((INetworkEditorComponentModelWrapper)model.getTopLevelContainer()).getStartTask()))
                ((INetworkEditorComponentModelWrapper)model.getTopLevelContainer()).setStartTask(null);
            if(model.getID().equals(((INetworkEditorComponentModelWrapper)model.getTopLevelContainer()).getEndTask()))
                ((INetworkEditorComponentModelWrapper)model.getTopLevelContainer()).setEndTask(null);
            ((INetworkEditorComponentModel)getModel()).die();
        } else if(e.getActionCommand().equals(ActionEvents.VIEW_REALIZATION)) {
            if(model.NONTRANSACTIONALNETWORK.equals(model.getTaskType())) {
                ITaskWrapper rootNetTask =  (ITaskWrapper)rootTask.getXml().getIdRef(model.getID());
                INetworkEditorComponentModelWrapper model1  = launchNE(rootNetTask, domainEnv);
                NetworkEditor ne = showNE(model1, rootTask.getXml(), rootNetTask.getNameAttribute());
                Rectangle bounds = ne.getRootPane().getParent().getBounds();
                ne.getRootPane().getParent().setBounds(bounds.x+100, bounds.y+100, bounds.width, bounds.height);
            } else if(model.TRANSACTIONALREAL.equals(model.getTaskType()))
                TransactionalRealization.launchEditor(rootTask.getXml(), model.getID());
            else if(model.NONTRANSACTIONALREAL.equals(model.getTaskType()))
                CorbaRealization.launchEditor(rootTask.getXml(), model.getID());
            else if(model.SYNCHRONIZATIONTASKIN.equals(model.getTaskType()) || model.SYNCHRONIZATIONTASKOUT.equals(model.getTaskType())){
                ITask task = (ITask)rootTask.getXml().getIdRef(model.getID());
                String partner = task.getRealization().getSyncRealization().getPartnerAttribute();
                partner = GetStringDialog.getString(null, partner, getLocationOnScreen());
                if(partner != null)
                    task.getRealization().getSyncRealization().setPartnerAttribute(partner);
            } else
                WARNING.println("cannot handle realization type " + model.getTaskType());
        } else if(e.getActionCommand().equals(ActionEvents.VIEW_UP)) {
            INetworkEditorComponentModelWrapper parentModel = (INetworkEditorComponentModelWrapper)model.getTopLevelContainer();
            LOG.println("parentModel.getDisplayName() is " + parentModel.getID());
            ITaskWrapper parent = (ITaskWrapper)rootTask.getXml().getIdRef(parentModel.getID());
            parent = (ITaskWrapper)parent.getParentTask();
            if(parent != null) {
                INetworkEditorComponentModelWrapper model1  = launchNE(parent, domainEnv);
                showNE(model1, parent.getXml(), parent.getNameAttribute());
            }
        } else if(e.getActionCommand().equals(ActionEvents.ADD_ARC_SOURCE_SUCCESS)) {
            NetworkEditor top = top();
            top.entityAddingArc = this;
            top.successArc = true;
        } else if(e.getActionCommand().equals(ActionEvents.ADD_ARC_SOURCE_FAIL)) {
            NetworkEditor top = top();
            top.entityAddingArc = this;
            top.successArc = false;
        } else if(e.getActionCommand().equals(ActionEvents.SAVE))
            rootTask.getXml().saveDocument();
        else if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            LOG.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1\n\n");
            DocSaver.remove(frame);
            rootTask.getXml().saveDocument();
            frame.dispose();
            LOG.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2\n\n");
            LOG.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2\n\n");
            LOG.println("MMMMM ABOUT TO EXIT WITH -1");
            System.exit(0);
            /* ow, removed
            try{
                System.exit(0);
            } catch (Exception ee){
                ee.printStackTrace();
            } catch (Error e2){
                e2.printStackTrace();
            }
            LOG.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>3\n\n");
            */
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
            System.exit(0);
        } else if(e.getActionCommand().equals(ActionEvents.SET_AS_STARTTASK))
            ((INetworkEditorComponentModel)top().getModel()).setStartTask(model.getID());
        else if(e.getActionCommand().equals(ActionEvents.SET_AS_ENDTASK))
            ((INetworkEditorComponentModel)top().getModel()).setEndTask(model.getID());
        else if(e.getActionCommand().equals(ActionEvents.UNSET_AS_STARTTASK)) {
            String startTask = ((INetworkEditorComponentModelWrapper)top().getModel()).getStartTask();
            if(startTask!= null && startTask.equals(model.getID()))
                ((INetworkEditorComponentModelWrapper)top().getModel()).setStartTask(null);
        } else if(e.getActionCommand().equals(ActionEvents.UNSET_AS_ENDTASK)) {
            String endTask = ((INetworkEditorComponentModel)top().getModel()).getEndTask();
            if(endTask!= null && endTask.equals(model.getID()))
                ((INetworkEditorComponentModel)top().getModel()).setEndTask(null);
        } else if(e.getActionCommand().equals(ActionEvents.COMPARTMENT_ED)) {
            String oldName = model.getDisplayName();
            String newName = GetStringDialog.getString((JFrame)getRootPane().getParent(), oldName, getLocationOnScreen());
            if(newName != null && !newName.equals(oldName))
                model.setDisplayName(newName);
        } else if(e.getActionCommand().equals(ActionEvents.ADD_DOMAIN)) {
            String newName = rootTask.getXml().generateUniqueName("Domain");
            INetworkEditorComponentModelWrapper child = (INetworkEditorComponentModelWrapper)model.createChildContainer(newName, INetworkEditorComponentModel.DOMAINTYPE);
            child.setBounds(new Rectangle(popupLocation, new Dimension(100, 100)));
            child.setDomainURL(model.getDomainURL());
        } else if(e.getActionCommand().equals(ActionEvents.INSERT_COMPARTMENT)) {
            String newName = rootTask.getXml().generateUniqueName("Compartment");
            INetworkEditorComponentModelWrapper child = (INetworkEditorComponentModelWrapper)model.createChildContainer(newName, INetworkEditorComponentModel.COMPARTMENTTYPE);
            child.setBounds(new Rectangle(popupLocation, new Dimension(100, 100)));
            child.setDomainURL(model.getDomainURL());
        } else if(e.getActionCommand().equals(ActionEvents.DATA_ED)) {
            String name = domainEnv.getName();
            String[] args = {name};
            org.ofbiz.designer.newdesigner.DataEditor.DataClassView.main(args);
        } else if(e.getActionCommand().equals(ActionEvents.INSERT_HUMAN_TASK))
            insertTask("Human", INetworkEditorComponentModel.HUMANREAL);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_TRANSACTIONAL_TASK))
            insertTask("Transactional", INetworkEditorComponentModel.TRANSACTIONALREAL);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_NONTRANSACTIONAL_TASK))
            insertTask("NonTransactional", INetworkEditorComponentModel.NONTRANSACTIONALREAL);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_NONTRANSACTIONAL_WORKFLOW))
            insertTask("NonTransactionalWF", INetworkEditorComponentModel.NONTRANSACTIONALNETWORK);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_TRANSACTIONAL_WORKFLOW))
            insertTask("TransactionalWF", INetworkEditorComponentModel.TRANSACTIONALNETWORK);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_OPEN2PC_WORKFLOW))
            insertTask("Open2PCWF", INetworkEditorComponentModel.OPEN2PCNETWORK);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_COMPOSITE_WORKFLOW))
            insertTask("CompositeWF", INetworkEditorComponentModel.COMPOSITENETWORK);
        else if(e.getActionCommand().equals(ActionEvents.INSERT_COLLABORATION_TASK))
            insertTask("Collaboration", INetworkEditorComponentModel.COLLABORATIONREAL);
        else if(e.getActionCommand().equals(ActionEvents.SPLIT_DESIGN)) {
            DesignSplitter.splitDesign(rootTask.getXml());
        } else if(e.getActionCommand().equals(ActionEvents.LOCAL_DOMAIN_ED)) {
            boolean enabled = !model.isPrimaryDomain();
            Point p = popupLocation;
            p.x += getLocationOnScreen().x;
            p.y += getLocationOnScreen().y;
            DomainProperties.launchEditor(rootTask.getXml(), model.getID(), p, enabled);
        } else if(e.getActionCommand().equals(ActionEvents.DOMAIN_ED))
            DomainEnvView.main(null);
        else if(e.getActionCommand().equals(ActionEvents.ROLE_ED))
            RoleDomainView.main(null);
        else WARNING.println("UNHANDLED ACTIONEVENT " + e.getActionCommand());
    }

    private INetworkEditorComponentModel insertTask(String nameRoot, String taskType) {
        INetworkEditorComponentModelWrapper model = (INetworkEditorComponentModelWrapper)this.getModel();
        String newName = rootTask.getXml().generateUniqueName(nameRoot);
        INetworkEditorComponentModel child = (INetworkEditorComponentModel)model.createChildContainer(newName, INetworkEditorComponentModel.TASKTYPE);
        child.setLocation(popupLocation);
        child.setTaskType(taskType);
        child.setDomainURL(model.getDomainURL());
        return child;
    }

    private void openFile() {
        FileDialog dlg = new FileDialog(frame);
        dlg.setFilenameFilter(new FilenameFilter() {
                                  public boolean accept(File file, String name) {
                                      return(name.endsWith(".xml"));
                                  }
                              });
        String xmlDirectory = System.getProperty("WF_XMLDIR");
        dlg.setDirectory(xmlDirectory + "\\org.ofbiz.designer.task");
        dlg.setVisible(true);
        String dir = dlg.getDirectory();
        String file = dlg.getFile();
        if(file == null) return;
        File newFile = new File(dir, file);
        XmlWrapper tempXml = XmlWrapper.openDocument(newFile);
        rootTask = (ITaskWrapper)((INetworkDesign)tempXml.getRoot()).getTaskAt(0);
        INetworkEditorComponentModelWrapper model1  = launchNE(rootTask, domainEnv);
        showNE(model1, tempXml, rootTask.getNameAttribute());
        frame.dispose();
    }

    private void newFile() {
        Point p = this.getLocationOnScreen();
        String newTaskName = GetStringDialog.getString(null, "TaskName ?", "New File Dialog", p);
        if(newTaskName == null)
            return;
        String newFileName = newTaskName + ".xml";
        Object key = rootTask.getXml().getKey();
        /*
        if(!(key instanceof File)) {
            WARNING.println("cannot handle URL xmlsource");
            return;
        }
        */
        //File keyFile = (File)key;
        File keyFile = new File(((URL)key).getFile());
        String path = keyFile.getParent();
        File newFile = new File(path, newFileName);
        try {
            DataOutputStream ds = new java.io.DataOutputStream(new BufferedOutputStream(new FileOutputStream(newFile)));
            CodeGenerator cg = new CodeGenerator(ds, CodeGenerator.XML);

            String xmlDir = System.getProperty("WF_XMLDIR");
            String dtdDir = System.getProperty("WF_DTDDIR");

            cg.println("<?xml version=\"1.0\"?>");
            cg.println("<!DOCTYPE NetworkDesign SYSTEM \"file:///" + dtdDir + "/org.ofbiz.designer.networkdesign.dtd\">");
	    //cg.println("<!DOCTYPE NetworkDesign SYSTEM \"file:///../../dtd/org.ofbiz.designer.networkdesign.dtd\">");
            cg.begin("NetworkDesign");{
                cg.begin("Task x=\"10\" name=\"" + newTaskName + "\" securitydomainurl=\"/DefaultDomain.xml#Secret\" y=\"10\" id=\"" + newTaskName + "\"");{
                    cg.begin("Realization");{
                        cg.begin("NetworkTaskRealization realizationtype=\"NonTransactionalNetwork\"");{
                            cg.println("<Domain url=\"/DefaultDomain.xml#Secret\" y=\"18\" id=\"Domain0.987324732984732\" height=\"203\" width=\"654\" x=\"21\"/>");
                            cg.println("<InputMappingList />");
                            cg.println("<OutputMappingList />");
                        }cg.end();
                    }cg.end();
                }cg.end();
            }cg.end();
            cg.close();
        } catch(IOException ee) {
            ee.printStackTrace();
        }
        XmlWrapper tempXml = XmlWrapper.openDocument(newFile);
        rootTask = (ITaskWrapper)tempXml.getIdRef(newTaskName);
        INetworkEditorComponentModelWrapper model1  = launchNE(rootTask, domainEnv);
        showNE(model1, tempXml, rootTask.getNameAttribute());
        frame.dispose();
    }

    Object entityAddingArc = null; // Node or Arc
    boolean successArc = true;
    private NetworkEditor startTask = null;
    private NetworkEditor endTask = null;
    private Point arcHead = null;

    public void synchronize() {
        super.synchronize();
        INetworkEditorComponentModel model = (INetworkEditorComponentModel)this.getModel();
        try {
            String startTaskID = model.getStartTask();
            if(startTaskID == null)
                startTask = null;
            else
                startTask = (NetworkEditor)model.getChildContainerByIDRecursive(startTaskID).getGui();
        } catch(NullPointerException e) {
        }
        try {
            String endTaskID = model.getEndTask();
            if(endTaskID == null)
                endTask = null;
            else
                endTask = (NetworkEditor)model.getChildContainerByIDRecursive(endTaskID).getGui();
        } catch(NullPointerException e) {
        }

        if(model.getColor() != null) setForeground(model.getColor());
        myIcon = model.getIcon();
        if(model.getDisplayName() != null)
            setName(model.getDisplayName());
        else
            setName(model.getID());
    }
    private ImageIcon myIcon = null;

    protected void setDragAdapter() {
        new NetworkEditorDragAdapter(this);
    }

    private NetworkEditor top() {
        NetworkEditor top = NetworkEditor.this;
        while(top.getParent() instanceof NetworkEditor)
            top = (NetworkEditor)top.getParent();
        return top;
    }
}

class NetworkEditorDragAdapter extends ContainerViewDragAdapter {
    NetworkEditorDragAdapter(ContainerView _containerView) {
        super(_containerView);
    }

    public void handleDrop(String dragType, IContainerModel draggedModel, Point dropLocation, Point dragLocation) {
        INetworkEditorComponentModel myModel = (INetworkEditorComponentModel)containerView.getModel();
        String myModelType = myModel.getModelType();
        ContainerView draggedComponent = (ContainerView)draggedModel.getGui();

        if(dragType.equals(RESIZE) && ((INetworkEditorComponentModel)draggedModel).getModelType().equals(INetworkEditorComponentModel.TASKTYPE)) {
            WARNING.println("Task cannot be resized");
            containerView.resizeBox = null;
            containerView.repaint();
            return;
        } else if(myModelType.equals(INetworkEditorComponentModel.WORKFLOWTYPE) && !((INetworkEditorComponentModel)draggedModel).getModelType().equals(INetworkEditorComponentModel.DOMAINTYPE)) {
            WARNING.println("Task cannot be dropped into Workflow");
            return;
        } else if(!myModelType.equals(INetworkEditorComponentModel.WORKFLOWTYPE) && 
                  !myModelType.equals(INetworkEditorComponentModel.DOMAINTYPE) && 
                  ((INetworkEditorComponentModel)draggedModel).getModelType().equals(INetworkEditorComponentModel.DOMAINTYPE)) {
            WARNING.println("Domain can only be dropped into Workflow");
            return;
        } else
            super.handleDrop(dragType, draggedModel, dropLocation, dragLocation);
    }
}

class NEFrame extends WFFrame {
    public NEFrame(String title) {
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(100, 100, 694, 532);
    }
}




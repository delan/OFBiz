package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.XmlWrapper;
import org.ofbiz.designer.pattern.BaseTranslator;
import org.ofbiz.designer.networkdesign.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.domainenv.IDomainEnvWrapper;
import org.ofbiz.designer.domainenv.IDomainInfo;
import org.ofbiz.designer.newdesigner.RoledomainTranslator;
import org.ofbiz.designer.newdesigner.popup.ActionEvents;
import org.ofbiz.designer.newdesigner.model.OperatorEditorType;
import org.ofbiz.designer.newdesigner.model.INetworkEditorComponentModelWrapper;
import org.ofbiz.designer.newdesigner.operatoreditor.OperatorEditor;
import org.ofbiz.designer.newdesigner.DataEditor.DataClassView;

import javax.swing.border.BevelBorder;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TaskEditor extends TaskEditorView {
    private XmlWrapper taskXml;
    private ITaskWrapper taskWrapper;
    private Hashtable allRoledomainXmls = new Hashtable();
    private Hashtable allSecurityDomains = new Hashtable();
    private WFFrame frame = null;

    private void initializeFieldNames() {
        nameField.setName("IdAttribute");
        descriptionField.setName("description");
    }

    public static void launchTaskEditor(XmlWrapper taskXml, String taskName) {
        TaskEditor taskEditor = new TaskEditor(taskXml, taskName);
        TEFrame frame = new TEFrame(taskEditor.taskWrapper.getNameAttribute());
        DocSaver.add(taskXml, frame);

        frame.getContentPane().add(taskEditor);
        taskEditor.frame = frame;
        WFMenuBar menuBar = new WFMenuBar();
        menuBar.addActionListener(taskEditor);
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.err.println("enter <filename> <taskname> as parameter");
            return;
        }

        if(!args[0].endsWith(".xml")) args[0] += ".xml";
        String fileName = args[0];
        String taskName = args[1];

        // david
        URL url = fileName.getClass().getResource("/" + fileName);
        if (url == null) {
         System.out.println("Cannot finde file" + fileName);
         return;
        }                   
        
        XmlWrapper taskXml = XmlWrapper.openDocument(new File(url.getFile()));
        ConsoleSpacer.init();
        launchTaskEditor(taskXml, taskName);
    }

    private void getAllRoledomains(ITaskWrapper taskWrapper) {
        int count = taskWrapper.getRolesCount();
        for(int i=0; i<count; i++) {
            IRoles roles = taskWrapper.getRolesAt(i);
            String href = roles.getRoledomainAttribute();
            XmlWrapper roledomainXml = XmlWrapper.openDocument(new File(href));
            allRoledomainXmls.put(href, roledomainXml);
        }
    }

    private void getAllSecurityDomains(ITaskWrapper taskWrapper) {
        String href = taskWrapper.getSecuritydomainurlAttribute();
        if(href == null) {
            WARNING.println("security domain is null");
            return;
        }
        int poundIndex = href.indexOf("#"); 
        href = href.substring(0, poundIndex);
        XmlWrapper securitydomainXml = null;
        href = XmlWrapper.XMLDIR + "\\domainenv\\" + href;
        href = XmlWrapper.fixURL(href);

        try {
            securitydomainXml = XmlWrapper.openDocument(new URL(href));
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        domainEnv = (IDomainEnvWrapper)securitydomainXml.getRoot();
        int count = domainEnv.getDomainInfoCount();
        for(int i=0; i<count; i++) {
            IDomainInfo domainInfo = domainEnv.getDomainInfoAt(i);
            allSecurityDomains.put(domainInfo.getName(), domainInfo);
        }
    }

    private IDomainEnvWrapper domainEnv=null;
    Bag bag = new Bag();        
    //ITaskWrapper taskWrapper = null;

    public TaskEditor(XmlWrapper taskXmlIn, String taskName) {
        initializeFieldNames();
        //nameField.setEnabled(false);
        taskXml = taskXmlIn;
        taskWrapper = (ITaskWrapper)taskXml.getIdRef(taskName);

        getAllRoledomains(taskWrapper);
        getAllSecurityDomains(taskWrapper);

        bag.nameFieldModel =  PlainDocumentModel.createModelProxy();
        bag.descriptionFieldModel =  PlainDocumentModel.createModelProxy();
        bag.hostFieldModel =  PlainDocumentModel.createModelProxy();
        bag.timeoutFieldModel =  PlainDocumentModel.createModelProxy();

        bag.foreignFieldModel =  CheckBoxModelImpl.createModelProxy();

        if(taskWrapper.getRolesCount() > 0) {
            bag.currentRolesFieldModel =  ListModelImpl.createModelProxy();
            bag.availableRolesFieldModel =  ListModelImpl.createModelProxy();
        }

        bag.inputArcsFieldModel =  ListModelImpl.createModelProxy();
        bag.outputArcsFieldModel =  ListModelImpl.createModelProxy();
        bag.exceptionArcsFieldModel =  ListModelImpl.createModelProxy();
        bag.compartmentsFieldModel =  PlainDocumentModel.createModelProxy();

        inputArcsField.setModel(bag.inputArcsFieldModel);
        outputArcsField.setModel(bag.outputArcsFieldModel);
        exceptionArcsField.setModel(bag.exceptionArcsFieldModel);
        compartmentField.setDocument(bag.compartmentsFieldModel);

        new ListTranslator((IListWrapper)bag.inputArcsFieldModel, taskWrapper, "getInputArcs", "removeInArcByName", "addInArc", BaseTranslator.UPDATE_MODEL);
        new ListTranslator((IListWrapper)bag.outputArcsFieldModel, taskWrapper, "getOutputArcs", "removeOutArcByName", "addOutArcByName", BaseTranslator.UPDATE_MODEL);     
        new ListTranslator((IListWrapper)bag.exceptionArcsFieldModel, taskWrapper, "getExceptionArcs", "removeLink", "addLink", BaseTranslator.UPDATE_MODEL);       

        bag.invocationsFieldModel =  ListModelImpl.createModelProxy();
        bag.outputsFieldModel =  ListModelImpl.createModelProxy();
        bag.exceptionsFieldModel =  ListModelImpl.createModelProxy();

        bag.constraintsFieldModel =  ListModelImpl.createModelProxy();

        bag.taskTypeFieldModel =  ComboBoxModelImpl.createModelProxy();
        bag.securityDomainFieldModel =  ComboBoxModelImpl.createModelProxy();
        if(taskWrapper.getParentTask() != null)
            securityDomainField.setEnabled(false);

        bag.roleDomainFieldModel =  ComboBoxModelImpl.createModelProxy();

        nameField.setDocument(bag.nameFieldModel);
        descriptionField.setDocument(bag.descriptionFieldModel);
        hostField.setDocument(bag.hostFieldModel);
        timeoutField.setDocument(bag.timeoutFieldModel);
        foreignTaskField.setModel(bag.foreignFieldModel);

        if(bag.currentRolesFieldModel != null)
            currentRolesField.setModel(bag.currentRolesFieldModel);
        if(bag.availableRolesFieldModel != null)
            availableRolesField.setModel(bag.availableRolesFieldModel);

        invocationsField.setModel(bag.invocationsFieldModel);
        outputsField.setModel(bag.outputsFieldModel);
        exceptionsField.setModel(bag.exceptionsFieldModel);
        constraintsField.setModel(bag.constraintsFieldModel);

        typeField.setModel(bag.taskTypeFieldModel);
        securityDomainField.setModel(bag.securityDomainFieldModel);

        roleDomainField.setModel(bag.roleDomainFieldModel);

        dataPermissions.setBorder(new BevelBorder(BevelBorder.LOWERED));
        dataPermissions.setLayout(new BorderLayout());
        dataPermissions.add(new DataPermissions(taskWrapper));

        //new DocumentTranslator((IDocumentWrapper)bag.nameFieldModel, taskWrapper, "IdAttribute", BaseTranslator.UPDATE_MODEL);
        new DocumentTranslator((IDocumentWrapper)bag.nameFieldModel, taskWrapper, "NameAttribute", BaseTranslator.UPDATE_MODEL);
        new DocumentTranslator((IDocumentWrapper)bag.descriptionFieldModel, taskWrapper, "Description", BaseTranslator.UPDATE_MODEL);
        new DocumentTranslator((IDocumentWrapper)bag.hostFieldModel, taskWrapper, "HostAttribute", BaseTranslator.UPDATE_MODEL);
        new DocumentTranslator((IDocumentWrapper)bag.timeoutFieldModel, taskWrapper, "TimeoutAttribute", BaseTranslator.UPDATE_MODEL);

        new CheckBoxTranslator((ICheckBoxWrapper)bag.foreignFieldModel, taskWrapper, "isForeign", "setForeign", BaseTranslator.UPDATE_MODEL);

        if(taskWrapper.getRolesCount() > 0) {
            new ListTranslator((IListWrapper)bag.currentRolesFieldModel, ((IRolesWrapper)taskWrapper.getRolesAt(0)), "getRoleNames", "removeRole", "addRole", BaseTranslator.UPDATE_MODEL);
            new AvailableRolesTranslator((IListWrapper)bag.availableRolesFieldModel, (IRolesWrapper)taskWrapper.getRolesAt(0), allRoledomainXmls, BaseTranslator.UPDATE_MODEL);
        }

        new ListTranslator((IListWrapper)bag.invocationsFieldModel, taskWrapper, "getInvocationParameters", "removeInvocationParameter", "addInvocationParameter", BaseTranslator.UPDATE_MODEL);        
        new ListTranslator((IListWrapper)bag.outputsFieldModel, taskWrapper, "getOutputNames", "removeOutputParameter", "addOutputParameter", BaseTranslator.UPDATE_MODEL);     
        new ListTranslator((IListWrapper)bag.exceptionsFieldModel, taskWrapper, "getExceptionNames", "removeTaskException", "addTaskException", BaseTranslator.UPDATE_MODEL);       

        new ListTranslator((IListWrapper)bag.constraintsFieldModel, taskWrapper, "getConstraintNames", "removeConstraint", "addConstraint", BaseTranslator.UPDATE_MODEL);       
        new ComboBoxTranslator((IComboBoxWrapper)bag.taskTypeFieldModel, taskWrapper, "getTaskTypes", "", "TaskType", BaseTranslator.UPDATE_MODEL);     
        //new SecurityDomainTranslator((IComboBoxWrapper)bag.securityDomainFieldModel, taskWrapper, allSecurityDomains, BaseTranslator.UPDATE_MODEL);

        new RoledomainTranslator((IComboBoxWrapper)bag.roleDomainFieldModel, taskWrapper, BaseTranslator.UPDATE_MODEL);

        roleDomainField.addActionListener(new ActionListener() {
                                              public void actionPerformed(ActionEvent e) {
                                                  int index = roleDomainField.getSelectedIndex();

                                                  bag.currentRolesFieldModel =  ListModelImpl.createModelProxy();
                                                  bag.availableRolesFieldModel =  ListModelImpl.createModelProxy();

                                                  currentRolesField.setModel(bag.currentRolesFieldModel);
                                                  availableRolesField.setModel(bag.availableRolesFieldModel);

                                                  new ListTranslator((IListWrapper)bag.currentRolesFieldModel, taskWrapper, "getRoleNames", "removeRole", "addRole", BaseTranslator.UPDATE_MODEL);
                                                  //  UNKNOWNnew AvailableRolesTranslator((IListWrapper)bag.availableRolesFieldModel, (IRolesWrapper)taskWrapper.getRolesAt(0), allRoledomainXmls, BaseTranslator.UPDATE_MODEL);
                                              }
                                          });
        relayout();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals(ActionEvents.EDIT_REALIZATION) || command.equals(ActionEvents.VIEW_REALIZATION)) {
            if(taskWrapper.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALNETWORK)) {
                INetworkEditorComponentModelWrapper model1  = NetworkEditor.launchNE(taskWrapper, domainEnv);
                NetworkEditor.showNE(model1, taskWrapper.getXml(), taskWrapper.getNameAttribute());
            }if(taskWrapper.getTaskType().equals(TaskSupportClass.TRANSACTIONALTASKREALIZATION))
                TransactionalRealization.launchEditor(taskWrapper.getXml(), taskWrapper.getIdAttribute());
            if(taskWrapper.getTaskType().equals(TaskSupportClass.NONTRANSACTIONALTASKREALIZATION))
                CorbaRealization.launchEditor(taskWrapper.getXml(), taskWrapper.getIdAttribute());
            else if(TaskSupportClass.SYNCHRONIZATIONTASKIN.equals(taskWrapper.getTaskType()) || TaskSupportClass.SYNCHRONIZATIONTASKOUT.equals(taskWrapper.getTaskType())){
                String partner = taskWrapper.getRealization().getSyncRealization().getPartnerAttribute();
                partner = GetStringDialog.getString(null, partner, getLocationOnScreen());
                if(partner != null) 
                    taskWrapper.getRealization().getSyncRealization().setPartnerAttribute(partner);
            }
            else
                WARNING.println("cannot handle realization type " + taskWrapper.getTaskType());
        } else if(command.equals(ActionEvents.CREATE_NEW_REALIZATION))
            WARNING.println(command + " NOT IMPLEMENTED");
        else if(command.equals(ActionEvents.BROWSE_REALIZATION))
            WARNING.println(command + " NOT IMPLEMENTED");
        else if(command.equals(ActionEvents.IMPORT_URL))
            WARNING.println(command + " NOT IMPLEMENTED");
        else if(command.equals(ActionEvents.ADD_ROLE)) {
            int index = availableRolesField.getSelectedIndex();
            String selected = (String)availableRolesField.getSelectedValue();
            ((IListModel)currentRolesField.getModel()).addElement(selected);
        } else if(command.equals(ActionEvents.REMOVE_ROLE)) {
            int index = currentRolesField.getSelectedIndex();
            ((IListModel)currentRolesField.getModel()).remove(index);
        } else if(command.equals(ActionEvents.ADD_ALL_ROLES)) {
            int size = ((IListModel)availableRolesField.getModel()).getSize();
            Vector roles = new Vector();
            for(int i=0; i<size; i++)
                roles.addElement(((IListModel)availableRolesField.getModel()).elementAt(i));
            for(int i=0; i<size; i++)
                ((IListModel)currentRolesField.getModel()).addElement(roles.elementAt(i));
        } else if(command.equals(ActionEvents.REMOVE_ALL_ROLES)) {
            int size = bag.currentRolesFieldModel.getSize();
            for(int i=size-1; i>=0; i--)
                bag.currentRolesFieldModel.remove(i);
        } else if(command.equals(ActionEvents.EDIT_ROLE)) {
            WARNING.println(command + " NOT IMPLEMENTED");
        } else if(command.equals(ActionEvents.INPUT_OPERATOR_EDITOR)) {
            String[] inArcsArr = IDRefHelper.getReferenceArray(taskWrapper.getInarcsAttribute());
            if((inArcsArr.length > 1) || (inArcsArr.length>0 && (taskWrapper.getIdAttribute().equals(taskWrapper.getParentTask().getRealization().getNetworkTaskRealization().getFirsttaskAttribute()))))
                OperatorEditor.launchOperatorEditor(taskWrapper.getXml(), taskWrapper.getIdAttribute(), OperatorEditorType.INPUT_OPERATOR);
            else
                WARNING.println("NEED 2 OR MORE INPUT ARCS FOR OPERATOR");
        } else if(command.equals(ActionEvents.INPUT_TASK_EDITOR)) {
            String taskName = (String)inputArcsField.getSelectedValue();
            if(taskName == null) return;
            INetworkDesign context = (INetworkDesign)taskXml.getRoot();
            int count = context.getTaskCount();
            for(int i=0; i<count; i++) {
                ITask task = (ITask)context.getTaskAt(i);
                LOG.println("org.ofbiz.designer.task is " + task);
                if(taskName.equals(task.getNameAttribute())) {
                    String taskID = task.getIdAttribute();
                    launchTaskEditor( taskXml, taskID);
                }

            }
        } else if(command.equals(ActionEvents.INPUT_ARC_EDITOR)) {
            String sourceTaskName = (String)inputArcsField.getSelectedValue();
            if(sourceTaskName == null) return;
            INetworkDesign context = (INetworkDesign)taskXml.getRoot();
            int count = context.getTaskCount();
            for(int i=0; i<count; i++) {
                ITask task = (ITask)context.getTaskAt(i);
                if(sourceTaskName.equals(task.getNameAttribute())) {
                    int arcCount = context.getArcCount();
                    for(int j=0; j<arcCount; j++) {
                        IArc arc = context.getArcAt(j);
                        String destination = arc.getDestinationAttribute();
                        String source = arc.getSourceAttribute();
                        if(taskXml.getIdRef(source) instanceof IArc) 
                            source = ((IArc)taskXml.getIdRef(source)).getSourceAttribute();
                        if(destination.equals(taskWrapper.getIdAttribute()) && source.equals(task.getIdAttribute())) {
                            ArcEditor.launchArcEditor(taskXml, arc);
                            return;
                        }
                    }
                }
            }
        } else if(command.equals(ActionEvents.INPUT_DELETE_ARC)) {
            int index = inputArcsField.getSelectedIndex();
            if(index == -1) return;
            ((IListModel)inputArcsField.getModel()).remove(index);
        } else if(command.equals(ActionEvents.OUTPUT_OPERATOR_EDITOR)) {
            String[] outArcsArr = IDRefHelper.getReferenceArray(taskWrapper.getOutarcsAttribute());
            if((outArcsArr.length > 1) || (outArcsArr.length>0 && (taskWrapper.getIdAttribute().equals(taskWrapper.getParentTask().getRealization().getNetworkTaskRealization().getLasttaskAttribute()))))
                OperatorEditor.launchOperatorEditor(taskWrapper.getXml(), taskWrapper.getIdAttribute(), OperatorEditorType.OUTPUT_OPERATOR);
            else
                WARNING.println("NEED 2 OR MORE INPUT ARCS FOR OPERATOR");
        } else if(command.equals(ActionEvents.OUTPUT_TASK_EDITOR)) {
            String taskName = (String)outputArcsField.getSelectedValue();
            if(taskName == null) return;
            INetworkDesign context = (INetworkDesign)taskXml.getRoot();
            int count = context.getTaskCount();
            for(int i=0; i<count; i++) {
                ITask task = (ITask)context.getTaskAt(i);
                if(taskName.equals(task.getNameAttribute())) {
                    String taskID = task.getIdAttribute();
                    launchTaskEditor( taskXml, taskID);
                }
            }
        } else if(command.equals(ActionEvents.OUTPUT_ARC_EDITOR)) {
            String destinationTaskName = (String)outputArcsField.getSelectedValue();
            if(destinationTaskName == null) return;
            INetworkDesign context = (INetworkDesign)taskXml.getRoot();
            int count = context.getTaskCount();
            for(int i=0; i<count; i++) {
                ITask task = (ITask)context.getTaskAt(i);
                if(destinationTaskName.equals(task.getNameAttribute())) {
                    int arcCount = context.getArcCount();
                    for(int j=0; j<arcCount; j++) {
                        IArc arc = context.getArcAt(j);
                        if(arc.getSourceAttribute().equals(taskWrapper.getIdAttribute()) && arc.getDestinationAttribute().equals(task.getIdAttribute())) {
                            ArcEditor.launchArcEditor(taskXml, arc);
                            return;
                        }
                    }
                }
            }
        } else if(command.equals(ActionEvents.EXCEPTION_TASK_EDITOR)) {
            String taskName = (String)exceptionArcsField.getSelectedValue();
            if(taskName == null) return;
            launchTaskEditor(taskXml, taskName);
        } else if(command.equals(ActionEvents.EXCEPTION_ARC_EDITOR)) {
            String destinationTaskName = (String)exceptionArcsField.getSelectedValue();
            if(destinationTaskName == null) return;
            INetworkDesign context = (INetworkDesign)taskXml.getRoot();
            int count = context.getTaskCount();
            for(int i=0; i<count; i++) {
                ITask task = (ITask)context.getTaskAt(i);
                if(destinationTaskName.equals(task.getNameAttribute())) {
                    int arcCount = context.getArcCount();
                    for(int j=0; j<arcCount; j++) {
                        IArc arc = context.getArcAt(j);
                        if(arc.getSourceAttribute().equals(taskWrapper.getIdAttribute()) && arc.getDestinationAttribute().equals(task.getIdAttribute())) {
                            ArcEditor.launchArcEditor(taskXml, arc);
                            return;
                        }
                    }
                }
            }
        } else if(command.equals(ActionEvents.NEW_INVOCATION)) {
            String msg = "To create a new invocation, drag and drop from DataEditor";
            WARNING.println(msg);
        } else if(command.equals(ActionEvents.REMOVE_INVOCATION)) {
            int index = invocationsField.getSelectedIndex();
            if(index != -1) ((IListModel)invocationsField.getModel()).remove(index);
        } else if(command.equals(ActionEvents.REMOVE_ALL_INVOCATION)) {
            int size = ((IListModel)invocationsField.getModel()).getSize();
            for(int i=size-1; i>=0; i--)
                ((IListModel)invocationsField.getModel()).remove(i);
        } else if(command.equals(ActionEvents.EDIT_INVOCATION)) {
            int index = invocationsField.getSelectedIndex();
            String invocation = (String)invocationsField.getSelectedValue();
            StringTokenizer stk = new StringTokenizer(invocation);
            String type = stk.nextToken();
            String name = stk.nextToken();
            name = GetStringDialog.getString(null, name, "Edit variable name", getLocationOnScreen());
            if(name != null) {
                invocation = type + " " + name;
                bag.invocationsFieldModel.remove(index);
                bag.invocationsFieldModel.insertElementAt(invocation, index);
            }
        } else if(command.equals(ActionEvents.INVOCATION_PERMISSIONS)) {
            WARNING.println(command + " NOT IMPLEMENTED");
        } else if(command.equals(ActionEvents.NEW_OUTPUT)) {
            String msg = "To create a new output, drag and drop from DataEditor";
            WARNING.println(msg);
        } else if(command.equals(ActionEvents.REMOVE_OUTPUT)) {
            int index = outputsField.getSelectedIndex();
            if(index != -1) ((IListModel)outputsField.getModel()).remove(index);
        } else if(command.equals(ActionEvents.REMOVE_ALL_OUTPUTS)) {
            int size = ((IListModel)outputsField.getModel()).getSize();
            for(int i=size-1; i>=0; i--)
                ((IListModel)outputsField.getModel()).remove(i);
        } else if(command.equals(ActionEvents.EDIT_OUTPUT)) {
            int index = outputsField.getSelectedIndex();
            String output = (String)outputsField.getSelectedValue();
            StringTokenizer stk = new StringTokenizer(output);
            String type = stk.nextToken();
            String name = stk.nextToken();
            name = GetStringDialog.getString(null, name, "Edit variable name", getLocationOnScreen());
            if(name != null) {
                output = type + " " + name;
                bag.outputsFieldModel.remove(index);
                bag.outputsFieldModel.insertElementAt(output, index);
            }
        } else if(command.equals(ActionEvents.OUTPUT_PERMISSIONS)) {
            WARNING.println(command + " NOT IMPLEMENTED");
        } else if(command.equals(ActionEvents.NEW_EXCEPTION)) {
            String msg = "To create a new exception, drag and drop from DataEditor";
            WARNING.println(msg);
        } else if(command.equals(ActionEvents.REMOVE_EXCEPTION)) {
            int index = exceptionsField.getSelectedIndex();
            if(index != -1) ((IListModel)exceptionsField.getModel()).remove(index);
        } else if(command.equals(ActionEvents.REMOVE_ALL_EXCEPTIONS)) {
            int size = ((IListModel)exceptionsField.getModel()).getSize();
            for(int i=size-1; i>=0; i--)
                ((IListModel)exceptionsField.getModel()).remove(i);
        } else if(command.equals(ActionEvents.EDIT_EXCEPTION)) {
            WARNING.println(command + " NOT IMPLEMENTED");
        } else if(command.equals(ActionEvents.EXCEPTION_HANDLER)) {
            String selectedException = (String)exceptionsField.getSelectedValue();
            if(selectedException == null) return;
            int count = taskWrapper.getTaskExceptionCount();
            for(int i=0;i<count;i++) {
                ITaskException te = taskWrapper.getTaskExceptionAt(i);
                if(te.getDatatypeurlAttribute().equals(selectedException)) 
                    org.ofbiz.designer.newdesigner.ExceptionHandler.launchEditor(taskXml, te.getIdAttribute(), new Point(10, 10), true);
            }
            //launchEditor(taskXml, exceptionID, new Point(10, 10), true);
        } else if(command.equals(ActionEvents.NEW_CONSTRAINT)) {
            String newConstraint = GetStringDialog.getString(null, "Input new Constraint", getLocationOnScreen());
            bag.constraintsFieldModel.addElement(newConstraint);
        } else if(command.equals(ActionEvents.REMOVE_CONSTRAINT)) {
            int selectedConstraints[] = constraintsField.getSelectedIndices();
            for(int i=selectedConstraints.length-1; i>=0;  i--)
                bag.constraintsFieldModel.remove(selectedConstraints[i]);
        } else if(command.equals(ActionEvents.REMOVE_ALL_CONSTRAINTS)) {
            int size = ((IListModel)constraintsField.getModel()).getSize();
            for(int i=size-1; i>=0; i--)
                ((IListModel)constraintsField.getModel()).remove(i);
        } else if(command.equals(ActionEvents.EDIT_CONSTRAINT)) {
            String selectedConstraint = (String)constraintsField.getSelectedValue();
            int selectedIndex = constraintsField.getSelectedIndex();
            if(selectedConstraint == null)
                return;
            String newConstraint = GetStringDialog.getString(null, selectedConstraint, getLocationOnScreen());
            bag.constraintsFieldModel.remove(selectedIndex);
            bag.constraintsFieldModel.insertElementAt(newConstraint, selectedIndex);
        } else if(e.getActionCommand().equals(ActionEvents.SAVE_EXIT)) {
            DocSaver.remove(frame);
            taskXml.saveDocument();
            frame.dispose();
            System.exit(0);
        } else if(e.getActionCommand().equals(ActionEvents.DISCARD_QUIT)) {
            DocSaver.remove(frame);
            frame.dispose();
            System.exit(0);
        } else if(e.getActionCommand().equals(ActionEvents.DATA_ED)) {
            String name = domainEnv.getName();
            String[] args = {name};
            DataClassView.main(null);
        } else
            WARNING.println("UNHANDLED ACTIONCOMMAND " + command);
    }
}

class TEFrame extends WFFrame{
    public TEFrame(String title){
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(110, 110, 537, 725);
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;

import java.awt.Image;
import java.io.CharConversionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.python.django.project.ui.TreeRootNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.actions.FindAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.xml.XMLUtil;


/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoLogicalView implements LogicalViewProvider {

    private DjangoProject project;

    public DjangoLogicalView(DjangoProject project) {
        this.project = project;
    }

    public Node createLogicalView() {
        return new DjangoProjectNode();
    }

    public Node findPath(Node root, Object target) {
        Project project = root.getLookup().lookup(Project.class);
        if (project == null) {
            return null;
        }        
        if (target instanceof FileObject) {
            FileObject targetFO = (FileObject) target;
            Project owner = FileOwnerQuery.getOwner(targetFO);
            if (!project.equals(owner)) {
                return null; // Don't waste time if project does not own the fo
            }
            
            Node[] rootChildren = root.getChildren().getNodes(true);
            for (int i = 0; i < rootChildren.length; i++) {
                TreeRootNode.PathFinder pf2 = rootChildren[i].getLookup().lookup(TreeRootNode.PathFinder.class);
                if (pf2 != null) {
                    Node n =  pf2.findPath(rootChildren[i], target);
                    if (n != null) {
                        return n;
                    }
                }
                FileObject childFO = (FileObject) rootChildren[i].getLookup().lookup(DataObject.class).getPrimaryFile();
                if (targetFO.equals(childFO)) {
                    return rootChildren[i];
                }
            }
        }
        
        return null;
    }
    private static Image brokenProjectBadge = ImageUtilities.loadImage("org/netbeans/modules/python/django/project/resources/brokenProjectBadge.gif", true);
                             
    private final class DjangoProjectNode extends AbstractNode {
        
        private boolean broken; //for future use, marks the project as 
        
        public DjangoProjectNode() {
           
            super(NodeFactorySupport.createCompositeChildren(project, "Projects/org-netbeans-modules-python-django-project/Nodes"),
                  Lookups.singleton(project));
            
            setIconBaseWithExtension("org/netbeans/modules/python/django/project/resource/django-icon-naive-16.png");
            super.setName( ProjectUtils.getInformation( project ).getDisplayName() );            
        }
        
        public @Override String getShortDescription() {
            //todo: Add python platform description
            String dirName = FileUtil.getFileDisplayName(project.getProjectDirectory());
            return NbBundle.getMessage(DjangoLogicalView.class, "DjangoLogicalView.ProjectTooltipDescription", dirName);
        }
        
        public @Override String getHtmlDisplayName() {
            String dispName = super.getDisplayName();
            
                try {
                    dispName = XMLUtil.toElementContent(dispName);
                } catch (CharConversionException ex) {
                    Exceptions.printStackTrace(ex);
                }
           
            // XXX text colors should be taken from UIManager, not hard-coded!
            return broken ? "<font color=\"#A40000\">" + dispName + "</font>" : null; //NOI18N
        }

       @Override
       public Image getIcon(int type) {
           Image original = super.getIcon(type);
           return broken ? ImageUtilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;
          // return Utilities.loadImage (
           //     "org/netbeans/modules/python/django/project/resource/django-icon-naive-16.png");
       }
        
       @Override
       public Image getOpenedIcon(int type) {
           Image original = super.getOpenedIcon(type);
           return original;
           //return broken ? ImageUtilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;
       }
       
       @Override
       public Action[] getActions( boolean context ) {
           System.out.println("Get Actions");
            return getAdditionalActions();
        }
        
       @Override
       public boolean canRename() {
           return true;
       }
        
       @Override
       public void setName(String s) {
           DefaultProjectOperations.performDefaultRenameOperation(project, s);
       }
        
       
       @Override
       public HelpCtx getHelpCtx() {
           return new HelpCtx(DjangoProjectNode.class);
       }
       
       private Action[] getAdditionalActions () {
           final List<Action> actions = new ArrayList<Action>();            
           actions.add(CommonProjectActions.newFileAction());
           actions.add(null);

//            The action provider is not done yet
//            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_BUILD, bundle.getString("LBL_BuildAction_Name"), null)); // NOI18N
//            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_REBUILD, bundle.getString("LBL_RebuildAction_Name"), null)); // NOI18N
//            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_CLEAN, bundle.getString("LBL_CleanAction_Name"), null)); // NOI18N
//            actions.add(ProjectSensitiveActions.projectCommandAction(JavaProjectConstants.COMMAND_JAVADOC, bundle.getString("LBL_JavadocAction_Name"), null)); // NOI18N
//            actions.add(null);
        
            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_RUN, NbBundle.getMessage(DjangoLogicalView.class,"LBL_RunAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.COMMAND_SYNCDB,NbBundle.getMessage(DjangoLogicalView.class,"LBL_RunSyncdb_Name"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.COMMAND_SHELL,NbBundle.getMessage(DjangoLogicalView.class,"LBL_RunShell_Name"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.COMMAND_VALIDATE,NbBundle.getMessage(DjangoLogicalView.class,"LBL_RunValidate_Name"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.DJANGO_SQL_COMMAND,NbBundle.getMessage(DjangoLogicalView.class,"LBL_SqlCommand_Name"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.ADMIN_TEMPLATE,NbBundle.getMessage(DjangoLogicalView.class,"LBL_AdminTemplate"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.DJANGO_COMMAND,NbBundle.getMessage(DjangoLogicalView.class,"LBL_Command_Name"),null));
            actions.add(ProjectSensitiveActions.projectCommandAction(DjangoActionProvider.DJANGO_TEST_COMMAND,NbBundle.getMessage(DjangoLogicalView.class,"LBL_TestCommand_Name"),null));


            //            actions.addAll(Utilities.actionsForPath("Projects/Debugger_Actions_temporary")); //NOI18N
//            actions.addAll(Utilities.actionsForPath("Projects/Profiler_Actions_temporary")); //NOI18N
//            actions.add(ProjectSensitiveActions.projectCommandAction(ActionProvider.COMMAND_TEST, bundle.getString("LBL_TestAction_Name"), null)); // NOI18N
           //actions.add(null);
            
            actions.add(CommonProjectActions.setAsMainProjectAction());
            actions.add(CommonProjectActions.openSubprojectsAction());
            actions.add(CommonProjectActions.closeProjectAction());
            
            actions.add(CommonProjectActions.renameProjectAction());
            actions.add(CommonProjectActions.moveProjectAction());
            actions.add(CommonProjectActions.copyProjectAction());
            actions.add(CommonProjectActions.deleteProjectAction());
            
            actions.add(SystemAction.get(FindAction.class)); 
            
            // honor 57874 contact
           actions.addAll(Utilities.actionsForPath("Projects/Actions"));
            
            actions.add(null);            
            actions.add(CommonProjectActions.customizeProjectAction());            
            
            return actions.toArray(new Action[actions.size()]);
            // return actions.toArray(new Action[actions.size()]);
       }
        
    }

}

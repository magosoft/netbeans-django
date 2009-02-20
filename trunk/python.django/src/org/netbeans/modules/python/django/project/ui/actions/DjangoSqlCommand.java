/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.actions;

import java.io.File;
import java.util.ArrayList;
import org.netbeans.modules.python.api.PythonMIMEResolver;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.django.project.DjangoActionProvider;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.ui.customizer.RunSqlCommandPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoSqlCommand extends Command {

    private static final String COMMAND_ID = DjangoActionProvider.DJANGO_SQL_COMMAND;

    public DjangoSqlCommand(DjangoProject djangoProject) {
        super(djangoProject);
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        String[] djangoApplication = getDjangoApplication();
        RunSqlCommandPanel runSqlCommandPanel = new RunSqlCommandPanel(djangoApplication);
        DialogDescriptor input = new DialogDescriptor(runSqlCommandPanel,"Run SQL Command");//NOI18N
        input.setOptions(new Object[]{runSqlCommandPanel.getOKButton(), runSqlCommandPanel.getCancelButton()});
        if (DialogDisplayer.getDefault().notify(input) == runSqlCommandPanel.getOKButton()) {
            StringBuilder scriptargs = new StringBuilder(runSqlCommandPanel.getCommand());
            if (!runSqlCommandPanel.getApplicationName().equals("")) {
                scriptargs.append(" ");
                scriptargs.append(runSqlCommandPanel.getApplicationName());
            }
            if (!runSqlCommandPanel.getArgs().equals("")) {
                scriptargs.append(" ");
                scriptargs.append(runSqlCommandPanel.getArgs());
            }
            run(scriptargs.toString());
        }
        
    }
 
    
   @Override
    public boolean isActionEnabled(Lookup context) throws IllegalArgumentException {
        DjangoProject djangoProject = getProject();
        PythonPlatform platform = DjangoProjectUtil.getActivePlatform(djangoProject);
        if (platform == null) {
            return false;
        }
        final FileObject fo = findMainFile (djangoProject);
        if (fo == null) {
            return false;
        }
        return PythonMIMEResolver.PYTHON_MIME_TYPE.equals(fo.getMIMEType());
        
    }

  /* private FileObject findMainFile(DjangoProject djangoProject) {
         final FileObject[] roots = djangoProject.getDjangoSourceRoots().getRoots();
      //  final String mainFile = djangoProject.getEvaluator().getProperty(DjangoProjectProperties.MAIN_FILE);
        final String mainFile = "manage.py";
        if (mainFile == null) {
            return null;
        }
        FileObject fo = null;
        for (FileObject root : roots) {
            fo = root.getFileObject(mainFile);
            if (fo != null) {
                break;
            }
        }
        return fo;
    } */


}

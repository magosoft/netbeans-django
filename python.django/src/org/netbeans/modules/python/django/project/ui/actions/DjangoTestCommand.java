/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.actions;

import org.netbeans.modules.python.api.PythonMIMEResolver;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.django.project.DjangoActionProvider;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.ui.customizer.RunTestPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;



/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoTestCommand extends Command {

    private static final String COMMAND_ID = DjangoActionProvider.DJANGO_TEST_COMMAND;

    public DjangoTestCommand(DjangoProject djangoProject) {
        super(djangoProject);
    }
    
    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        String[] djangoApplication = getDjangoApplication();
        RunTestPanel runTestPanel = new RunTestPanel(djangoApplication);
        DialogDescriptor input = new DialogDescriptor(runTestPanel,"Run Test Command");//NOI18N
        input.setOptions(new Object[]{runTestPanel.getOKButton(), runTestPanel.getCancelButton()});
        if (DialogDisplayer.getDefault().notify(input) == runTestPanel.getOKButton()) {
            StringBuilder scriptargs = new StringBuilder(runTestPanel.getCommand());
            if (!runTestPanel.getApplicationName().equals("")) {
                scriptargs.append(" ");
                scriptargs.append(runTestPanel.getApplicationName());
            }
            System.out.println("ARG:"+runTestPanel.getArgs());
            if (!runTestPanel.getArgs().equals("")) {
                scriptargs.append(".");
                scriptargs.append(runTestPanel.getArgs());
            }
            System.out.println("Script "+scriptargs.toString());
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

}

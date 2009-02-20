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
import org.netbeans.modules.python.django.project.ui.customizer.RunCommandPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoCommand extends Command {

    private static final String COMMAND_ID = DjangoActionProvider.DJANGO_COMMAND;

    public DjangoCommand(DjangoProject djangoProject) {
        super(djangoProject);
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        String[] djangoApplication = getDjangoApplication();
        RunCommandPanel runCommandPanel = new RunCommandPanel(djangoApplication);
        DialogDescriptor input = new DialogDescriptor(runCommandPanel,"Run Command");//NOI18N
        input.setOptions(new Object[]{runCommandPanel.getOKButton(), runCommandPanel.getCancelButton()});
        if (DialogDisplayer.getDefault().notify(input) == runCommandPanel.getOKButton()) {
            StringBuilder scriptargs = new StringBuilder(runCommandPanel.getCommand());
            if (!runCommandPanel.getApplicationName().equals("")) {
                scriptargs.append(" ");
                scriptargs.append(runCommandPanel.getApplicationName());
            }
            if (!runCommandPanel.getArgs().equals("")) {
                scriptargs.append(" ");
                scriptargs.append(runCommandPanel.getArgs());
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

}

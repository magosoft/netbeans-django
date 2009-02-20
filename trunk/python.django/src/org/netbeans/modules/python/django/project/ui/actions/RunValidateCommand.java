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
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Ravi Hingarajiya
 */
public class RunValidateCommand extends Command {

    private static final String COMMAND_ID = DjangoActionProvider.COMMAND_VALIDATE; 

    public RunValidateCommand (DjangoProject djangoProject) {
        super(djangoProject);
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        String scriptargs = "validate";
        run(scriptargs);
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.actions;


import org.netbeans.modules.python.api.PythonMIMEResolver;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.ui.customizer.CustomizerRun;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Ravi Hingarajiya
 */
public class RunCommand extends Command {

    private static final String COMMAND_ID = ActionProvider.COMMAND_RUN;
    
    public RunCommand (DjangoProject project) {
        super(project);
        
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {

        CustomizerRun customizerRun = new CustomizerRun();
        DialogDescriptor input = new DialogDescriptor(customizerRun, "Run Configuration");//NOI18N
        input.setOptions(new Object[]{customizerRun.getOKButton(), customizerRun.getCancelButton()});
        if (DialogDisplayer.getDefault().notify(input) == customizerRun.getOKButton()) {
            String portnumber = customizerRun.getPortNumber().trim();
            String scriptargs = "runserver"+" "+portnumber;
            run(scriptargs);
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
        System.out.println("File OBJECT:"+fo.getName());
        if (fo == null) {
            return false;
        }
        return PythonMIMEResolver.PYTHON_MIME_TYPE.equals(fo.getMIMEType());
    }

}

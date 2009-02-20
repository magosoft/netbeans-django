/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.netbeans.modules.python.api.PythonMIMEResolver;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.django.project.DjangoActionProvider;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.ui.customizer.DjangoTemplatePanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoAdminTemplate extends Command {

    private static final String COMMAND_ID = DjangoActionProvider.ADMIN_TEMPLATE;

    public DjangoAdminTemplate (DjangoProject djangoProject) {
        super(djangoProject);
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        //String[] djangoTemplate = getDjangoTemplate();
        String[] djangoTemplate = new String[1];
        djangoTemplate[0] = "Admin";
        DjangoTemplatePanel djangoTemplatePanel = new DjangoTemplatePanel(djangoTemplate);
        DialogDescriptor input = new DialogDescriptor(djangoTemplatePanel,"Customize Admin Template");//NOI18N
        input.setOptions(new Object[]{djangoTemplatePanel.getOKButton(), djangoTemplatePanel.getCancelButton()});
        if (DialogDisplayer.getDefault().notify(input) == djangoTemplatePanel.getOKButton()) {

              String templateName = djangoTemplatePanel.getTemplateName();
              String templateURL = djangoTemplatePanel.getUrlName();
              copyFolder(templateName);
        }
    }
    
    public String[] getDjangoTemplate() {
        String defaultDjango = null;
        ArrayList<String> a1 = new ArrayList<String>();
        String temp = System.getProperty("netbeans.dirs");
        StringTokenizer token = new StringTokenizer(temp, File.pathSeparator);
        String pattern = "nbPython";
        boolean found = false;
        while (!found && token.hasMoreTokens() ){
            String tempToken = token.nextToken();
            if(tempToken.contains(pattern) || tempToken.contains(pattern.toLowerCase()) ){
                defaultDjango = tempToken + File.separator + "django-1.0" ;
                found = true;
            }
        }
        
        String template = "django"+File.separator+"contrib";
        FileObject templateDir = FileUtil.toFileObject(new File(defaultDjango+File.separator+template));
        
        String directory = templateDir.getPath();
        File projectDirectory  = new File(directory);
            if(projectDirectory.isDirectory()) {
                String s[] = projectDirectory.list();
                for (int i=0;i<s.length;i++) {
                        File applicationDirectory = new File(directory+File.separator+s[i]);
                        if (applicationDirectory.isDirectory()) {
                            File f1 = new File(directory+File.separator+s[i]+File.separator+"templates");
                            if (f1.isDirectory()) {
                                a1.add(applicationDirectory.getName());
                        }
                    }
                }
            }
        
        int number = a1.size();
        String[] result = new String[number]; 
        int j=0;
        for (String s : a1) {
            result[j] = s;
            j=j+1;
        }
        return result;
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

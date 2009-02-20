/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.netbeans.modules.python.api.PythonExecution;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.ui.customizer.DjangoProjectProperties;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * @author Ravi Hingarajiya
 */
public abstract class Command {


    private final DjangoProject project;
    private final DjangoProjectProperties properties;
    
    private String defaultPython;

    public Command(DjangoProject project) {
        this.project = project;
        assert project != null;
        properties = new DjangoProjectProperties(this.project);
    }

    public abstract String getCommandId();

    public abstract void invokeAction(Lookup context) throws IllegalArgumentException;

    public abstract boolean isActionEnabled(Lookup context) throws IllegalArgumentException;

    public boolean asyncCallRequired() {
        return true;
    }

    public boolean saveRequired() {
        return true;
    }

    public final DjangoProject getProject() {
        return project;
    }
    public Node[] getSelectedNodes(){
        return TopComponent.getRegistry().getCurrentNodes();
    }

    public void run (String scriptargs) {
        String temp = System.getProperty("netbeans.dirs");
        StringTokenizer token = new StringTokenizer(temp, File.pathSeparator);
        String pattern = "nbPython";
        boolean found = false;
        while (!found && token.hasMoreTokens() ){
            String tempToken = token.nextToken();
            if(tempToken.contains(pattern) || tempToken.contains(pattern.toLowerCase()) ){
                defaultPython = tempToken + File.separator + "django-1.0" ;
                found = true;
                //System.out.println(defaultPython);
            }
        }

        DjangoProject djangoProject =  getProject();
        final PythonPlatform platform = DjangoProjectUtil.getActivePlatform(djangoProject);
        assert platform != null;
        final FileObject script = findMainFile(djangoProject);
        final FileObject parent = script.getParent();
        assert script != null;        
        final PythonExecution pyexec = new PythonExecution();
        pyexec.setDisplayName (ProjectUtils.getInformation(djangoProject).getDisplayName());                
        String path = FileUtil.toFile(parent).getAbsolutePath();
        pyexec.setWorkingDirectory(path);        
        pyexec.setCommand(platform.getInterpreterCommand());

        //Set python script
        path = FileUtil.toFile(script).getAbsolutePath();
        pyexec.setScript(path);
        pyexec.setCommandArgs(platform.getInterpreterArgs());        

        //build path & set
        final ArrayList<String> pythonPath = new ArrayList<String>();
        pythonPath.addAll(platform.getPythonPath());
        for (FileObject fo : djangoProject.getDjangoSourceRoots().getRoots()) {
            File f = FileUtil.toFile(fo);
            pythonPath.add(f.getAbsolutePath());
        }

        pyexec.setPath(PythonPlatform.buildPath(pythonPath));
        pyexec.setPath(defaultPython);
        pyexec.setScriptArgs(scriptargs);
        pyexec.setShowControls(true);
        pyexec.setShowInput(true);
        pyexec.setShowWindow(true);
        pyexec.run();
    }

    public void copyFolder(String templateName) {

        String temp = System.getProperty("netbeans.dirs");
        StringTokenizer token = new StringTokenizer(temp, File.pathSeparator);
        String pattern = "nbPython";
        String defaultDjango = null;
        boolean found = false;

        while (!found && token.hasMoreTokens() ){
            String tempToken = token.nextToken();
            if(tempToken.contains(pattern) || tempToken.contains(pattern.toLowerCase()) ){
                defaultDjango = tempToken + File.separator + "django-1.0" ;
                found = true;
                //System.out.println(defaultPython);
            }
        }
        String srcarg = defaultDjango + File.separator + "django" + File.separator + "contrib" + File.separator + templateName;
        String srcarg1 = srcarg.replace(' ','$');


        DjangoProject djangoProject =  getProject();
        final PythonPlatform platform = DjangoProjectUtil.getActivePlatform(djangoProject);
        assert platform != null;
        FileObject script1 = findMainFile(djangoProject);
        FileObject parent1 = script1.getParent();

        String destarg = FileUtil.toFile(parent1).getAbsolutePath()+File.separator+templateName;
        String destarg1 = destarg.replace(' ','$');
    
        final FileObject script = FileUtil.toFileObject(new File(defaultDjango+File.separator+"util"+File.separator+"CopyFolder.py"));
        final FileObject parent = script.getParent();

        final PythonExecution pyexec = new PythonExecution();
        pyexec.setDisplayName (ProjectUtils.getInformation(djangoProject).getDisplayName());
        String path = FileUtil.toFile(parent).getAbsolutePath();
        System.out.println("path" + path);
        pyexec.setWorkingDirectory(path);
        pyexec.setCommand(platform.getInterpreterCommand());

        //Set python script
        path = FileUtil.toFile(script).getAbsolutePath();
        pyexec.setScript(path);
        pyexec.setCommandArgs(platform.getInterpreterArgs());

        //build path & set
        final ArrayList<String> pythonPath = new ArrayList<String>();
        pythonPath.addAll(platform.getPythonPath());
        pythonPath.add(defaultDjango+File.separator+"util");


        pyexec.setPath(PythonPlatform.buildPath(pythonPath));
        pyexec.setPath(defaultDjango);
        pyexec.setScriptArgs(srcarg1+" "+destarg1);
        pyexec.setShowControls(true);
        pyexec.setShowInput(true);
        pyexec.setShowWindow(true);
        pyexec.run();



    }

    public String[] getDjangoApplication() {
        ArrayList<String> a1 = new ArrayList();
        a1.add(" ");
        DjangoProject djangoProject = getProject();
        final FileObject[] roots = djangoProject.getDjangoSourceRoots().getRoots();
        for (FileObject root : roots) {
           String directory = root.getPath();
           File projectDirectory  = new File(directory);
           if(projectDirectory.isDirectory()) {
                String s[] = projectDirectory.list();
                for (int i=0;i<s.length;i++) {
                    File applicationDirectory = new File(directory+File.separator+s[i]);
                    if (applicationDirectory.isDirectory()) {
                        File f1 = new File(directory+File.separator+s[i]+File.separator+"models.py");
                        if (f1.isFile()) {
                            a1.add(applicationDirectory.getName());
                        }
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
    protected static FileObject findMainFile (final DjangoProject djangoProject) {
        
        final FileObject[] roots = djangoProject.getDjangoSourceRoots().getRoots();
      //final String mainFile = djangoProject.getEvaluator().getProperty(DjangoProjectProperties.MAIN_FILE);
        final String mainFile = "manage.py";
        
        if (mainFile == null) {
            return null;
        }
        FileObject fo = null;
        for (FileObject root : roots) {
            fo = root.getFileObject(mainFile);
            System.out.println("File Object:"+fo.getName());
            if (fo != null) {
                break;
            }
        }
        return fo;
    }

    protected DjangoProjectProperties getProperties(){
        return properties;
    }
}

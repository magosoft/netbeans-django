/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.python.django.project.ui.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.LifecycleManager;
import org.openide.WizardDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

public final class DjangoApplicationWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor.Panel targetChooserPanel;
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        Project project = Templates.getProject(wizard);
        File f1 = new File("src");
        
       // Templates.setTargetFolder(wizard);

        Sources sources = project.getLookup().lookup(Sources.class);
        SourceGroup[] sg = sources.getSourceGroups(Sources.TYPE_GENERIC);
        
        if (panels == null) {
            targetChooserPanel = 
                    Templates.createSimpleTargetChooser(project, sg);
            panels = new WizardDescriptor.Panel[]{
                        targetChooserPanel
                    };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    public Set instantiate() throws IOException, DataObjectNotFoundException {
        final Set<FileObject> resultSet = new HashSet<FileObject>();
        FileObject template = 
                Repository.getDefault().getDefaultFileSystem().findResource("Templates/Python/init.py");
        FileObject template1 =
                Repository.getDefault().getDefaultFileSystem().findResource("Templates/Python/views.py");
        FileObject template2 =
                Repository.getDefault().getDefaultFileSystem().findResource("Templates/Python/models.py");
       
        FileObject dir = Templates.getTargetFolder(wizard);
        System.out.println("dir :"+dir);
        String targetName = Templates.getTargetName(wizard);
        File parent = FileUtil.toFile(dir);
        File packageFile= new File(parent.getAbsolutePath() + File.separator + targetName);
        File templateDirectory = new File(parent.getAbsolutePath() + File.separator + targetName + File.separator + "templates");
        File templateAppDirectory = new File(parent.getAbsoluteFile()+File.separator+ targetName + File.separator + "templates" + File.separator + targetName);
        File mediaDirectory = new File(parent.getAbsolutePath() + File.separator + targetName + File.separator + "media");
        File cssDirectory = new File(parent.getAbsolutePath() + File.separator + targetName + File.separator + "media" + File.separator+ "css");
        File imageDirectory = new File(parent.getAbsolutePath() + File.separator + targetName + File.separator + "media" + File.separator + "image");
        File jsDirectory = new File(parent.getAbsolutePath() + File.separator + targetName + File.separator + "media" + File.separator + "js");
        FileObject newPackage = createPackage(packageFile);
        FileObject newtemplateApp = createPackage(templateAppDirectory);

        resultSet.add(newPackage);
        resultSet.add(createPackage(templateDirectory));
        resultSet.add(newtemplateApp);
        resultSet.add(createPackage(mediaDirectory));
        resultSet.add(createPackage(cssDirectory));
        resultSet.add(createPackage(imageDirectory));
        resultSet.add(createPackage(jsDirectory));
        FileObject initFile = createFile(template, newPackage, "__init__.py").getPrimaryFile();
        FileObject modelsFile = createFile(template2,newPackage,"models.py").getPrimaryFile();
        FileObject viewsFile = createFile(template1,newPackage,"views.py").getPrimaryFile();
        resultSet.add(initFile);
        resultSet.add(modelsFile);
        resultSet.add(viewsFile);
        try {
            String insertString = "\n" +"\t"+" '"+ targetName + "',";
            String findString = "INSTALLED_APPS = (";
            editFile(dir,"settings.py",findString,insertString);
            insertString ="\n" + "\t" + "(r'^"+targetName+"/$','"+targetName+".views.index'),";
            findString ="urlpatterns = patterns('',";
            editFile(dir,"urls.py",findString,insertString);

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return resultSet;
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
    
    private FileObject createPackage(File dir) throws IOException{
        FileObject packageFO = FileUtil.createFolder(dir);
        return packageFO;
    }

   private void editFile(FileObject projectDir,String fileLocation,String findString,String result) throws BadLocationException, DataObjectNotFoundException, IOException {
       
       FileObject fo = projectDir.getFileObject(fileLocation); // NOI18N
        if (fo != null) {
            BaseDocument bdoc = null;
            
                DataObject dobj = DataObject.find(fo);
                EditorCookie ec = dobj.getCookie(EditorCookie.class);
                if (ec != null) {
                    javax.swing.text.Document doc = null;
                        try {
                            doc = ec.openDocument();
                            String text = doc.getText(0,doc.getLength()).toString();
                            int offset = text.lastIndexOf(findString); 
                            while(offset!=-1)
                                if (text.charAt(offset) == '\n') {
                                    break;
                                } else {
                                    offset++;
                                }
                            
                            doc.insertString(offset,result, null);
                            SaveCookie sc = dobj.getCookie(SaveCookie.class);
                            if (sc != null) {
                                sc.save();
                            } else {
                                LifecycleManager.getDefault().saveAll();
                            }
                     } catch (Exception e){
                         
                      
                    // Replace contents wholesale
                    
         }
     }
    }
 }
  

    private DataObject createFile(FileObject template, FileObject sourceDir, String name) throws IOException {
        DataFolder dataFolder = DataFolder.findFolder(sourceDir);
        DataObject dataTemplate = DataObject.find(template);
        //Strip extension when needed
        int index = name.lastIndexOf('.');
        if (index >0 && index<name.length()-1 && "py".equalsIgnoreCase(name.substring(index+1))) {
            name = name.substring(0, index);
        }
        return dataTemplate.createFromTemplate(dataFolder, name);
    }
}

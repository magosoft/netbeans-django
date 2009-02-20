/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.python.django.project.template;

/**
 *
 * @author Ravi Hingarajiya
 * @author Vasant Patel
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.python.django.project.DjangoProjectType;
import org.netbeans.modules.python.django.project.SourceRoots;
import org.netbeans.modules.python.project.ui.customizer.PythonProjectProperties;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NewDjangoProjectWizardIterator implements WizardDescriptor.ProgressInstantiatingIterator {
    
    static final String SET_AS_MAIN = "setAsMain";  //NOI18N
    static final String MAIN_FILE ="mainFile";      //NOI18N
    static final String PROP_PROJECT_NAME = "projectName";  //NOI18N
    static final String PROP_PROJECT_LOCATION = "pojectLocation";   //NOI18N
    static final String PROP_PLATFORM_ID = "platform";              //NOI18N
    static final String SOURCE_ROOTS = "sources";                   //NOI18N
    static final String TEST_ROOTS = "tests";                       //NOI18N
    static final String DATABASE_ENGINE = "databaseengine";
    static final String DATABASE_NAME = "databasename";
    static final String DATABASE_HOST = "databasehost";
    static final String DATABASE_USERNAME ="databaseusername";
    static final String DATABASE_PASSWORD = "databasepasssword";
    static final String DATABASE_PORT = "databaseport";

    
    DjangoFileWriter djangoFileWriter;
    String[] databaseinfo;
   
    public static enum WizardType {
        APP,
        EXISTING,
    }

    private final WizardType wizardType;
    private WizardDescriptor descriptor;
    private WizardDescriptor.Panel[] panels;
    private int index;

    public NewDjangoProjectWizardIterator() {    
        this(WizardType.APP);
    }

    private NewDjangoProjectWizardIterator(WizardType wizardType) {
        this.wizardType = wizardType;
    }   
    
    public static NewDjangoProjectWizardIterator createApplication () {
        return new NewDjangoProjectWizardIterator();
    }

    public static NewDjangoProjectWizardIterator createExistingProject(){
        return new NewDjangoProjectWizardIterator (WizardType.EXISTING);
    }
    public void initialize(WizardDescriptor wizard) {
        descriptor = wizard;
        index = 0;
        panels = createPanels();
        // normally we would do it in uninitialize but we have listener on ide options (=> NPE)
        initDescriptor(wizard);
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
        descriptor = null;
    }

    public Set instantiate() throws IOException {
        assert false : "Cannot call this method if implements WizardDescriptor.ProgressInstantiatingIterator.";
        return null;
    }

    public Set instantiate(ProgressHandle handle) throws IOException {
        final Set<FileObject> resultSet = new HashSet<FileObject>();

        handle.start(5);

        String msg = NbBundle.getMessage(
                NewDjangoProjectWizardIterator.class, "LBL_NewDjangoProjectWizardIterator_WizardProgress_CreatingProject");
        handle.progress(msg, 3);

        // project
        File projectDirectory = (File) descriptor.getProperty(PROP_PROJECT_LOCATION);
        String projectName = (String) descriptor.getProperty(PROP_PROJECT_NAME);
       
        AntProjectHelper helper = createProject(projectDirectory, projectName);
        resultSet.add(helper.getProjectDirectory());

        if (wizardType == WizardType.APP) {
            // sources
            FileObject sourceDir = createSourceRoot();           
            File parent = FileUtil.toFile(sourceDir);
            File packageFile= new File(parent.getAbsolutePath() + File.separator +(String)descriptor.getProperty(NewDjangoProjectWizardIterator.PROP_PROJECT_NAME));            
            resultSet.add(sourceDir);
            
            resultSet.add(createMainFile(FileUtil.getConfigFile("Templates/Python/manage.py"), sourceDir, "manage").getPrimaryFile());
            resultSet.add(createMainFile(FileUtil.getConfigFile("Templates/Python/urls.py"), sourceDir, "urls").getPrimaryFile());
             resultSet.add(createMainFile(FileUtil.getConfigFile("Templates/Python/settings.py"), sourceDir, "settings").getPrimaryFile());
            resultSet.add(createMainFile(FileUtil.getConfigFile("Templates/Python/init.py"), sourceDir, "__init__").getPrimaryFile());
           

            // main file
            //final String mainName = (String) descriptor.getProperty(NewDjangoProjectWizardIterator.MAIN_FILE);
           /* if (mainName != null) {
          
                resultSet.add(createMainFile(Repository.getDefault().getDefaultFileSystem().findResource( "Templates/Python/Module.py"),
                        sourceDir,mainName).getPrimaryFile());
            }
           */
        }

        msg = NbBundle.getMessage(
                NewDjangoProjectWizardIterator.class, "LBL_NewDjangoProjectWizardIterator_WizardProgress_PreparingToOpen");
        handle.progress(msg, 5);

        
        if (wizardType == WizardType.APP) {

            djangoFileWriter = new DjangoFileWriter();

            databaseinfo = new String[6];
            String database = (String) descriptor.getProperty(DATABASE_ENGINE);
            
            if(database!=null) {
                databaseinfo[0] = (String) descriptor.getProperty(DATABASE_ENGINE);
                databaseinfo[1] = (String) descriptor.getProperty(DATABASE_NAME);
                databaseinfo[2] = (String) descriptor.getProperty(DATABASE_USERNAME);
                databaseinfo[3] = (String) descriptor.getProperty(DATABASE_PASSWORD);
                databaseinfo[4] = (String) descriptor.getProperty(DATABASE_HOST);
                databaseinfo[5] = (String) descriptor.getProperty(DATABASE_PORT);
            } else {
                databaseinfo[0] = new String();
                databaseinfo[1] = new String();
                databaseinfo[2] = new String();
                databaseinfo[3] = new String();
                databaseinfo[4] = new String();
                databaseinfo[5] = new String();
                
            }
            djangoFileWriter.editurlsFile(helper.getProjectDirectory(), "src/urls.py",projectName);
            djangoFileWriter.editsettingsFile(helper.getProjectDirectory(), "src/settings.py",projectName,databaseinfo);

        }
        return resultSet;
    }
 private FileObject createPackage(File dir) throws IOException{
        FileObject packageFO = FileUtil.createFolder(dir);
        return packageFO;
    }
    public String name() {
        return NbBundle.getMessage(NewDjangoProjectWizardIterator.class, "LBL_IteratorName", index + 1, panels.length);
    }

    public boolean hasNext() {
        return index < panels.length - 1;
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

    public WizardDescriptor.Panel current() {
        // wizard title
        String title = NbBundle.getMessage(NewDjangoProjectWizardIterator.class, wizardType == WizardType.APP ? "TXT_DjangoProject" : "TXT_ExistingDjangoProject");
        descriptor.putProperty("NewProjectWizard_Title", title); // NOI18N
        return panels[index];
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    static String getFreeFolderName (final File owner, final String proposal){
        assert owner != null;
        assert proposal != null;
        String freeName = proposal;
        File f = new File (owner, freeName);
        int counter = 1;

        while (f.exists())  {
            counter++;
            freeName = proposal+counter;
            f = new File (owner, freeName);
        }
        return freeName;
    }

    private WizardDescriptor.Panel[] createPanels() {
       switch(wizardType){
           case APP:
           {
                String[] steps = new String[] {
                    NbBundle.getBundle(NewDjangoProjectWizardIterator.class).getString("LBL_ProjectNameLocation"),
                };

                PanelConfigureProject configureProjectPanel = new PanelConfigureProject(wizardType, steps);
                PanelPropertyProject panelPropertyProject = new PanelPropertyProject(wizardType,steps);
                return new WizardDescriptor.Panel[] {
                    configureProjectPanel,
                    panelPropertyProject,
                };
           }
           
           case EXISTING:
           {
                String[] steps = new String[] {
                    NbBundle.getBundle(NewDjangoProjectWizardIterator.class).getString("LBL_ProjectNameLocation"),
                    NbBundle.getMessage(NewDjangoProjectWizardIterator.class,"LBL_ProjectSources"),
                };
                
                PanelConfigureProject configureProjectPanel = new PanelConfigureProject(wizardType, steps);
                PanelConfigureSources configureSourcesPanel = new PanelConfigureSources(wizardType, steps);
                return new WizardDescriptor.Panel[] {
                    configureProjectPanel,
                    configureSourcesPanel,
                };
           }
           default:
               throw new IllegalStateException(wizardType.toString());
       }
        
    }

    // prevent incorrect default values (empty project => back => existing project)
    private void initDescriptor(WizardDescriptor settings) {
        settings.putProperty(PROP_PROJECT_NAME, null);
        settings.putProperty(PROP_PROJECT_LOCATION, null);
        settings.putProperty(SOURCE_ROOTS, new File[0]);
        settings.putProperty(TEST_ROOTS, new File[0]);
    }    
    
    private AntProjectHelper createProject(final File dir, final String name) throws IOException {

        FileObject projectFO = FileUtil.createFolder(dir);
        final AntProjectHelper helper = ProjectGenerator.createProject(projectFO, DjangoProjectType.TYPE);
        
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run () throws MutexException {
                    try {
                    // configure
                    final Element data = helper.getPrimaryConfigurationData(true);
                    Document doc = data.getOwnerDocument();
                    Element nameEl = doc.createElementNS(DjangoProjectType.PROJECT_CONFIGURATION_NAMESPACE, "name"); // NOI18N
                    nameEl.appendChild(doc.createTextNode(name));
                    data.appendChild(nameEl);


                    EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);

                    configureSources(helper, data, properties);
                  //  configureRuntime(properties);
                    configureMainFile(properties);        
                    helper.putPrimaryConfigurationData(data, true);
                    helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);

                    Project project = ProjectManager.getDefault().findProject(helper.getProjectDirectory());
                    ProjectManager.getDefault().saveProject(project);
                    return null;
                    } catch (IOException ioe) {
                       throw new MutexException(ioe); 
                    }
                }
            });
        } catch (MutexException e) {
            Exception ie = e.getException();
            if (ie instanceof IOException) {
                throw (IOException)ie;
            }
            Exceptions.printStackTrace(e);
        }
        return helper;
    }    

    private void configureSources(final AntProjectHelper helper, final Element data, final EditableProperties properties) {
        List<? extends File> srcDirs = getSources();       
        final Document doc = data.getOwnerDocument();
        final Element sourceRoots = doc.createElementNS(DjangoProjectType.PROJECT_CONFIGURATION_NAMESPACE,SourceRoots.E_SOURCES);
      //  final Element templateRoots = doc.createElementNS(DjangoProjectType.PROJECT_CONFIGURATION_NAMESPACE,SourceRoots.E_TEMPLATE);
        final File projectDirectory = FileUtil.toFile(helper.getProjectDirectory());
        for (File srcDir : srcDirs) {
            String srcPath = PropertyUtils.relativizeFile(projectDirectory, srcDir);
            
            // # 132319
            if (srcPath == null || srcPath.startsWith("../")) { // NOI18N
                // relative path, change to absolute
                srcPath = srcDir.getAbsolutePath();
            }
            Element root = doc.createElementNS (DjangoProjectType.PROJECT_CONFIGURATION_NAMESPACE,"root");   //NOI18N
            String propName;
            String name = srcDir.getName();
            propName = name + ".dir";    //NOI18N
            int rootIndex = 1;                            
            while (properties.containsKey(propName)) {
                rootIndex++;
                propName = name + rootIndex + ".dir";
                System.out.println("while Loop"+propName);
                //NOI18N;
            }
            root.setAttribute ("id",propName);   //NOI18N
            sourceRoots.appendChild(root);
         //   sourceRoots.appendChild(templateRoots);
            properties.setProperty(propName, srcPath);            
        }
        data.appendChild (sourceRoots);
        
        final Element testRoots = doc.createElementNS(DjangoProjectType.PROJECT_CONFIGURATION_NAMESPACE,SourceRoots.E_TESTS);
        data.appendChild (testRoots);
    }    

    private void configureRuntime (final EditableProperties properties) {
        String platformId = (String) descriptor.getProperty(PROP_PLATFORM_ID);
        
        assert platformId != null;
        properties.setProperty(PythonProjectProperties.ACTIVE_PLATFORM, platformId);
        properties.setProperty(PythonProjectProperties.PYTHON_LIB_PATH, "");    //NOI18N
        properties.setProperty(PythonProjectProperties.JAVA_LIB_PATH, "");    //NOI18N
    }
    
    private void configureMainFile(EditableProperties properties) {
        String mainFile = (String) descriptor.getProperty(NewDjangoProjectWizardIterator.MAIN_FILE);
        if (mainFile != null) {
            properties.setProperty(PythonProjectProperties.MAIN_FILE, mainFile);
        }
    }   

    
    private FileObject createSourceRoot() throws IOException {
        return FileUtil.createFolder(getSources().get(0));
    }
    
    private List<? extends File> getSources () {
        if (wizardType == WizardType.APP) {
            
            return Collections.singletonList(new File ((File) descriptor.getProperty(PROP_PROJECT_LOCATION),"src"));
        }
        else if (wizardType == WizardType.EXISTING) {
            return Arrays.asList((File[])descriptor.getProperty(SOURCE_ROOTS));
        }
        else {
            //todo:
            throw new UnsupportedOperationException();
        }
    }
   
    private DataObject createMainFile(FileObject template, FileObject sourceDir, String name) throws IOException {
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

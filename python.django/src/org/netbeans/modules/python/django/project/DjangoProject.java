/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import org.netbeans.modules.python.project.PythonProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import javax.swing.Icon;
import javax.swing.ImageIcon;
//import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
//import org.netbeans.modules.gsfpath.api.classpath.GlobalPathRegistry;
import org.netbeans.modules.python.django.project.ui.customizer.DjangoCustomizerProvider;
import org.netbeans.modules.python.project.UpdateHelper;
import org.netbeans.modules.python.project.UpdateImplementation;
import org.netbeans.modules.python.project.gsf.ClassPathProviderImpl;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;



/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoProject extends PythonProject {

    public  Icon PROJECT_ICON = new ImageIcon(ImageUtilities.loadImage("org/netbeans/modules/python/django/project/resource/django-icon-naive-16.png")); // NOI18N

    protected SourceRoots sourceRoots;
    protected SourceRoots testRoots;
  // protected SourceRoots templateRoots;
    DjangoLogicalView djangoLogicalView;
    DjangoOpenedHook DjangoOpenedHook;
    
     //AuxiliaryConfiguration aux;
     //private DjangoSources djangoSources;
    
    public DjangoProject(final AntProjectHelper helper){
          //super(helper,0);
       // super();
        
        assert helper != null;
        this.helper = helper;
        this.aux  = helper.createAuxiliaryConfiguration();
        this.evaluator = createEvaluator();
        this.updateHelper = new UpdateHelper(UpdateImplementation.NULL,helper);
        refHelper = new ReferenceHelper(helper, aux, evaluator);
        this.sourceRoots = SourceRoots.create(updateHelper, evaluator, refHelper, false);
      //  this.templateRoots=SourceRoots.create(updateHelper, evaluator, refHelper, false);
        this.testRoots = SourceRoots.create(updateHelper, evaluator, refHelper, false); 
         
        this.DjangoOpenedHook = new DjangoOpenedHook();
        djangoLogicalView = new DjangoLogicalView(this);
        
        this.lkp = createLookup();
       
    }
    
    AntProjectHelper getHelper () {
        return this.helper;
    }
    
//  @Override
    public Lookup createLookup() {
             
        return Lookups.fixed(new Object[]{
              //s projectOpenedHook,
                this, //project spec requires a project be in it's own lookup
                this.aux,  //Auxiliary configuartion to store bookmarks and so on
                new DjangoActionProvider(this), //Provides Standard like build and cleen
                djangoLogicalView, // Logical view if project implementation
               
                new Info(), // Project information Implementation
            //    new DjangoOpenedHook(), //Called by project framework when project is opened (closed)
                new DjangoProjectXmlSavedHook(),  //Called when project.xml changes
                new DjangoSources(helper,evaluator,sourceRoots,testRoots),    //Python source grops - used by package view, factories, refactoring, ...
                new DjangoProjectOperations(this),  //move, rename, copy of project
                new RecommendedTemplatesImpl(this.updateHelper), // Recommended Templates
                new DjangoCustomizerProvider(this), //Django Project customizer

            });
    }
    
    public class DjangoProjectXmlSavedHook extends ProjectXmlSavedHook {
        
        DjangoProjectXmlSavedHook() {}
        
        protected void projectXmlSaved() throws IOException {
            Info info = getLookup().lookup(Info.class);
            assert info != null;
            info.firePropertyChange(ProjectInformation.PROP_NAME);
            info.firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
        }   
    }
   @Override 
   public FileObject getSrcFolder() {
        System.out.println("Project Directory :"+getProjectDirectory().getName());
        return getProjectDirectory();
    }                    
    
    public class DjangoOpenedHook extends ProjectOpenedHook {
        protected void projectOpened() {
            // register project's classpaths to GlobalPathRegistry
            final ClassPathProviderImpl cpProvider = getLookup().lookup(ClassPathProviderImpl.class);
            assert cpProvider != null;
           // GlobalPathRegistry.getDefault().register(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
           // GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
        }

        @Override
        protected void projectClosed() {
            final ClassPathProviderImpl cpProvider = getLookup().lookup(ClassPathProviderImpl.class);
            assert cpProvider != null;
          //  GlobalPathRegistry.getDefault().unregister(ClassPath.BOOT, cpProvider.getProjectClassPaths(ClassPath.BOOT));
          //  GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, cpProvider.getProjectClassPaths(ClassPath.SOURCE));
            try {
                ProjectManager.getDefault().saveProject(DjangoProject.this);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }

    }    
    
    private static final class RecommendedTemplatesImpl implements RecommendedTemplates, PrivilegedTemplates {
        
        RecommendedTemplatesImpl (UpdateHelper helper) {
            this.helper = helper;
        }
        
        private final UpdateHelper helper;
        
        // List of primarily supported templates
        
        private static final String[] APPLICATION_TYPES = new String[] { 
            "python"         // NOI18N
        };
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Python/pythonPackage",//NOI18N
            "Templates/Python/Module.py", //NOI18N
            "Templates/Python/ExecutableModule.py" // NOI18N            
        };
        
        public String[] getRecommendedTypes() {
            return APPLICATION_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
        
    }


    public void setName (String name) {
        //TODO       return super.getName();
    }

    @Override
    public String getName () {
       return super.getName();
    }
    private final class Info implements ProjectInformation{
        
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        
        public void addPropertyChangeListener(PropertyChangeListener  listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        public String getDisplayName() {
            return getName();
        }

        public Icon getIcon() {
            return PROJECT_ICON;
        }

        public String getName() {
            return DjangoProject.this.getName();
        }

        public Project getProject() {
            return DjangoProject.this;
        }
        
        void firePropertyChange(String prop) {
            propertyChangeSupport.firePropertyChange(prop , null, null);
        }
    }
    
    public SourceRoots getDjangoSourceRoots () {
        return this.sourceRoots;
    }
    
    
    public SourceRoots getDjangoTestRoots () {
        return this.testRoots;
    }
    

    
}

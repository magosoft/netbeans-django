/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;

import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.api.PythonPlatformManager;
import org.netbeans.modules.python.project.ui.customizer.PythonProjectProperties;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoProjectUtil {
    
    private  DjangoProjectUtil() {}
    
    public static DjangoProject getProject (final Project project) {
        assert project != null;
        return project.getLookup().lookup(DjangoProject.class);
    }
    
    public static AntProjectHelper getProjectHelper (final Project project) {
        final DjangoProject djangoProject = getProject(project);
        return djangoProject == null ? null : djangoProject.getHelper();
    }
    
    public static PythonPlatform getActivePlatform (final Project project) {
        final DjangoProject pp = getProject(project);
        if (pp == null) {
            return null;    //No Django project
        }
        final PythonPlatformManager manager = PythonPlatformManager.getInstance();
        String platformId = pp.getEvaluator().getProperty(PythonProjectProperties.ACTIVE_PLATFORM);
        if (platformId == null) {
            platformId = manager.getDefaultPlatform();
        }
        if (platformId == null) {
            return null;    //No Python platform in the IDE
        }
        return manager.getPlatform(platformId);
    }
   
}

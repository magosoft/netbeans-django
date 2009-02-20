/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoProjectType implements AntBasedProjectType{

    public static String TYPE = "org.netbeans.modules.python.django.project";
    public static final String PROJECT_CONFIGURATION_NAMESPACE = "http://nbpython.dev.java.net/ns/php-project/1"; // NOI18N
    private static final String PROJECT_CONFIGURATION_NAME = "data"; // NOI18N

    private static final String PRIVATE_CONFIGURATION_NAMESPACE = "http://nbpython.dev.java.net/ns/php-project-private/1"; // NOI18N
    private static final String PRIVATE_CONFIGURATION_NAME = "data"; // NOI18N
    
    //Probably it should become a part of django api.
   // public static final String SOURCES_TYPE_DJA = "django"; // NOI18N
     public static String SOURCES_TYPE_PYTHON = "python";
    
    public String getType() {
        return TYPE;
    }

    public Project createProject(AntProjectHelper helper) throws IOException {
       System.out.println("this is Django Project Type");
        assert helper != null;
        return new DjangoProject(helper);
        
    }

    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return shared ? PROJECT_CONFIGURATION_NAME : PRIVATE_CONFIGURATION_NAME;
    }

    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
          return shared ? PROJECT_CONFIGURATION_NAMESPACE : PRIVATE_CONFIGURATION_NAMESPACE;
    }

}

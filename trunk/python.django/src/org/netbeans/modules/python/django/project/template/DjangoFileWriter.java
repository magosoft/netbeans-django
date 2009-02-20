/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.template;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.openide.LifecycleManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoFileWriter {
    
   public void editFile(FileObject projectDir,String fileLocation,String projectName, String insert) {
        FileObject fo = projectDir.getFileObject(fileLocation); // NOI18N
        if (fo != null) {
            BaseDocument bdoc = null;
            try {
                DataObject dobj = DataObject.find(fo);
                EditorCookie ec = dobj.getCookie(EditorCookie.class);
                if (ec != null) {
                    javax.swing.text.Document doc = null;
                        try {
                            doc = ec.openDocument();
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    // Replace contents wholesale
                    if (doc instanceof BaseDocument) {
                        bdoc = (BaseDocument) doc;
                        bdoc.atomicLock();
                    }
                    
                        try {

                            doc.remove(0, doc.getLength());
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    
                    
                    doc.insertString(0, insert, null);
                    SaveCookie sc = dobj.getCookie(SaveCookie.class);
                    
                    if (sc != null) {
                        sc.save();
                    } else {
                        LifecycleManager.getDefault().saveAll();
                    }
                }
            } catch (BadLocationException ble) {
                Exceptions.printStackTrace(ble);
            } catch (DataObjectNotFoundException dnfe) {
                Exceptions.printStackTrace(dnfe);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            } finally {
                if (bdoc != null) {
                    bdoc.atomicUnlock();
                }
            }
        }
        
    }
    
    public void editurlsFile(FileObject projectDir,String fileLocation,String projectName) {

        String insert = null;
         // NOI18N
        insert = "from django.conf.urls.defaults import *\n" +
                "# Uncomment the next two lines to enable the admin:\n" +
                "from django.contrib import admin\n"+
                "admin.autodiscover()\n"+
                "urlpatterns = patterns('',\n"+
                    "\t# Example:\n"+
                    "\t# (r'^"+projectName+"/', include('"+projectName+".foo.urls')),\n"+
                    "\t# Uncomment the admin/doc line below and add 'django.contrib.admindocs' \n"+
                    "\t# to INSTALLED_APPS to enable admin documentation:\n"+
                    "\t# (r'^admin/doc/', include('django.contrib.admindocs.urls')),\n"+
                    "\t# Uncomment the next line to enable the admin:\n"+
                    "\t (r'^admin/(.*)', admin.site.root),\n"+
                ")";
        editFile(projectDir,fileLocation,projectName, insert);
}
    
    public void editsettingsFile(FileObject projectDir,String fileLocation,String projectName,String[] databaseinfo) {

        String insert = null;
         // NOI18N
        insert = "# Django settings for "+projectName+" project. \n" +
                "DEBUG = True \n" +
                "TEMPLATE_DEBUG = DEBUG \n\n" +

                "ADMINS = ( \n" +
                "# ('Your Name', 'your_email@domain.com'), \n" +
                ") \n\n" +

                "MANAGERS = ADMINS \n\n" +

                "DATABASE_ENGINE = '"+databaseinfo[0]+"'           # 'postgresql_psycopg2', 'postgresql', 'mysql', 'sqlite3' or 'oracle'. \n" +
                "DATABASE_NAME = '"+databaseinfo[1]+"'             # Or path to database file if using sqlite3. \n" +
                "DATABASE_USER = '"+databaseinfo[2]+"'             # Not used with sqlite3. \n" +
                "DATABASE_PASSWORD = '"+databaseinfo[3]+"'         # Not used with sqlite3. \n" +
                "DATABASE_HOST = '"+databaseinfo[4]+"'             # Set to empty string for localhost. Not used with sqlite3. \n" +
                "DATABASE_PORT = '"+databaseinfo[5]+"'             # Set to empty string for default. Not used with sqlite3. \n\n" +

                "# Local time zone for this installation. Choices can be found here: \n" +
                "# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name \n" +
                "# although not all choices may be available on all operating systems. \n" +
                "# If running in a Windows environment this must be set to the same as your \n" +
                "# system time zone. \n" +
                "TIME_ZONE = 'America/Chicago'\n\n" +
                
                "# Language code for this installation. All choices can be found here: *\n" +
                "# http://www.i18nguy.com/unicode/language-identifiers.html \n" +
                "LANGUAGE_CODE = 'en-us'\n\n"+
                
                "SITE_ID = 1 \n\n"+
                
                "# If you set this to False, Django will make some optimizations so as not \n"+
                "# to load the internationalization machinery. \n"+
                "USE_I18N = True \n\n"+

                "# Absolute path to the directory that holds media. \n" +
                "# Example: \"/home/media/media.lawrence.com/\" \n" +
                "MEDIA_ROOT = '' \n\n" +
                
                "# URL that handles the media served from MEDIA_ROOT. Make sure to use a \n" +
                "# trailing slash if there is a path component (optional in other cases). \n" +
                "# Examples: \"http://media.lawrence.com\", \"http://example.com/media/\" \n" +
                "MEDIA_URL = '' \n\n" +
                
                "# URL prefix for admin media -- CSS, JavaScript and images. Make sure to use a \n" +
                "# trailing slash. \n" +
                "# Examples: \"http://foo.com/media/\", \"/media/\". \n"+
                "ADMIN_MEDIA_PREFIX = '/media/' \n\n"+
                
                "# Make this unique, and don't share it with anybody. \n"+
                "SECRET_KEY = '$a^$7n#u&i2j7*(y3od%sug5f#@cyrzkl73k56#^e(21@2!9nx' \n\n"+
                
                "# List of callables that know how to import templates from various sources. \n"+
                "TEMPLATE_LOADERS = ( \n"+
                    "\t 'django.template.loaders.filesystem.load_template_source',\n"+
                    "\t 'django.template.loaders.app_directories.load_template_source',\n"+
                    "\t # 'django.template.loaders.eggs.load_template_source', \n"+    
                ")\n\n"+
                
                "MIDDLEWARE_CLASSES = ( \n"+
                    "\t 'django.middleware.common.CommonMiddleware',\n"+
                    "\t 'django.contrib.sessions.middleware.SessionMiddleware',\n"+
                    "\t 'django.contrib.auth.middleware.AuthenticationMiddleware',\n"+
                ")\n\n" +
                
                "ROOT_URLCONF = 'src.urls' \n\n" +

                "TEMPLATE_DIRS = ( \n" +
                    "\t # Put strings here, like \"/home/html/django_templates\" or \"C:/www/django/templates\". \n"+
                    "\t # Always use forward slashes, even on Windows. \n" +
                    "\t # Don't forget to use absolute paths, not relative paths. \n" +
                ")\n\n" +

                "INSTALLED_APPS = ( \n" +
                     "\t 'django.contrib.auth', \n" +
                    "\t 'django.contrib.contenttypes', \n" +
                    "\t 'django.contrib.sessions', \n" +
                    "\t 'django.contrib.sites', \n" +
                    "\t 'django.contrib.admin', \n" +
                ")";
        
 editFile(projectDir,fileLocation,projectName, insert);
}

}

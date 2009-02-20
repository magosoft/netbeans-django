/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui.customizer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.modules.python.django.project.DjangoProject;
import org.netbeans.modules.python.django.project.SourceRoots;
import org.netbeans.modules.python.django.project.DjangoProjectUtil;
import org.netbeans.modules.python.django.project.util.Pair;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import javax.swing.table.AbstractTableModel;
/**
 *
 * @author Ravi Hingarajiya
 * @author Vasant
 */
public class DjangoProjectProperties {

    public static final String SRC_DIR = "src.dir"; //NOI18N
    public static final String MAIN_FILE = "main.file"; //NOI18N
    public static final String APPLICATION_ARGS = "application.args"; //N0I18N
    public static final String ACTIVE_PLATFORM = "platform.active"; //NOI18N
    public static final String PYTHON_LIB_PATH = "python.lib.path"; //NOI18N
    public static final String JAVA_LIB_PATH = "java.lib.path";     //NOI18N
    public static final String DJANGO_LIB_PATH = "django.lib.path";
    public static final String SOURCE_ENCODING = "source.encoding"; //N018N

    private final DjangoProject project;
    private final PropertyEvaluator eval;

    private volatile String encoding;
    private volatile List<Pair<File,String>> sourceRoots;
    private volatile String[] sourceRootLabels;
    private volatile List<Pair<File,String>> testRoots;
    private volatile String[] testRootLabels;
    private volatile String mainModule;
    private volatile String appArgs;
    private volatile ArrayList<String>pythonPath;
    private volatile String activePlatformId;



    @SuppressWarnings("static-access")
    public DjangoProjectProperties(final DjangoProject project){
        assert project != null;
        this.project = project;
        this.eval = project.getEvaluator();
        System.out.println(DjangoProjectProperties.PYTHON_LIB_PATH);
        System.out.println(DjangoProjectProperties.DJANGO_LIB_PATH);
        System.out.println(DjangoProjectProperties.JAVA_LIB_PATH);
    }

    //Properties

    public DjangoProject getProject () {
        return this.project;
    }

    public FileObject getProjectDirectory () {
        return this.project.getProjectDirectory();
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        if(this.encoding == null){
            this.encoding = eval.getProperty(SOURCE_ENCODING);
        }
        return this.encoding;
    }

    class SourceModel extends AbstractTableModel {

        public SourceModel(String[] labels, SourceRoots sourceRoots){
            super();
            this.columnNames=labels;
//            this.data=sourceRoots;
        }
        private String[] columnNames = {};
        private Object[][] data = {};

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }

    public List<Pair<File, String>> getSourceRoots() {
        if(sourceRoots == null){
            final SourceRoots sourceRoots = project.getDjangoSourceRoots();
            final String[] rootLabels = sourceRoots.getRootNames();
            final String[] rootProps = sourceRoots.getRootProperties();
            final URL[] rootURLs = sourceRoots.getRootURLs();
            final List<Pair<File, String>> data = new LinkedList<Pair<File,String>>();
            for(int i=0; i< rootURLs.length; i++){
                final File f = new File(URI.create (rootURLs[i].toExternalForm()));
                final String s = sourceRoots.getRootDisplayName(rootLabels[i], rootProps[i]);
                data.add(Pair.of(f, s));
            }
            this.sourceRoots = data;
            this.sourceRootLabels = rootLabels;
        }
        return this.sourceRoots;
    }

    public void setSourceRoots(final List<Pair<File,String>> sourceRoots){
        assert sourceRoots != null;
        this.sourceRoots = sourceRoots;
    }

    public List<Pair<File, String>>  getTestRoots(){
        if(testRoots == null){
            final SourceRoots testRoots = project.getDjangoTestRoots();
            final String[] rootLabels = testRoots.getRootNames();
            final String[] rootProps = testRoots.getRootProperties();
            final URL[] rootURLs = testRoots.getRootURLs();
            final List<Pair<File,String>> data = new LinkedList<Pair<File,String>>();

            for(int i=0; i< rootURLs.length; i++){
                final File f = new File(URI.create (rootURLs[i].toExternalForm()));
                final String s = testRoots.getRootDisplayName(rootLabels[i],rootProps[i]);
                data.add(Pair.of(f, s));
            }
            this.testRoots = data;
            this.testRootLabels = rootLabels;
        }
        return this.testRoots;
    }

    public void setTestRoots(final List<Pair<File,String>> testRoots){
        assert testRoots != null;
        this.sourceRoots = testRoots;
    }

    public String getMainModule(){
        if(mainModule == null){
            mainModule = eval.getProperty(MAIN_FILE);
        }
        return mainModule;
    }

    public void setMainModule(final String module){
        this.mainModule = module;
    }

    public String getApplicationArgs(){
        if(appArgs == null) {
            appArgs = eval.getProperty(APPLICATION_ARGS);
        }
        return appArgs;
    }

    public void setApplicaionArgs(final String args){
        this.appArgs = args;
    }

    public ArrayList<String> getPythonPath(){
        if(pythonPath==null)
            pythonPath = buildPathList(eval.getProperty(PYTHON_LIB_PATH));
        return pythonPath;
    }

    public void setPythonPath(ArrayList<String> pythonPath){
        assert pythonPath != null;
        this.pythonPath = pythonPath;
    }

    public String getActivePlatformId() {
        if(activePlatformId == null)
            activePlatformId = eval.getProperty(ACTIVE_PLATFORM);
        return activePlatformId;
    }

    public void setActivePlatformId(String activePlatformId) {
        this.activePlatformId = activePlatformId;
    }

    // Storing

    void save() {
        try{
            if(this.sourceRoots != null){
                final SourceRoots sr = this.project.getDjangoSourceRoots();
                sr.putRoots(this.sourceRoots);
            }
            if(this.testRoots != null){
                final SourceRoots sr = this.project.getDjangoTestRoots();
                sr.putRoots(this.testRoots);
            }

            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run() throws IOException {
                    saveProperties();
                    return null;
                }
            });
            ProjectManager.getDefault().saveProject(project);

        }catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }


      }

      private void saveProperties () throws IOException {

        final AntProjectHelper helper = DjangoProjectUtil.getProjectHelper(project);
        // get properties
        final EditableProperties projectProperties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        final EditableProperties privateProperties = helper.getProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH);

        if (mainModule != null) {
            projectProperties.put(MAIN_FILE, mainModule);
        }

        if (encoding != null) {
            projectProperties.put(SOURCE_ENCODING, encoding);
        }

        if (appArgs != null) {
            privateProperties.put(APPLICATION_ARGS, appArgs);
        }
        if (pythonPath != null){
            projectProperties.put(PYTHON_LIB_PATH, buildPathString(pythonPath));
        }
        if (activePlatformId != null)
            projectProperties.put(ACTIVE_PLATFORM, activePlatformId);

        // store all the properties
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, projectProperties);
        helper.putProperties(AntProjectHelper.PRIVATE_PROPERTIES_PATH, privateProperties);

        // additional changes
        // encoding
        if (encoding != null) {
            try {
                FileEncodingQuery.setDefaultEncoding(Charset.forName(encoding));
            } catch (UnsupportedCharsetException e) {
                //When the encoding is not supported by JVM do not set it as default
            }
        }
    }

    private static final String PYTHON_PATH_SEP = "|";
    /**
     *Build a path string from arraylist
     * @param path
     * @return
     */
    private static String buildPathString(ArrayList<String> path){
        StringBuilder pathString = new StringBuilder();
        int count = 0;
        for(String pathEle: path){
            pathString.append(pathEle);
            if (count++ < path.size()){
                pathString.append(PYTHON_PATH_SEP);
            }
        }
        return pathString.toString();
    }
    /**
     *
     * @param pathString
     * @return
     */
    private static ArrayList<String> buildPathList(String pathString){
        ArrayList<String> pathList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(pathString, PYTHON_PATH_SEP);
        while(tokenizer.hasMoreTokens()){
            pathList.add(tokenizer.nextToken());
        }
        return pathList;
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.ui;

import java.awt.Component;
import java.io.File;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.netbeans.modules.python.api.PythonPlatform;
import org.netbeans.modules.python.api.PythonPlatformManager;
import org.openide.awt.HtmlRenderer;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
public class Utils {
    
    public static String browseLocationAction(final Component parent, String path, String title) {
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (path != null && path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(parent)) {
            return FileUtil.normalizeFile(chooser.getSelectedFile()).getAbsolutePath();
        }
        return null;
    }
    
    public static ComboBoxModel createPlatformModel () {
        return new PlatformModel ();
    }
    
    public static ListCellRenderer createPlatformRenderer () {
        return new PlatformRenderer();
    }
    
    
    
    private static class PlatformModel extends DefaultComboBoxModel {
        
        private final PythonPlatformManager manager;
        
        public PlatformModel () {
            manager = PythonPlatformManager.getInstance();
            init ();
        }
        
        private void init () {
            this.removeAllElements();   //init will be used also in case of chnge of installed plaforms
            final List<String> ids = manager.getPlatformList();
            for (String id : ids) {
                PythonPlatform platform = manager.getPlatform(id);
                this.addElement(platform);
            }
        }
    }
    
    private static class PlatformRenderer implements ListCellRenderer {
        
        private final ListCellRenderer delegate;
        
        public PlatformRenderer () {
            delegate = HtmlRenderer.createRenderer();
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String name;
            if (value instanceof PythonPlatform) {
                PythonPlatform key = (PythonPlatform) value;
                name = key.getName();
            }
            else if (value instanceof String) {
                //hndles broken platform for customizer
                name = "<html><font color=\"#A40000\">" //NOI18N
                            + NbBundle.getMessage(
                                    Utils.class, "TXT_BrokenPlatformFmt", (String)value);
            }
            else {
                name = "";
            }            
            return delegate.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
        }
        
    }

}

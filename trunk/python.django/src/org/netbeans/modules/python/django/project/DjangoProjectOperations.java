/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoProjectOperations implements DeleteOperationImplementation, CopyOperationImplementation,
        MoveOperationImplementation {

    private final DjangoProject project;

    public DjangoProjectOperations(final DjangoProject project) {
        assert project != null;
        this.project = project;
    }

    public void notifyDeleted() throws IOException {
        project.getHelper().notifyDeleted();
    }

    public void notifyDeleting() throws IOException {
    }


    public void notifyCopied(Project originalProject, File file, String newName) throws IOException {
        if (originalProject == null) {
            // do nothing for the original project.
            return;
        }
        project.setName(newName);
    }

    public void notifyCopying() throws IOException {
    }

    public void notifyMoved(Project originalProject, File file, String newName) throws IOException {
        if (originalProject == null) {
            project.getHelper().notifyDeleted();
            return;
        }
        project.setName(newName);
    }

    public void notifyMoving() throws IOException {
    }

    public List<FileObject> getDataFiles() {
        final FileObject[] srcRoots = project.getDjangoSourceRoots().getRoots();
       // final FileObject[] testRoots = project.getTestRoots().getRoots();
        final List<FileObject> result = new ArrayList<FileObject>(srcRoots.length);
        result.addAll(Arrays.asList(srcRoots));
     //   result.addAll(Arrays.asList(testRoots));
        return result;
    }

    public List<FileObject> getMetadataFiles() {
        List<FileObject> files = new ArrayList<FileObject>(1);        
        FileObject nbProject = project.getHelper().getProjectDirectory().getFileObject("nbproject"); // NOI18N
        if (nbProject != null) {
            files.add(nbProject);
        }
        return files;
    }

}

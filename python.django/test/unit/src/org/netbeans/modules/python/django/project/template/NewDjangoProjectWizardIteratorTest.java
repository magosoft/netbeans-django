/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.template;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.python.django.project.DjangoProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author alberto
 */
public class NewDjangoProjectWizardIteratorTest {

    public NewDjangoProjectWizardIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createApplication method, of class NewDjangoProjectWizardIterator.
     */
    @Test
    public void testCreateApplication() {
        System.out.println("createApplication");
        NewDjangoProjectWizardIterator expResult = null;
        NewDjangoProjectWizardIterator wizardIterator = NewDjangoProjectWizardIterator.createApplication();
        String projectName = "djtest";
        File projectDirectory = new File("/home/alberto/tmp/djtest/");
        System.out.println(projectDirectory);
        FileObject projectFO= FileUtil.toFileObject(projectDirectory);
        try {
            AntProjectHelper helper = ProjectGenerator.createProject(projectFO, DjangoProjectType.TYPE);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

//    /**
//     * Test of createExistingProject method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testCreateExistingProject() {
//        System.out.println("createExistingProject");
//        NewDjangoProjectWizardIterator expResult = null;
//        NewDjangoProjectWizardIterator result = NewDjangoProjectWizardIterator.createExistingProject();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initialize method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testInitialize() {
//        System.out.println("initialize");
//        WizardDescriptor wizard = null;
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.initialize(wizard);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of uninitialize method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testUninitialize() {
//        System.out.println("uninitialize");
//        WizardDescriptor wizard = null;
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.uninitialize(wizard);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of instantiate method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testInstantiate_0args() throws Exception {
//        System.out.println("instantiate");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        Set expResult = null;
//        Set result = instance.instantiate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of instantiate method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testInstantiate_ProgressHandle() throws Exception {
//        System.out.println("instantiate");
//        ProgressHandle handle = null;
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        Set expResult = null;
//        Set result = instance.instantiate(handle);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of name method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testName() {
//        System.out.println("name");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        String expResult = "";
//        String result = instance.name();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasNext method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testHasNext() {
//        System.out.println("hasNext");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        boolean expResult = false;
//        boolean result = instance.hasNext();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasPrevious method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testHasPrevious() {
//        System.out.println("hasPrevious");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        boolean expResult = false;
//        boolean result = instance.hasPrevious();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of nextPanel method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testNextPanel() {
//        System.out.println("nextPanel");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.nextPanel();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of previousPanel method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testPreviousPanel() {
//        System.out.println("previousPanel");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.previousPanel();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of current method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testCurrent() {
//        System.out.println("current");
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        Panel expResult = null;
//        Panel result = instance.current();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addChangeListener method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testAddChangeListener() {
//        System.out.println("addChangeListener");
//        ChangeListener l = null;
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.addChangeListener(l);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeChangeListener method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testRemoveChangeListener() {
//        System.out.println("removeChangeListener");
//        ChangeListener l = null;
//        NewDjangoProjectWizardIterator instance = new NewDjangoProjectWizardIterator();
//        instance.removeChangeListener(l);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFreeFolderName method, of class NewDjangoProjectWizardIterator.
//     */
//    @Test
//    public void testGetFreeFolderName() {
//        System.out.println("getFreeFolderName");
//        File owner = null;
//        String proposal = "";
//        String expResult = "";
//        String result = NewDjangoProjectWizardIterator.getFreeFolderName(owner, proposal);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
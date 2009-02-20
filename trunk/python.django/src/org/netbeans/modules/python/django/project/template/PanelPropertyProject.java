/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.template;

import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Ravi Hingarajiya
 */
public class PanelPropertyProject implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel{
    
    private WizardDescriptor wizardDescriptor;
    private PanelPropertyProjectVisual component;
    private final NewDjangoProjectWizardIterator.WizardType type;
    private final String[] steps;

    public PanelPropertyProject(final NewDjangoProjectWizardIterator.WizardType type, String[] steps) {
        assert type != null;
        assert steps != null;
        this.type = type;
        this.steps = steps;
    }
    
    public PanelPropertyProjectVisual getComponent() {
         if (component == null) {
            component = new PanelPropertyProjectVisual(this, type);            
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            
        }
        return component;
    }

    public HelpCtx getHelp() {
        return new HelpCtx(PanelPropertyProject.class);
    }

    public void readSettings(Object settings) {
       wizardDescriptor = (WizardDescriptor) settings;
       getComponent().read(wizardDescriptor);  
    }
    public void storeSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        getComponent().store(wizardDescriptor);
    } 

    public boolean isValid() {
        getComponent();
        return getComponent().valid(wizardDescriptor);
    }
    
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    
    public void addChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }    
        
    }

    public void validate() throws WizardValidationException {
        getComponent();
        getComponent().validate(wizardDescriptor);
    }

    public boolean isFinishPanel() {
        return true;
    }

    

}

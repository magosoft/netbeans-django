/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project.template;

import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

/**
 *
 * @author Tomas Zezula
 * @author Vasant Patel
 */
public class PanelConfigureSources implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel{

    private PanelConfigureSourcesVisual component;
    private WizardDescriptor wizardDescriptor;
    private final ChangeSupport changeSupport;
    private final NewDjangoProjectWizardIterator.WizardType type;
    private final String[] steps;

    public PanelConfigureSources (final NewDjangoProjectWizardIterator.WizardType type, final String[] steps) {
        assert type != null;
        assert steps != null;
        this.type = type;
        this.steps = steps;
        this.changeSupport = new ChangeSupport(this);
    }

    public PanelConfigureSourcesVisual getComponent() {
        if (component == null) {
            component = new PanelConfigureSourcesVisual ();
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 1);
        }
        return component;
    }

    public HelpCtx getHelp() {
        return new HelpCtx(PanelConfigureSources.class);
    }

    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        getComponent().read(wizardDescriptor);
    }

    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        getComponent().store(d);
    }

    public boolean isValid() {
        return getComponent().valid(wizardDescriptor);
    }

    public void addChangeListener(ChangeListener l) {
        assert l != null;
        this.changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        assert l != null;
        this.changeSupport.removeChangeListener(l);
    }

    public void validate() throws WizardValidationException {
        getComponent().validate(wizardDescriptor);
    }

    public boolean isFinishPanel() {
        return true;
    }

}

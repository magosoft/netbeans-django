/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.django.project;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.python.django.project.ui.actions.Command;
import org.netbeans.modules.python.django.project.ui.actions.CopyCommand;
import org.netbeans.modules.python.django.project.ui.actions.DjangoSqlCommand;
import org.netbeans.modules.python.django.project.ui.actions.DeleteCommand;
import org.netbeans.modules.python.django.project.ui.actions.MoveCommand;
import org.netbeans.modules.python.django.project.ui.actions.RenameCommand;
import org.netbeans.modules.python.django.project.ui.actions.RunCommand;
import org.netbeans.modules.python.django.project.ui.actions.RunShellCommand;
import org.netbeans.modules.python.django.project.ui.actions.RunSyncdbCommand;
import org.netbeans.modules.python.django.project.ui.actions.RunValidateCommand;
import org.netbeans.modules.python.django.project.ui.actions.DjangoAdminTemplate;
import org.netbeans.modules.python.django.project.ui.actions.DjangoCommand;
import org.netbeans.modules.python.django.project.ui.actions.DjangoTestCommand;
import org.netbeans.spi.project.ActionProvider;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Ravi Hingarajiya
 */
public class DjangoActionProvider implements ActionProvider  {
    
    public static final String DJANGO_SQL_COMMAND = "djangosqlcommand";
    public static String COMMAND_SYNCDB = "syncdbcommand";
    public static String COMMAND_SHELL = "shellcommand";
    public static String COMMAND_VALIDATE = "validatecommand";
    public static String ADMIN_TEMPLATE = "admintemplate";
    public static String DJANGO_COMMAND = "djangocommand";
    public static String DJANGO_TEST_COMMAND ="djangotestcommand";
    
   

    private final Map<String,Command> commands;
    
    
    public DjangoActionProvider(DjangoProject project){
        
        assert project != null;

        commands = new LinkedHashMap<String, Command>();
        Command[] commandArray = new Command[] {
            new DeleteCommand(project),
            new CopyCommand(project),
            new MoveCommand(project),
            new RenameCommand(project),
          //  new RunSingleCommand(project),
            new DjangoSqlCommand(project),
            new DjangoCommand(project),
            new DjangoTestCommand(project),
            new RunCommand(project),
            new RunSyncdbCommand(project),
            new RunShellCommand(project),
            new RunValidateCommand(project),
            new DjangoAdminTemplate(project),
        };
        for (Command command : commandArray) {
            commands.put(command.getCommandId(), command);
        }
    }
    public String[] getSupportedActions() {
        final Set<String> names = commands.keySet();
        return names.toArray(new String[names.size()]);
    }

    public void invokeAction(String commandName,final Lookup context) throws IllegalArgumentException {
        
        final Command command = findCommand(commandName);
        assert command != null;
        if (command.saveRequired()) {
            LifecycleManager.getDefault().saveAll();
        }
        if (!command.asyncCallRequired()) {
            command.invokeAction(context);
        } else {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    command.invokeAction(context);
                }
            });
        }

        
    }

    public boolean isActionEnabled(String commandName, Lookup context) throws IllegalArgumentException {
        final Command command = findCommand (commandName);
        assert command != null;
        return command.isActionEnabled(context);
    }
    private Command findCommand (final String commandName) {
        assert commandName != null;
        return commands.get(commandName);
    }



}

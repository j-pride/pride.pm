package util;

import java.util.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Base class for a simplified construction of command line clients.
 * Interactive clients are more fun when discovering a new tool, so
 * there are a few of these in the PriDE examples :-)
 */
public abstract class AbstractCommandLineClient {
	List<Command> commands = new ArrayList<Command>();

    public void work() throws Exception {
    	help();
        BufferedReader input = new BufferedReader(new InputStreamReader( System.in ) );
        while (true) {
	        String cmd_ln = input.readLine();
	        if (cmd_ln == null)
	        	return;
	        String[] str = cmd_ln.split("[\\s]+");
	        if (str.length == 0 || str[0].length() == 0 || str[0].startsWith("?")) {
	        	help();
	        }
	        else {
		        try {
			        if (str[0].equals("exit")) {
			        	System.out.println("bye");
			        	return;
			        }
		        	executeCmd (str);
		        }
		        catch(InvocationTargetException itx) {
		        	System.err.println(itx.getTargetException().getMessage());
		        }
		        catch(IllegalArgumentException iax) {
		        	System.err.println(iax.getMessage());
		        	System.err.flush();
		        	Thread.sleep(100); // Let standard err to be flushed to the screen before displaying the help
		        	help();
		        }
	        }
        }
    }

	private void help() {
		System.out.println("?  -  help");
		System.out.println("exit  -  exit the client");
		for (Command command: commands) {
			System.out.println(command);
		}
	}

	protected void executeCmd(String[] cmd) throws Exception {
		for (Command command: commands) {
			if (command.method.getName().equals(cmd[0])) {
				command.execute(this, cmd);
				return;
			}
		}
		throw new IllegalArgumentException("Unknown command '" + cmd[0] + "'");
	}
	
	public AbstractCommandLineClient registerCommand(String help, String methodName, String... parameterNames) {
		for (Method method: getClass().getMethods()) {
			if (method.getName().equals(methodName) &&
				 method.getParameterTypes().length == parameterNames.length) {
				commands.add(new Command(method, help, parameterNames));
				return this;
			}
		}
		throw new IllegalArgumentException("No such command method: " + methodName);
	}
	
	private static class Command {
		Method method;
		String help;
		String[] parameterNames;
		
		public Command(Method method, String help, String[] parameterNames) {
			this.method = method;
			this.help = help;
			this.parameterNames = parameterNames;
		}

		public void execute(AbstractCommandLineClient client, String[] cmd) throws Exception {
			 if (method.getParameterTypes().length != cmd.length-1) {
				 throw new IllegalArgumentException("Wrong number of arguments");
			 }

			Object[] args = new Object[cmd.length-1];
			Class<?> argTypes[] = method.getParameterTypes();
			for (int i = 0; i < args.length; i++) {
				args[i] = parseCmdParameter(cmd[i+1], argTypes[i]);
			}
			method.invoke(client, args);
		}

		private Object parseCmdParameter(String input, Class<?> targetType) {
			try {
				if (targetType == String.class) {
					return input;
				}
				if (targetType == int.class || targetType == Integer.class) {
					return Integer.parseInt(input);
				}
			}
			catch(Exception x) {}
			throw new IllegalArgumentException("Can't convert '" + input + "' to " + targetType.getSimpleName());
		}
		
		public String toString() {
			String result = method.getName();
			for (String parameterName: parameterNames) {
				result += "  <" + parameterName + ">";
			}
			return result + "  -  " + help;
		}
	}
}


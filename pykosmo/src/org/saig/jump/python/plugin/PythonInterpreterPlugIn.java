package org.saig.jump.python.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;
import org.saig.jump.python.util.PythonInterpreterManager;

import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.util.Blackboard;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedPlugIn;
import com.vividsolutions.jump.workbench.ui.plugin.PersistentBlackboardPlugIn;

/**
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * */
public class PythonInterpreterPlugIn extends AbstractPlugIn implements ThreadedPlugIn {
	
	private static final Logger LOGGER = Logger.getLogger(PythonInterpreterPlugIn.class);
	
	private static final String NAME = "Intérprete Python";
	private static final String BB_KEY = "PYTHON_SCRIPT_LAST_DIRECTORY";
	protected static String LAST_SCRIPT_EXECUTED;
	protected File pyFile;
	
	private static final JFileChooser chooser = new JFileChooser();
	
	{
		chooser.setFileFilter(
				new FileNameExtensionFilter("Script Python", "py"));
	}
	
	private RepeatPythonScriptPlugIn repeatPythonScriptPlugIn;
	
	public void setRepeatPythonScriptPlugIn(RepeatPythonScriptPlugIn repeatPythonScriptPlugIn){
		this.repeatPythonScriptPlugIn = repeatPythonScriptPlugIn;
	}

	private static final String[] menuPath = new String[]{"Herramientas"};
	
	@Override
	public void run(TaskMonitor monitor, PlugInContext context)	throws Exception {
		if(pyFile == null){
			return;
		}
		
		PythonInterpreterManager manager = PythonInterpreterManager.getInstance();
        
        manager.executeScript(pyFile);
		
			
		LOGGER.info("El script python " + pyFile.getName() + " terminó su ejecución");
		
//		PyObject x = interp.get("x");
//		System.err.println("x: " + (x != null ? x.toString() : "null"));
		
	}
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		
		Blackboard bb = PersistentBlackboardPlugIn.get(context.getWorkbenchContext());
		
		String dirPath = (String)bb.get(BB_KEY);
		if(dirPath != null){
			File dir = new File(dirPath);
			if(dir.exists()){
				chooser.setCurrentDirectory(dir);
			}
		}
		
		int returnVal = chooser.showOpenDialog(JUMPWorkbench.getFrameInstance());
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			
			pyFile = chooser.getSelectedFile();
			LAST_SCRIPT_EXECUTED = pyFile.getAbsolutePath();
			
			updateRepeatPlugInName(context);
			
			bb.put(BB_KEY, pyFile.getAbsolutePath());
			
			return true;
		}
		
		return false;
	}

	private void updateRepeatPlugInName(PlugInContext context) {
		JButton button = (JButton) context.getWorkbenchFrame().getToolBar().getPlugInButton(RepeatPythonScriptPlugIn.class);
		
		if(button != null && repeatPythonScriptPlugIn != null){
			button.setName(repeatPythonScriptPlugIn.getName());
			//TODO No entiendo por qué tengo que hacer un setName para que se actualice el tooltip;
			//tampoco sé si hacerlo es peligroso (¿el nombre del botón se usa para algo?)
			button.setToolTipText(repeatPythonScriptPlugIn.getName());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
    public EnableCheck getCheck() {
        return createEnableCheck(JUMPWorkbench.getFrameInstance().getContext());
    }
    
    public static EnableCheck createEnableCheck( WorkbenchContext workbenchContext ) {
        MultiEnableCheck solucion = new MultiEnableCheck();
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        solucion.add(checkFactory.createTaskWindowMustBeActiveCheck());
        
        return solucion;
    }
    
	@Override
	public void initialize(PlugInContext context) throws Exception {
		context.getFeatureInstaller().addMainMenuItem(this, menuPath, false, true);
	}

	@Override
	public void finish(PlugInContext context) {
		context.getFeatureInstaller().removeMainMenuItem(this, menuPath, this.getName());	
	}
}
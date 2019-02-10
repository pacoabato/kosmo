package org.saig.jump.python.plugin;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;

/**
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * */
public class RepeatPythonScriptPlugIn extends PythonInterpreterPlugIn{
	
	private static final Logger LOGGER = Logger.getLogger(RepeatPythonScriptPlugIn.class);
	
	private static final String NAME = "Repetir script";
	private static final Icon ICON = IconLoader.icon("arrow_refresh.png");

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		pyFile = new File(LAST_SCRIPT_EXECUTED);
		
		if(pyFile == null || !pyFile.canRead()){
			return false;
		}
		
		LOGGER.info("Repitiendo la ejecución del script python: " + pyFile.getName());
		return true;
	}

	@Override
	public EnableCheck getCheck() {
		return createEnableCheck(JUMPWorkbench.getFrameInstance().getContext());
	}

	public static EnableCheck createEnableCheck(WorkbenchContext context) {
        return new MultiEnableCheck()
        	.add(new EnableCheck(){
				@Override
				public String check(JComponent component) {
					return LAST_SCRIPT_EXECUTED == null ?
							"Aún no ha ejecutado ningún script de python" :
							null;
				}
        	});
	}

	@Override
	public Icon getIcon() {
		return ICON;
	}

	@Override
	public String getName() {
		String lastScriptFullName = LAST_SCRIPT_EXECUTED;
		
		if(StringUtils.isEmpty(lastScriptFullName)){
			return NAME;
		}
		
		File file = new File(lastScriptFullName);
		
		return NAME + ": " + file.getName();
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		context.getWorkbenchFrame().getToolBar().addPlugIn(this, context.getWorkbenchContext());
	}

	@Override
	public void finish(PlugInContext context) {
		context.getWorkbenchFrame().getToolBar().removePlugIn(this);
	}
}
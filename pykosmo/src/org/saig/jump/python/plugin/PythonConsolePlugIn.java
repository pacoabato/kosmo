package org.saig.jump.python.plugin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;
import org.saig.jump.python.widgets.PythonConsoleController;
import org.saig.jump.python.widgets.PythonConsoleFrame;

import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedPlugIn;
import com.vividsolutions.jump.workbench.ui.GUIUtil;

public class PythonConsolePlugIn extends AbstractPlugIn implements ThreadedPlugIn {
	
	private static final Logger LOGGER = Logger.getLogger(PythonConsolePlugIn.class);
	
	private static final String NAME = "Consola Python";
	
	private static final String[] menuPath = new String[]{"Herramientas"};
	
	@Override
	public void run(TaskMonitor monitor, PlugInContext context)	throws Exception {
		//TODO quitar este método
	}
	
	@Override
	public boolean execute(PlugInContext context) throws Exception {
	    
//	    PythonConsoleFrame frame = new PythonConsoleFrame();
//        frame.pack();
//        context.getWorkbenchFrame().addInternalFrame(frame);
//        context.getWorkbenchFrame().getDesktopPane().setLayer(frame, 100);
//        GUIUtil.centreOnWindow(frame);
	    
	    PythonConsoleController consoleController = new PythonConsoleController(context);
	    
	    
	    
		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		context.getFeatureInstaller().addMainMenuItem(this, menuPath, false, true);
	}

	@Override
	public void finish(PlugInContext context) {
		context.getFeatureInstaller().removeMainMenuItem(this, menuPath, this.getName());	
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

}
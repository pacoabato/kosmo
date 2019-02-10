package org.saig.jump.python.plugin;

import javax.swing.Icon;

import org.apache.log4j.Logger;

import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

/**
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 */
public class PythonInterpreterExtension extends Extension implements PlugIn{
	
	private final static Logger LOGGER = Logger.getLogger(PythonInterpreterExtension.class);
	
	private final static String NAME = "Int�rprete Python";
	
	private final static String VERSION = "0.4 (20100722)";
	
	private final static String DESCRIPTION =
		"Extensi�n que permite la ejecuci�n de c�digo Python mediante la librer�a Jython.\n"
		+ "v.0.2: A�adido plugin Repetir �ltimo script\n"
		+ "v.0.3: Primera API de Kosmo\n"
		+ "v.0.3.4: Jython 2.5.1 y API aumentada (GenericPlugInActivator)\n"
		+ "v.0.4: C�digo python estructurado en m�dulos\n"
		+ "v.0.4.1: Consola"
		;
	
	PythonInterpreterPlugIn pythonPlugin;
	PythonConsolePlugIn pythonConsolePlugIn;
	RepeatPythonScriptPlugIn repeatPythonScriptPlugIn;
	CustomPlugIn customPlugIn;
	
	@Override
	public String getName(){
		return NAME;
	}
	
	@Override
	public String getVersion(){
		return VERSION;
	}
	
	@Override
	public String getDescription(){
		return DESCRIPTION;
	}
	
	@Override
	public void install(PlugInContext context) throws Exception {
		initTools(context);
		LOGGER.info("Instalando la extensi�n: " + getName());
	}

	@Override
	public void uninstall(PlugInContext context) throws Exception {
		if(pythonPlugin != null){
			pythonPlugin.finish(context);
			repeatPythonScriptPlugIn.finish(context);
			customPlugIn.finish(context);
		}
		LOGGER.info("Desinstalando la extensi�n: " + getName());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void finish(PlugInContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EnableCheck getCheck() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getDisabledIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		initTools(context);
		LOGGER.info("Instalando la extensi�n: " + getName());		
	}
	
	private void initTools(PlugInContext context) throws Exception{
	    if(pythonPlugin == null){
            pythonPlugin= new PythonInterpreterPlugIn();
            repeatPythonScriptPlugIn = new RepeatPythonScriptPlugIn();
            pythonPlugin.setRepeatPythonScriptPlugIn(repeatPythonScriptPlugIn);
            customPlugIn = new CustomPlugIn();
            pythonConsolePlugIn = new PythonConsolePlugIn();
        }
        
        pythonPlugin.initialize(context);
        repeatPythonScriptPlugIn.initialize(context);
        customPlugIn.initialize(context);
        pythonConsolePlugIn.initialize(context);
	}
}
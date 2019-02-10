/* 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, contact:
 * 
 * Francisco Abato Helguera
 * pacoabato@gmail.com
 *
 */

package com.pacoabato.kosmo.extension;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.saig.jump.lang.I18N;
import org.saig.jump.plugin.config.ConfigPlugIn;
import org.saig.jump.widgets.config.ConfigDialog;

import com.pacoabato.kosmo.config.MinimapConfigPanel;
import com.pacoabato.kosmo.kminimap.MinimapPlugIn;
import com.pacoabato.kosmo.magnifyingglass.MagnifyingGlassTool;
import com.pacoabato.kosmo.whereisthis.WhereIsThisPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;


/**
 * Extensión que carga el minimapa de navegación.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MinimapExtension extends Extension implements PlugIn{
    
    private final static Logger LOGGER = Logger.getLogger(MinimapExtension.class);
    
    private final static String NAME = I18N.getString(MinimapExtension.class, "minimap-navigation"); //$NON-NLS-1$
    
    private final static String VERSION = "2.1 (20130620)"; //$NON-NLS-1$
    
    private final static String DESCRIPTION = I18N.getString(
            MinimapExtension.class,
            "includes-the-tools-navigation-minimap-magnifying-glass-and-where-is-this"); //$NON-NLS-1$
    
    private MinimapPlugIn minimapPlugin;
    private MagnifyingGlassTool magnifyingGlassTool;
    private WhereIsThisPlugIn whereIsThisPlugIn;
    
    private MinimapConfigPanel configPanel;
    
    private static final String OPTIONS_PANEL_PATH = ConfigDialog.OTHERS_MAIN_CATEGORY_NAME;
    
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
    	// se usa cuando se carga como extensión 
        LOGGER.info(
                I18N.getString(getClass(), "installing-extension") //$NON-NLS-1$
                + getName());
        
        initTools(context);
        addConfigPanel(context);
    }

	@Override
    public void uninstall(PlugInContext context) throws Exception {
		LOGGER.info(
		        I18N.getString(getClass(), "uninstalling-extension") //$NON-NLS-1$
		        + getName());
		
        if(minimapPlugin != null){
            minimapPlugin.finish(context);
        }
        
        if(magnifyingGlassTool != null){
            context.getWorkbenchFrame().getToolBar().removeCursorTool(magnifyingGlassTool);
        }
        
        if(whereIsThisPlugIn != null){
            whereIsThisPlugIn.finish(context);
        }
        
        removeConfigPanel();
    }
	
	@Override
    public boolean execute(PlugInContext context) throws Exception {
    	// nothing to do, MinimapExtension as a plugin only loads the MinimapPlugin
        return false;
    }

    @Override
    public void finish(PlugInContext context) {
    	// nothing to do, MinimapExtension as a plugin only loads the MinimapPlugin
    }

    @Override
    public EnableCheck getCheck() {
    	// nothing to do, MinimapExtension as a plugin only loads the MinimapPlugin
        return null;
    }

    @Override
    public Icon getDisabledIcon() {
    	// nothing to do, MinimapExtension as a plugin only loads the MinimapPlugin
        return null;
    }

    @Override
    public Icon getIcon() {
    	// nothing to do, MinimapExtension as a plugin only loads the MinimapPlugin
        return null;
    }

    @Override
    public void initialize(PlugInContext context) throws Exception {
    	// se usa cuando se ejecuta como plugin
        LOGGER.info(
                I18N.getString(getClass(), "installing-extension") //$NON-NLS-1$
                + getName());
        
        initTools(context);
        addConfigPanel(context);
    }
    
    private void initTools(PlugInContext context) throws Exception{
        if(minimapPlugin == null){
            minimapPlugin = new MinimapPlugIn();
        }
        minimapPlugin.initialize(context);
        
        if(magnifyingGlassTool == null){
            magnifyingGlassTool = new MagnifyingGlassTool(context);
        }
        context.getWorkbenchFrame().getToolBar().addCursorTool(
                "Lupa",
                magnifyingGlassTool,
                MagnifyingGlassTool.createEnableCheck(context.getWorkbenchContext()));
        
        if(whereIsThisPlugIn == null){
            whereIsThisPlugIn = new WhereIsThisPlugIn();
        }
        whereIsThisPlugIn.initialize(context);
    }
    
    private void addConfigPanel(PlugInContext context) {
		if (configPanel == null) {
			configPanel = new MinimapConfigPanel(context);
		}
		
		ConfigPlugIn.getDialog().addConfigPanel(
				configPanel,
				OPTIONS_PANEL_PATH,
				configPanel.getName());
	}
    
    private void removeConfigPanel() {
    	if (configPanel != null) {
        	ConfigPlugIn.getDialog().removeConfigPanel(configPanel, OPTIONS_PANEL_PATH);
    	}
	}
}
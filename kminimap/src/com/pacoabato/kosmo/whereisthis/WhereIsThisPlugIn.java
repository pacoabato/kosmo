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
package com.pacoabato.kosmo.whereisthis;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.saig.jump.lang.I18N;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;

/**
 * Plugin para mostrar una vista del mundo completo indicando
 * dónde se encuentra la vista actual.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class WhereIsThisPlugIn extends AbstractPlugIn {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(WhereIsThisPlugIn.class);
    
    private static final String NAME = I18N.getString(WhereIsThisPlugIn.class, "where-is-this"); // $NON-NLS-1$
    private static final Icon ICON = IconLoader.icon("Magnify.gif"); // $NON-NLS-1$
    
    private WhereIsThisController controller;
    
    public WhereIsThisPlugIn() {
        super();
    }
    
    @Override
    public boolean execute( PlugInContext context ) throws Exception {
        reportNothingToUndoYet(context);
        
        if (controller == null) {
            controller = new WhereIsThisController(context);
        }
        
        // Recuperamos el layerViewPanel del que se va a mostrar la vista ampliada
        LayerViewPanel lvp = JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel(); 
        
        if (lvp != null) {
            controller.magnify(lvp);
        }
        
        return true;
    }
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
    
    @Override
    public void initialize(PlugInContext context) throws Exception {
        context.getWorkbenchFrame().getToolBar().addPlugIn(this, JUMPWorkbench.getFrameInstance().getContext());
    }

    @Override
    public void finish(PlugInContext context) {
        context.getWorkbenchFrame().getToolBar().removePlugIn(this);
    }
    
    @Override
    public EnableCheck getCheck() {
        return WhereIsThisPlugIn.createEnableCheck(JUMPWorkbench.getFrameInstance().getContext());
    }
    
    public static EnableCheck createEnableCheck( WorkbenchContext workbenchContext ) {
        MultiEnableCheck solucion = new MultiEnableCheck();
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        solucion.add(checkFactory.createTaskWindowMustBeActiveCheck());
        
        return solucion;
    }
}
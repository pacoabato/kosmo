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
package com.pacoabato.kosmo.magnifyingglass;

import java.awt.Cursor;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.saig.jump.lang.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.cursortool.NClickTool;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;

/**
 * Plugin para mostrar una vista ampliada del mundo.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MagnifyingGlassTool extends NClickTool {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(MagnifyingGlassTool.class);
    
    private static final String NAME = I18N.getString(MagnifyingGlassTool.class, "magnifying-glass"); // $NON-NLS-1$
    private static final Icon ICON = IconLoader.icon("Magnify.gif"); // $NON-NLS-1$
    private static final ImageIcon CURSOR = IconLoader.icon("MagnifyCursor.gif"); // $NON-NLS-1$
    
    
    private MagnifyingGlassController controller;
    
    public MagnifyingGlassTool(PlugInContext context) {
        super(1);
    }
    
    @Override
    protected void gestureFinished() throws Exception {
        reportNothingToUndoYet();
        
        WorkbenchContext context = JUMPWorkbench.getFrameInstance().getContext();
        PlugInContext plugInContext = context.createPlugInContext();
        
        
        if (controller == null) {
            controller = new MagnifyingGlassController();
        }
        
        // Recuperamos el layerViewPanel del que se va a mostrar la vista ampliada
        LayerViewPanel lvp = context.getLastClickedLayerViewPanel();
        // TODO ¿usar esto o JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel()? 
        // mejor como está, porque si hay dos frames en pantalla y se clica en el que no 
        // está seleccionado la otra opción fallaría
        
        
        if (lvp != null ) {
//            Point clickedPoint = viewPanel.getLastClickedPoint();
//            Coordinate clickedPointInModelCoordinates = viewPanel.getViewport().toModelCoordinate(clickedPoint);
            
            Coordinate clickedPoint = getModelSource();
            controller.magnify(lvp, plugInContext, clickedPoint);
        }
    }
    
    /**
     * Sobreescribe el método original para que no sea necesario que haya 
     * capas editables.
     */
    @Override
    protected void add( Coordinate c ) {
        if (!activate) {
            return;
        }
        coordinates.add(c);
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
    public Cursor getCursor() {                             
        return createCursor(CURSOR.getImage());
    }
    
    public static EnableCheck createEnableCheck( WorkbenchContext workbenchContext ) {
        MultiEnableCheck solucion = new MultiEnableCheck();
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        solucion.add(checkFactory.createTaskWindowMustBeActiveCheck());
        
        return solucion;
    }
}

/* 
 * Kosmo - Sistema Abierto de Información Geográfica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2010, SAIG S.L.
 *
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
 * Sistemas Abiertos de Información Geográfica, S.L.
 * Avnda. República Argentina, 28
 * Edificio Domocenter Planta 2ª Oficina 7
 * C.P.: 41930 - Bormujos (Sevilla)
 * España / Spain
 *
 * Teléfono / Phone Number
 * +34 954 788876
 * 
 * Correo electrónico / Email
 * info@saig.es
 *
 */
package org.saig.jump.python.widgets;

import javax.swing.JInternalFrame;

import org.apache.log4j.Logger;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.model.LayerManagerProxy;
import com.vividsolutions.jump.workbench.model.Task;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerNamePanel;
import com.vividsolutions.jump.workbench.ui.LayerNamePanelProxy;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelProxy;
import com.vividsolutions.jump.workbench.ui.SelectionManager;
import com.vividsolutions.jump.workbench.ui.SelectionManagerProxy;
import com.vividsolutions.jump.workbench.ui.TaskFrame;

/**
 * Ventana para la consola Python. 
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class PythonConsoleFrame extends TaskFrame //JInternalFrame 
    implements LayerViewPanelProxy, LayerNamePanelProxy, LayerManagerProxy, SelectionManagerProxy{
    
    private static final Logger LOGGER = Logger.getLogger(PythonConsoleFrame.class);
    
    private PythonConsolePanel mainPanel;
    
    private LayerViewPanel layerViewPanel;
    private LayerNamePanel layerNamePanel;
    private LayerManager layerManager;
    private SelectionManager selectionManager;
    
    public PythonConsoleFrame(PlugInContext context){
//        super("PyKosmo console", false, true, false, false);
        super(context.getTask(), context.getWorkbenchContext());
        //Otra forma es guardar en una variable python el nombre de la vista asociada a la consola de python y los métodos
        //get_selection_manager(), get_view_panel(), etc, de la API que hagan lo siguiente:
        //JUMPWorkbench.getFrameInstance().getContext().getTaskManager().getTask("taskname")
        
        setTitle("PyKosmo console");
        setResizable(false);
        setClosable(true);
        setMaximizable(false);
        setIconifiable(false);
        
        layerViewPanel = context.getLayerViewPanel();
        layerNamePanel = context.getLayerNamePanel();
        layerManager = context.getLayerManager();
        selectionManager = layerViewPanel.getSelectionManager();
        
        mainPanel = new PythonConsolePanel();
        setContentPane(mainPanel);
    }
    
    public PythonConsolePanel getConsolePanel(){
        return mainPanel;
    }

    @Override
    public LayerViewPanel getLayerViewPanel() {
        return layerViewPanel;
    }

    @Override
    public LayerNamePanel getLayerNamePanel() {
        return layerNamePanel;
    }

    @Override
    public LayerManager getLayerManager() {
        return layerManager;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}

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
 * 
 * Correo electrónico / Email
 * pacoabato@gmail.com
 *
 */
package com.pacoabato.kosmo.kminimap;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.log4j.Logger;
import org.saig.jump.lang.I18N;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.MainMenuNames;

/**
 * Plugin que se muestra en la interfaz de usuario para que se
 * ejecute el minimapa de navegación.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MinimapPlugIn extends AbstractPlugIn{
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(MinimapPlugIn.class);
    
    private static final String NAME = I18N.getString(MinimapPlugIn.class, "minimap"); //$NON-NLS-1$
    
    private static final String[] menuPath = new String[]{MainMenuNames.VIEW};
    
    private MinimapController controller;
    
    private JInternalFrame minimapFrame; 
    
    @Override
    public boolean execute(PlugInContext context) throws Exception {
        if(controller == null){
            controller = new MinimapController(context);
            minimapFrame = createMinimapFrame();
            minimapFrame.pack();
            GUIUtil.centre(minimapFrame, JUMPWorkbench.getFrameInstance());
            
            minimapFrame.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    super.internalFrameClosed(e);
                    controller.setIgnore(true);
                    // don't want the minimap to work if the frame is closed
                }
            });
        }
        
        controller.setIgnore(false);
        
        LayerViewPanel viewPanel = context.getLayerViewPanel();
        controller.setLayerViewPanel(viewPanel);
        
        controller.regenerateMinimap();
        
//        minimapFrame.addInternalFrameListener(new InternalFrameAdapter() {
//            @Override
//            public void internalFrameClosed(InternalFrameEvent e) {
//                controller.removeListeners();
//            }
//        });
        
        // añade el minimapa a una ventana y la muestra
        context.getWorkbenchFrame().addInternalFrame(minimapFrame);
        context.getWorkbenchFrame().getDesktopPane().setLayer(minimapFrame, 100);
        
        // aquí el mapPanel ha "crecido" cuatro píxel en vertical (dos arriba y
        // dos abajo) porque en #addInternalFrame, #centreOnWindow y #pack
        // se le añaden esos 4 (aunque no de forma acumulativa, sólo uno de
        // los tres añade los 4 píxel).
        
        minimapFrame.setVisible(true);
        
        return true;
    }
    
    /** Crea una ventana (JInternalFrame) para albergar el minimapa.*/
    private JInternalFrame createMinimapFrame(){
        JInternalFrame minimapFrame = new JInternalFrame();
        minimapFrame.setTitle(I18N.getString(getClass(), "minimap")); //$NON-NLS-1$
        minimapFrame.setClosable(true);
        minimapFrame.setIconifiable(false);
        minimapFrame.setMaximizable(false);
        minimapFrame.setResizable(false);
        minimapFrame.setLayout(new BorderLayout());
        minimapFrame.add(controller.getMapPanel(), BorderLayout.CENTER);
        
//        minimapFrame.pack();
        
        return minimapFrame;
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
        context.getFeatureInstaller().removeMainMenuItem(menuPath, this.getName());
    }
    
    @Override
    public EnableCheck getCheck() {
        return createEnableCheck(JUMPWorkbench.getFrameInstance().getContext());
    }
    
    public static EnableCheck createEnableCheck( WorkbenchContext workbenchContext ) {
        MultiEnableCheck solucion = new MultiEnableCheck();
        EnableCheckFactory cf = new EnableCheckFactory(workbenchContext);
        
        EnableCheck[] checks = {
        		cf.createTaskWindowMustBeActiveCheck()
        };
        
        for (EnableCheck check : checks) {
        	solucion.add(check);
        }
        
        return solucion;
    }
}

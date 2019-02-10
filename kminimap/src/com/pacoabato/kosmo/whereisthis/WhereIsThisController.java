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
package com.pacoabato.kosmo.whereisthis;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.saig.jump.lang.I18N;
import org.saig.jump.widgets.util.AbstractWaitDialog;

import com.pacoabato.kosmo.utils.MinimapUtils;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;

/**
 * Controlador para la ventana de WhereIsThisTool.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class WhereIsThisController{
//    private static final Logger LOGGER = Logger.getLogger(WhereIsThisController.class);
    
    private PlugInContext context;
    private WhereIsThisFrame frame;
    
    public WhereIsThisController(PlugInContext context) {
        this.context = context;
        frame = new WhereIsThisFrame();
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
            }
        });
    }
    
    @SuppressWarnings("serial")
    public void magnify(final LayerViewPanel userViewLvp){
        new AbstractWaitDialog(
                JUMPWorkbench.getFrameInstance(),
                I18N.getString(WhereIsThisPlugIn.class, "where-is-this")) { // $NON-NLS-1$
            @Override
            protected void methodToPerform() throws Exception {
                int imgWidth = (int) WhereIsThisFrame.LENS_SIZE.getWidth();
                int imgHeight = (int) WhereIsThisFrame.LENS_SIZE.getHeight();
                
                BufferedImage image = MinimapUtils.getWorldImage(
                        userViewLvp,
                        context,
                        imgWidth,
                        imgHeight);

                frame.getMapPanel().setMap(image);
            }
        }.setVisible(true);
        
        frame.setVisible(true); // if executed inside the waitdialog the frame is shown in background
    }
}

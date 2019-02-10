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
package com.pacoabato.kosmo.magnifyingglass;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.pacoabato.kosmo.utils.MinimapUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;

/**
 * Controlador para la ventana de la lupa.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MagnifyingGlassController {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(MagnifyingGlassController.class);
    
    private MagnifyingGlassFrame frame;
    
    private static final double ZOOM_LEVEL = 0.05; // más aumento cuanto más pequeño
    
    public MagnifyingGlassController() {    
        frame = new MagnifyingGlassFrame();
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
            }
        });
    }
    
    /**
     * @param centerOfLensView el punto sobre el que se va a aumentar la vista (en el que
     * el usuario hizo clic).*/
    public void magnify(LayerViewPanel userViewLvp, PlugInContext context, Coordinate centerOfLensView) {
        Envelope userViewEnv = userViewLvp.getViewport().getEnvelopeInModelCoordinates();
        Envelope newEnv = calculateNewEnvelope(userViewEnv, centerOfLensView);
        
        int imgWidth = (int) MagnifyingGlassFrame.LENS_SIZE.getWidth();
        int imgHeight = (int) MagnifyingGlassFrame.LENS_SIZE.getHeight();
        
        BufferedImage image = new BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_ARGB);
        
		MinimapUtils.copyViewToImage(
        		userViewLvp,
        		context,
        		newEnv,
        		image);
		
        frame.getMapPanel().setMap(image);
        frame.setVisible(true);
    }
    
    /**
     * Calcula un nuevo envelope que representa un zoom de 1/4.
     * @param env Envelope en unidades del mundo de la vista del usuario
     * @param centerOfLensView2 punto en unidades del mundo que será el centro del nuevo
     * envelope
     * @return Envelope en unidades del mundo que enmarca la vista que se
     * mostrará en la lente de aumento.
     */
    private Envelope calculateNewEnvelope( Envelope env, Coordinate centerOfLensView ) {
        double newEnvelopeWidth = env.getWidth() * ZOOM_LEVEL;
        double newEnvelopeHeight = env.getHeight() * ZOOM_LEVEL;
        
        double xmin = centerOfLensView.x - (centerOfLensView.x * ZOOM_LEVEL) + (env.getMinX() * ZOOM_LEVEL);
        double ymin = centerOfLensView.y - (centerOfLensView.y * ZOOM_LEVEL) + (env.getMinY() * ZOOM_LEVEL);

        Envelope newEnvelope = new Envelope(
                xmin, xmin + newEnvelopeWidth,
                ymin, ymin + newEnvelopeHeight);
        
        // modificar el envelope para que sea cuadrado (como el panel donde se dibujará):
        
        if (newEnvelopeWidth > newEnvelopeHeight) {
        	newEnvelope.expandBy(0, (newEnvelopeWidth-newEnvelopeHeight)/2);
        } else if (newEnvelopeHeight > newEnvelopeWidth) {
        	newEnvelope.expandBy((newEnvelopeHeight-newEnvelopeWidth)/2, 0);
        }

        return newEnvelope;
    }
}

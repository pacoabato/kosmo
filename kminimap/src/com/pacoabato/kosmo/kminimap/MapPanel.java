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

package com.pacoabato.kosmo.kminimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.pacoabato.kosmo.extension.ImageHolder;
import com.pacoabato.kosmo.utils.MinimapUtils;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

/** Panel que almacena la imagen del mapa para el minimapa y la información referente
 * a la vista (el rectángulo de la imagen que se corresponde con el mundo (el full extent) y
 * el rectángulo que se corresponde con la porción del mundo que el usuario
 * está visualizando).*/
@SuppressWarnings("serial")
public class MapPanel extends JPanel implements ImageHolder {

    private BufferedImage mapImage = null;
    
    public MapPanel(PlugInContext context) {
    }
    
    void setMap(BufferedImage map) {
    	this.mapImage = map;
        
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (mapImage != null) {
            // dibujar la imagen del mapa
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            Point2D imageOrigin = MinimapUtils.getImageOrigin(this, mapImage);
            
            g.drawImage(
            		mapImage,
            		(int)imageOrigin.getX(),
            		(int)imageOrigin.getY(),
            		mapImage.getWidth(),
            		mapImage.getHeight(),
            		null);
            
            // dibujar la posición de la vista sobre la imagen
            Envelope viewEnvelope = MinimapController.getViewEnvelope();
            Envelope fullViewEnvelope = MinimapController.getFullViewEnvelope();
            
            Envelope env = MinimapUtils.viewEnvelopeToMinimap(this, viewEnvelope, fullViewEnvelope);
            MinimapUtils.drawEnvelope(g, env);
        }
    }
    
    public BufferedImage getImage() {
    	return mapImage;
    }
}
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.pacoabato.kosmo.utils.MinimapUtils;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.ui.GUIUtil;

/**
 * Ventana sin decoradores (botones, bordes, etc.) para mostrar una
 * vista ampliada del mapa de Kosmo.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
@SuppressWarnings("serial")
public class MagnifyingGlassFrame extends JFrame {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(MagnifyingGlassFrame.class);
    private MapPanel mapPanel;
    
    public static final Dimension LENS_SIZE = new Dimension(400, 400);
    
    public MagnifyingGlassFrame() {
        super();
        setDimension();
        setUndecorated(true);
        mapPanel = new MapPanel();
        setContentPane(mapPanel);
        GUIUtil.centre(this, JUMPWorkbench.getFrameInstance());
    }
    
    private void setDimension() {
        // TODO leer la dimensión del kosmo.ini o de un panel de configuración
        setPreferredSize(LENS_SIZE);
        setMinimumSize(LENS_SIZE);
        setMaximumSize(LENS_SIZE);
        setSize(LENS_SIZE);
    }
    
    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    /** Panel que almacena la imagen del mapa para la lupa.*/
    public class MapPanel extends JPanel {
        private BufferedImage mapImage = null;
        
        void setMap(BufferedImage map){
            if (map == null) {
                return;
            }
            
            this.mapImage = map;
            
            this.repaint();
        }
        
        @Override
        public void paint(Graphics g) {
            if (mapImage != null) {
                // dibujar la imagen del mapa
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                Point2D imageOrigin = MinimapUtils.getImageOrigin(this, mapImage);
                
                g.drawImage(
                		mapImage,
                		(int)imageOrigin.getX(),
                		(int)imageOrigin.getY(),
                		mapImage.getWidth()-1,
                		mapImage.getHeight()-1,
                		null);
                
//                g.drawImage(mapImage, 0, 0, null);
                g.setColor(Color.BLACK);
                g.drawRect(
                		0, 0,
                        (int) LENS_SIZE.getWidth()-1,
                        (int) LENS_SIZE.getHeight()-1);
            }
        }
    }
}

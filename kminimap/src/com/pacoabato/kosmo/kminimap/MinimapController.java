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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.pacoabato.kosmo.config.MinimapConfigPanel;
import com.pacoabato.kosmo.utils.MinimapUtils;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.workbench.model.CategoryEvent;
import com.vividsolutions.jump.workbench.model.FeatureEvent;
import com.vividsolutions.jump.workbench.model.LayerEvent;
import com.vividsolutions.jump.workbench.model.LayerListener;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerNamePanelListener;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelListener;
import com.vividsolutions.jump.workbench.ui.Viewport;

/**
 * Contiene la funcionalidad del minimapa de navegación.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MinimapController {
    private static final Logger LOGGER = Logger.getLogger(MinimapController.class);
    
    private static MapPanel mapPanel;
    private static LayerViewPanel userViewLvp;
    
    private PlugInContext context;

    private MinimapMouseListener minimapMouseListener;

    private MinimapLayerViewPanelListener minimapLayerViewPanelListener;

    private MinimapMouseWheelListener minimapMouseWheelListener;

    private MinimapLayerListener minimapLayerListener;
    
    // if true the minimap will not be updated
    private boolean ignore;
    
    public MinimapController(PlugInContext context){
        this.context = context;
        ignore = true;
        
        mapPanel = new MapPanel(context);
        setMapPanelDimension();
        // TODO si se cambia en la configuración el tamaño, no se tiene en cuenta hasta
        // que se reinicia el minimapa
	    
        addListeners();
    }
    
    public void setMapPanelDimension() {
    	int panelWidth = MinimapUtils.getMapPanelWidth();
	    int panelHeight = MinimapUtils.getMapPanelHeight() - 4;
	    // por algún motivo Swing (o vete a saber quién) mete 4 píxel 
	    // de alto (dos arriba y dos abajo) al meter el panel en el internal frame
	    
    	Dimension dim = new Dimension(panelWidth, panelHeight);
        mapPanel.setMinimumSize(dim);
        mapPanel.setMaximumSize(dim);
        mapPanel.setPreferredSize(dim);
        mapPanel.setSize(dim);
    }
    
    /**
     * Calcula la imagen para el minimapa y se la asigna.
     */
    public void regenerateMinimap() {
        if (ignore) {
            return;
        }
//        new AbstractWaitDialog(
//                JUMPWorkbench.getFrameInstance(),
//                I18N.getString(getClass(), "minimap_wait_title")) {
//            @Override
//            protected void methodToPerform() throws Exception {
                int imgWidth = MinimapConfigPanel.getMinimapPanelWidth();
                int imgHeight = MinimapConfigPanel.getMinimapPanelHeight();
                BufferedImage img  = MinimapUtils.getWorldImage(userViewLvp, context, imgWidth, imgHeight);
                setMap(img);
//            }
//        }.setVisible(true);
    }
    
    private void addListeners() {
        minimapMouseListener = new MinimapMouseListener();
        minimapMouseWheelListener = new MinimapMouseWheelListener();
        minimapLayerViewPanelListener = new MinimapLayerViewPanelListener();
        
        addMouseListener(minimapMouseListener);
        addMouseWheelListener(minimapMouseWheelListener);
        addLayerViewPanelListener(minimapLayerViewPanelListener);
        
//        addLayerNamePanelListener(new MinimapLayerNamePanelListener()); //no tiene sentido responder a este evento
        
        minimapLayerListener = new MinimapLayerListener();
        addLayerListener(minimapLayerListener); // TODO, vigilar este evento porque puede que no funcione bien,
        // sufre mucho cuando hay muchos elementos en pantalla
    }
    
//    public void removeListeners() {
//        removeMouseListener(minimapMouseListener);
//        removeMouseWheelListener(minimapMouseWheelListener);
//        removeLayerViewPanelListener(minimapLayerViewPanelListener);
//        
////        removeLayerListener(minimapLayerListener);
//    }
    
    /** Guarda el LayerViewPanel de la vista del usuario.*/
    public void setLayerViewPanel(LayerViewPanel lvp){
        MinimapController.userViewLvp = lvp;
    }
    
    public void setMap(BufferedImage map){
        mapPanel.setMap(map);
        mapPanel.repaint();
    }
    
    /** Registra un mouselistener en el frame.*/
    public void addComponentListener(ComponentListener listener){
        mapPanel.addComponentListener(listener);
    }
    
    /** Registra un mouselistener del frame.*/
    public void removeComponentListener(ComponentListener listener){
        mapPanel.removeComponentListener(listener);
    }
    
    /** Registra un mouselistener en el panel del minimapa (no en el frame).*/
    public void addMouseListener(MouseListener listener){
        mapPanel.addMouseListener(listener);
    }
    
    /** Elimina un mouselistener del panel del minimapa (no del frame).*/
    public void removeMouseListener(MouseListener listener){
        mapPanel.removeMouseListener(listener);
    }

    /** Registra un mousewheellistener en el panel del minimapa (no en el frame).*/
    public void addMouseWheelListener(MouseWheelListener listener) {
        mapPanel.addMouseWheelListener(listener);
    }
    
    /** Elimina un mousewheellistener del panel del minimapa (no del frame).*/
    public void removeMouseWheelListener(MouseWheelListener listener) {
        mapPanel.removeMouseWheelListener(listener);
    }
    
    public void addLayerViewPanelListener(LayerViewPanelListener listener){
        context.getLayerViewPanel().addListener(listener);
    }
    
    public void removeLayerViewPanelListener(LayerViewPanelListener listener){
        context.getLayerViewPanel().removeListener(listener);
    }
    
    public void addLayerNamePanelListener(LayerNamePanelListener listener){
        context.getLayerNamePanel().addListener(listener);
    }
    
    public void removeLayerNamePanelListener(LayerNamePanelListener listener){
        context.getLayerNamePanel().removeListener(listener);
    }
    
    public void addLayerListener(LayerListener listener){
        context.getLayerManager().addLayerListener(listener);
    }
    
    public void removeLayerListener(LayerListener listener){
        context.getLayerManager().removeLayerListener(listener);
    }
    
    public MapPanel getMapPanel(){
        return mapPanel;
    }
    
    /** Devuelve una copia (para evitar efectos colaterales) del 
     * envelope de la vista actual (lo que está viendo el usuario).*/
    public static Envelope getViewEnvelope() {
    	Envelope env = userViewLvp.getViewport().getEnvelopeInModelCoordinates();
    	
    	env = new Envelope(env);
    	
    	return env;
    }
    
    /** Devuelve una copia (para evitar efectos colaterales) del 
     * envelope del full extent de la vista.*/
    public static Envelope getFullViewEnvelope() {
    	Envelope env = userViewLvp.getViewport().fullExtent();
    	
    	env = new Envelope(env);
    	
    	return env;
    }
    
    /**
     * MouseListener para que al hacer clic en el minimapa se haga el desplazamiento
     * correspondiente en el LayerViewPanel.
     * @return MouseListener
     */
    class MinimapMouseListener extends MouseAdapter{
        @Override
        public void mouseReleased(MouseEvent me) {
            
            Point p = me.getPoint();
            Envelope newEnvelope = MinimapUtils.calculateNewView(p, 1, mapPanel, userViewLvp);
            
            try {
                Viewport viewport = userViewLvp.getViewport();
                viewport.zoom(newEnvelope, true);
            } catch (NoninvertibleTransformException nte) {
                LOGGER.error("", nte); // $NON-NLS-1$
            }
            
            // repintar para que se dibuje el indicador de posición (rectángulo o cruz)
            mapPanel.repaint();
        }
    }
    
    /**
     * Crea un ComponentListener para que al mostrar la ventana del minimap y al 
     * cambiar su tamaño se recalcule la imagen.
     * @return InternalFrameListener
     */
    class MinimapComponentListener extends ComponentAdapter{
        @Override
        public void componentResized(ComponentEvent e) {
            regenerateMinimap();                
        }
        
        @Override
        public void componentShown(ComponentEvent e) {
            regenerateMinimap(); // TODO ¿esto es redundante con la llamada a regenerateMinimap en el execute del plugin?
        }
    }
    
    /**
     * Crea un listener para que al hacer girar la rueda del ratón sobre el
     * minimapa se haga zoom sobre el layerviewpanel.
     * @return MouseWheelListener
     */
    class MinimapMouseWheelListener implements MouseWheelListener{
        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            int notches = mwe.getWheelRotation();
            Viewport viewport = userViewLvp.getViewport();
            Envelope env = viewport.getEnvelopeInModelCoordinates();
            double factor = Math.pow(2, notches);
            env = MinimapUtils.calculateNewView(mwe.getPoint(), factor, mapPanel, userViewLvp);
            try {
                viewport.zoom(env, true);
            } catch (NoninvertibleTransformException nte) {
                LOGGER.error("", nte); // $NON-NLS-1$
            }
            
            mapPanel.repaint();
        }
    }
    
    class MinimapLayerViewPanelListener implements LayerViewPanelListener{
        @Override
        public void cursorPositionChanged(String x, String y) {
        }

        @Override
        public void painted(Graphics graphics) {
            mapPanel.repaint();
        }

        @Override
        public void renderingFinished() {
        }

        @Override
        public void renderingStarted() {
        }

        @Override
        public void selectionChanged() {
        }
    }
    
//    /** Se dispara cuando cambia se selecciona otra layer en el árbol de nombres.*/
//    public class MinimapLayerNamePanelListener implements LayerNamePanelListener{
//        @Override
//        public void layerSelectionChanged() {
//            regenerateMinimap();
//            mapPanel.repaint();
//        }
//    }
    
    /** Se dispara cuando se muestra, oculta, añade, elimina o modifica alguna capa.*/
    public class MinimapLayerListener implements LayerListener{
        @Override
        public void categoryChanged(CategoryEvent e) {
        	// cuando se añade, se mueve o elimina una categoría
        	
        	
        	// TODO cuando se mueve una categoría se lanzan dos eventos (remove y add)
        	// por lo que se ejecuta dos veces seguidas. Habría que buscar una forma
        	// de evitar esta duplicidad pero haría falta un nuevo CategoryEventType 
        	// MOVED o algo así
        	
        	List<Layerable> layerables = e.getCategory().getLayerables();
			if (CollectionUtils.isNotEmpty(layerables)) {
				// añdir, mover o borrar una categoría vacía no modifica el minimapa
				regenerateMinimap();
	            mapPanel.repaint();
        	}
        }

        @Override
        public void featuresChanged(FeatureEvent e) {
        	// cuando se modifica, añade o elimina una feature salta este evento además 
        	// del evento layerChanged por lo que es redundante meter aquí el #regenerateMinimap
//            regenerateMinimap();
//            mapPanel.repaint();
        }

        @Override
        public void layerChanged(LayerEvent e) {
        	//cuando una capa se añade, elimina, oculta, hace visible,
        	// cambia su orden respecto a otras capas, cambia su esquema,
        	// cambia de categoría
        	// o añade, modifica o elimina una feature
            regenerateMinimap();
            mapPanel.repaint();
        }
    }
    
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}

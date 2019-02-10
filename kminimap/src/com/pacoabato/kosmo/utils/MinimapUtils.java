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
package com.pacoabato.kosmo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.saig.core.renderer3.SingleThreadedMapDrawer;
import org.saig.core.styling.FeatureTypeStyle;
import org.saig.core.styling.Rule;
import org.saig.core.styling.Style;
import org.saig.core.styling.Symbolizer;
import org.saig.core.styling.TextSymbolizer;
import org.saig.core.util.UnitsManager;

import com.pacoabato.kosmo.config.MinimapConfigPanel;
import com.pacoabato.kosmo.extension.ImageHolder;
import com.pacoabato.kosmo.kminimap.MapPanel;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerNamePanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.Viewport;

/**
 * Útiles para la extensión del minimapa.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 * @since 2.0
 */
public class MinimapUtils{
    
    private static final Logger LOGGER = Logger.getLogger(MinimapUtils.class);
    
    /** Indica el tamaño mínimo en píxel para que se dibuje un 
     * recuadro de posición, en caso de que el ancho o el alto sea menor
     * se dibujará una cruz.*/
    private static final int MIN_DIM = 5;
    
//    private static final String LOADING_IMAGE_PATH = "loading.gif";
    
    /**
     * Extrae el Graphics2D de la imagen, lo configura y pinta toda
     * la extensión de blanco.
     * 
     * @param imagen la imagen de la que se quiere obtener el Graphics2D
     * @return Graphics2D configurado y pintado de blanco.
     */
    public static Graphics2D extractGraphics(BufferedImage imagen) {
        Graphics2D g = (Graphics2D) imagen.getGraphics();
        RenderingHints renderHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        renderHints.put(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        renderHints.put(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHints(renderHints);
        g.setColor(Color.WHITE); // color del fondo de la imagen en las zonas donde no se dibuje nada
        g.fillRect(0, 0, imagen.getWidth(), imagen.getHeight());
        return g;
    }
    
    /** Dibuja en g una cruz en el centro del envelope.*/
    public static void drawCross(Graphics g, Envelope env){
        int centerx = (int) ((env.getMinX() + env.getMaxX()) / 2);
        int centery = (int) ((env.getMinY() + env.getMaxY()) / 2);
        int left = centerx - 10;
        int right = centerx + 10;
        int bottom = centery + 10;
        int top = centery - 10;
        
        Color interiorColor = MinimapConfigPanel.getMinimapInteriorIndicatorColor();
        float interiorAlpha = MinimapConfigPanel.getMinimapInteriorIndicatorAlpha();
        Color exteriorColor = MinimapConfigPanel.getMinimapExteriorIndicatorColor();
        float exteriorAlpha = MinimapConfigPanel.getMinimapExteriorIndicatorAlpha();
        
        // dibujar un contorno para contraste con el fondo
        setAlpha(g, exteriorAlpha);
        g.setColor(exteriorColor);
        g.drawRect(left-1, centery-1, right-left+2, 2);
        g.drawRect(centerx-1, top-1, 2, bottom-top+2);
        
        // dibujar la cruz interior
        setAlpha(g, interiorAlpha);
        g.setColor(interiorColor);
        g.drawLine(left, centery, right, centery);
        g.drawLine(centerx, bottom, centerx, top);
    }
    
    /** Dibuja un rectángulo en g que coincide con env.*/
    public static void drawRect(Graphics g, Envelope env){
        int minx = (int) env.getMinX();
        int miny = (int) env.getMinY();
        int width = (int) env.getWidth();
        int height = (int) env.getHeight();
        
        Color interiorColor = MinimapConfigPanel.getMinimapInteriorIndicatorColor();
        float interiorAlpha = MinimapConfigPanel.getMinimapInteriorIndicatorAlpha();
        Color exteriorColor = MinimapConfigPanel.getMinimapExteriorIndicatorColor();
        float exteriorAlpha = MinimapConfigPanel.getMinimapExteriorIndicatorAlpha();
        
        // dibuja en negro un contorno para contraste con el fondo
        setAlpha(g, exteriorAlpha);
        g.setColor(exteriorColor);
        g.drawRect(minx+1, miny+1, width-2, height-2);
        g.drawRect(minx-1, miny-1, width+2, height+2);
        
        // dibuja el marco principal
        setAlpha(g, interiorAlpha);
        g.setColor(interiorColor);
        g.drawRect(minx, miny, width, height);
    }
    
    private static void setAlpha(Graphics g, float alpha) {
    	Graphics2D g2d = (Graphics2D)g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }

    /**
     * Dibuja env en g o una cruz si las dimensiones de env son 
     * demasiado pequeñas.
     * @param env las unidades son pixel
     */
    public static void drawEnvelope(Graphics g, Envelope env) {
        if (g == null || env == null) {
            return;
        }
        
        int width = (int) env.getWidth();
        int height = (int) env.getHeight();
        if (width >= MIN_DIM && height >= MIN_DIM) {
            MinimapUtils.drawRect(g, env);
        } else {
            MinimapUtils.drawCross(g, env);
        }
    }

    public static int getMapPanelWidth() {
		int width = MinimapConfigPanel.getMinimapPanelWidth();
	    
		return width;
	}
    
    public static int getMapPanelHeight() {
		int height = MinimapConfigPanel.getMinimapPanelHeight();
		
		return height;
	}

//    /**
//     * Dibuja en g el aviso de loading.
//     * @param g
//     */
//    public static void drawLoadingLogo(Graphics2D g) {
//        Image loadingImage = null;
//        
//        URL url = MinimapUtils.class.getResource(LOADING_IMAGE_PATH);
//        try{
//            loadingImage = new ImageIcon(url).getImage();
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
//        
//        g.drawImage(loadingImage, 0, 0, null);
//    }
    
    /**
     * Calcula un envelope para el minimapa que encuadra la vista del layerviewpanel.
     * Tiene en cuenta la inversión del eje vertical para dibujar en el canvas.
     * Dibujando diréctamente las coordenadas del envelope devuelto en el minimapa
     * quedará enmarcado en el mismo lo que el usuario está viendo en el layerviewpanel.
     * @param userViewEnv envelope de la vista del layerviewpanel que ve el usuario, en unidades del mundo. En 
     * todo caso es el recuadro de la vista que se va a "traducir" a las dimensiones de la imagen final (la 
     * que tiene mapPanel).
     * @param fullEnv es el recuadro que enmarca a todo el mundo en la vista.*/
    public static Envelope viewEnvelopeToMinimap(ImageHolder mapPanel, Envelope userViewEnv, Envelope fullEnv) {
        double viewMinX = userViewEnv.getMinX();
        double viewMaxX = userViewEnv.getMaxX();
        double viewMinY = userViewEnv.getMinY();
        double viewMaxY = userViewEnv.getMaxY();

        double fullMinX = fullEnv.getMinX();
        double fullMaxX = fullEnv.getMaxX();
        double fullMinY = fullEnv.getMinY();
        double fullMaxY = fullEnv.getMaxY();
        
        double fullCenterX = (fullMinX + fullMaxX) / 2;
        double fullCenterY = (fullMinY + fullMaxY) / 2;
        
        double fullWidth = fullEnv.getWidth();
        double fullHeight = fullEnv.getHeight();
        
        double xMinRatio = (viewMinX - fullCenterX) / fullWidth;
        double xMaxRatio = (viewMaxX - fullCenterX) / fullWidth;
        double yMinRatio = (viewMinY - fullCenterY) / fullHeight;
        double yMaxRatio = (viewMaxY - fullCenterY) / fullHeight;
        
        
        double panelCenterX = mapPanel.getWidth() / 2;
        double panelCenterY = mapPanel.getHeight() / 2;
        
        int imageWidth = mapPanel.getImage().getWidth();
        int imageHeight = mapPanel.getImage().getHeight();

        Envelope resEnv = null;
        try{
        	double xmin = panelCenterX + imageWidth * xMinRatio;
        	double xmax = panelCenterX + imageWidth * xMaxRatio;
        	double ymin = panelCenterY - imageHeight * yMinRatio;
        	double ymax = panelCenterY - imageHeight * yMaxRatio;;
        			
            resEnv = new Envelope(
                    xmin, xmax, ymin, ymax);
        }catch(Exception ex){
            LOGGER.error("", ex); //$NON-NLS-1$
        }
        
        return resEnv;
    }
    
    /**
     * Calcula un envelope para el layerviewpanel a partir de un
     * punto del minimapa y de un nivel de zoom.
     * 
     * @param p el punto del minimapa que se usa para calcular el nuevo
     * centro de la vista en el layerviewpanel.
     * @param factor factor de zoom. 1 lo deja como está, un valor mayor que 1 aumenta
     * el zoom y un valor entre 0 y 1 lo disminuye.*/
    public static Envelope calculateNewView(Point p, double factor, MapPanel mapPanel, LayerViewPanel userViewLvp){
        Envelope newEnv = null;
        
        double panelCenterX = mapPanel.getWidth() / 2;
        double panelCenterY = mapPanel.getHeight() / 2;
        
        double xFromCenter = panelCenterX - p.x;
        double yFromCenter = panelCenterY - p.y;
        
        int mapWidth = mapPanel.getImage().getWidth(); //mapPanel.getWidth(); //getMapWidth();
        int mapHeight = mapPanel.getImage().getHeight(); //mapPanel.getHeight(); //getMapHeight();
        double xratio = -1 * xFromCenter / mapWidth;
        double yratio = yFromCenter / mapHeight;
        // yratio no se multiplica por -1 porque ya viene invertido en p
        
        Viewport viewport = userViewLvp.getViewport();
        Envelope currentEnv = viewport.getEnvelopeInModelCoordinates();
        double currentWidth = currentEnv.getWidth();
        double currentHeight = currentEnv.getHeight();
        
        Envelope worldFullExtent = viewport.fullExtent();
        double worldWidth = worldFullExtent.getWidth();
        double worldHeight = worldFullExtent.getHeight();
        double worldXOrigin = worldFullExtent.getMinX();
        double worldYOrigin = worldFullExtent.getMinY();
        
        double worldCenterX = worldXOrigin + worldWidth / 2;
        double worldCenterY = worldYOrigin + worldHeight / 2;
        
        double x = worldCenterX + worldWidth * xratio;
        double y = worldCenterY + worldHeight * yratio;
        
        newEnv = new Envelope(
                x - (currentWidth / 2 * factor),
                x + (currentWidth / 2 * factor),
                y - (currentHeight / 2 * factor),
                y + (currentHeight / 2 * factor)
                );
        
       return newEnv;
    }
    
    /**
     * Genera una imagen (del tamaño del canvas en el que se va a dibujar el minimapa)
     * de todo el mundo con las capas que actualmente se encuentren en estado visible.
     * Tiene en cuenta el aspect ratio de la vista.
     * 
     * @param userViewLvp El LayerViewPanel de la vista que está vinculada con
     * el minimapa.
     * @param imgWidth el ancho de la imagen deseada (puede ser reducido para respetar
     * el aspect ratio de la vista).
     * @param imgHeight el alto de la imagen deseada (puede ser reducido para respetar
     * el aspect ratio de la vista).
     * 
     * @return BufferedImage La imagen es la generada a partir de todo el mundo
     * con un tamaño en píxel igual al del canvas del minimap.
     */
    public static BufferedImage getWorldImage(LayerViewPanel userViewLvp, PlugInContext context,
    		int imgWidth, int imgHeight) {
        Viewport viewport = userViewLvp.getViewport();
        Envelope viewportEnv = viewport.fullExtent();
        
        BufferedImage image = createImageWithAspectRatio(viewportEnv, imgWidth, imgHeight);
        
        Envelope env = new Envelope(
    			viewportEnv.getMinX(),
    			viewportEnv.getMaxX()+1, //TODO revisar el +1/-1
    			viewportEnv.getMinY()-1,
    			viewportEnv.getMaxY());
        
        copyViewToImage(userViewLvp, context, env, image);
        
        return image;
    }
    
    /** Crea un BufferedImage (vacío) con el tamaño indicado y guardando el aspect ratio 
     * de viewportEnv.
     * @param refEnv el recuadro cuyo aspect ratio se quiere pasar a la imagen.
     * @param imgWidth ancho deseado para la imagen resultante (será reducido si refEnv 
     * es más alto que ancho).
     * @param imgHeight ancho deseado para la imagen resultante (será reducido si refEnv
     * es más ancho que alto).*/
    public static BufferedImage createImageWithAspectRatio(Envelope refEnv, int imgWidth, int imgHeight) {
    	
    	double width = refEnv.getWidth();
        double height = refEnv.getHeight();
        
        if (width != height) {
            // hay que calcular imgWidth e imgHeight para que tengan el mismo 
            // aspect ratio que width y height (si no el minimapa se dibuja deformado)
            
            if (width > height) {
                imgHeight = (int) (imgWidth * height / width);
            } else {
                imgWidth = (int) (imgHeight * width / height);
            }
        }
        
        BufferedImage imagen = new BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_ARGB);
        
        return imagen;
    }
    
    /**
     * Copia en imagen la parte de la vista enmarcada por el parámetro env, con 
     * las capas que actualmente se encuentren en estado visible.
     * 
     * @param userViewLvp El LayerViewPanel de la vista que está vinculada con
     * el minimapa.
     * @param imagen BufferedImage en el que se va a dibujar.
     */
    public static void copyViewToImage(LayerViewPanel userViewLvp, 
            PlugInContext context, Envelope env, BufferedImage imagen) {
    	
    	boolean disableLabels = MinimapConfigPanel.getMinimapDisableLabels();
    	List<Layerable> layerables = MinimapUtils.getVisibleLayerables(context);
         
    	Map<Symbolizer, Boolean> labelsPrevState = null;
         
        try {
            
            // ***********************************
            // ***********************************
            
//            MinimapUtils.drawLoadingLogo(g);
//            mapPanel.setMap(imagen, null);
//            mapPanel.repaint();
            // FIXME intentando que dibuje una imagen de loading
            
            // ***********************************
            // ***********************************
            
            
            // dibujar las capas
            SingleThreadedMapDrawer drawer = new SingleThreadedMapDrawer();
            Unit<Length> unit = UnitsManager.getUserLengthUnit(context.getTask());
            
            if (disableLabels) {
            	labelsPrevState = disableLabels(layerables);
            }
            
            drawer.drawMap(imagen, env, layerables, unit, null);
            
        } catch(Exception ex) {
            LOGGER.error("", ex); //$NON-NLS-1$
        } finally {
        	if (disableLabels && labelsPrevState != null) {
        		restoreLabelsState(layerables, labelsPrevState);
        	}
        }
    }
    
    /**
     * Devuelve las simbologías de tipo textual a su estado previo.
     * @param layerables capas que se van a modificar
     * @param labelsPrevState almacena el estado previo de cada simbología
     * de tipo textual
     */
    private static void restoreLabelsState(List<Layerable> layerables,
            Map<Symbolizer, Boolean> labelsPrevState) {
        for (Layerable layerable : layerables) {
            Style modelStyle = layerable.getModelStyle();
            
            if (modelStyle == null) {
                // por ejemplo con capas WMS
                continue;
            }
            
            FeatureTypeStyle[] featureTypeStyles = modelStyle.getFeatureTypeStyles();

            for(int cont= 0; cont < featureTypeStyles.length; cont++) {
                FeatureTypeStyle fts = featureTypeStyles[cont];
                Rule[] rules = fts.getRules();
                for( int i = 0; i < rules.length; i++ ) {
                    Symbolizer[] simbolos = rules[i].getSymbolizers();
                    for( int j = 0; j < simbolos.length; j++ ) {
                        Symbolizer simbolo = simbolos[j];
                        if (simbolo instanceof TextSymbolizer) {
                            Boolean prevState = labelsPrevState.get(simbolo);
                            simbolo.setActive(prevState);
                        }
                    }
                }
            }
        }
    }

    /** Desactiva las simbologías de tipo texto de todos los layerables.
     * @return un map que almacena el estado previo de cada simbología textual (activa/inactiva).*/
    private static Map<Symbolizer, Boolean> disableLabels(List<Layerable> layerables) {
        Map<Symbolizer, Boolean> map = new HashMap<Symbolizer, Boolean>();
        
        for (Layerable layerable : layerables) {
            Style modelStyle = layerable.getModelStyle();
            
            if (modelStyle == null) {
            	continue;
            }
            
            FeatureTypeStyle[] featureTypeStyles = modelStyle.getFeatureTypeStyles();
            
            for(int cont= 0; cont < featureTypeStyles.length; cont++) {
                FeatureTypeStyle fts = featureTypeStyles[cont];
                Rule[] rules = fts.getRules();
                for( int i = 0; i < rules.length; i++ ) {
                    Symbolizer[] simbolos = rules[i].getSymbolizers();
                    for( int j = 0; j < simbolos.length; j++ ) {
                        Symbolizer simbolo = simbolos[j];
                        if (simbolo instanceof TextSymbolizer) {
                            map.put(simbolo, simbolo.isActive());
                            
                            simbolo.setActive(false);
                        }
                    }
                }
            }
        }
        
        return map;
    }
    
    
    /** Puede devolver una lista vacía pero nunca null. Devuelve las capas en 
     * orden inverso en el que los devuelve el layermanager.getAllLayers() (este
     * método parece devolver las capas en orden inverso al que se renderiza
     * en la vista).*/
    private static List<Layerable> getVisibleLayerables(PlugInContext context) {
		LayerNamePanel lnp = context.getLayerNamePanel();
		if (lnp == null) {
			return new ArrayList<Layerable>();
		}
		
		Collection<Layerable> allLayers = lnp.getLayerManager().getAllLayers();
		ArrayList<Layerable> allLayersList = new ArrayList<Layerable>(allLayers);
		
		ArrayList<Layerable> visibleLayers = new ArrayList<Layerable>();
		
		ListIterator<Layerable> it = allLayersList.listIterator(allLayersList.size());
		while (it.hasPrevious()) {
		    Layerable layerable = it.previous();
			if (layerable.isVisible()) {
				visibleLayers.add(layerable);
			}
		}
		
		return visibleLayers;
	}
    
    /** Devuelve el punto del panel en el que se debe situar la esquina
     * superior izquierda de la imagen para que ésta quede centrada en aquel.*/
    public static Point2D getImageOrigin(JPanel panel, BufferedImage image) {
    	int panelCenterX = panel.getWidth() / 2;
        int panelCenterY = panel.getHeight() / 2;
        double originX = panelCenterX - image.getWidth() / 2;
        double originY = panelCenterY - image.getHeight() / 2;
        
        return new Point2D.Double(originX, originY);
    }
}

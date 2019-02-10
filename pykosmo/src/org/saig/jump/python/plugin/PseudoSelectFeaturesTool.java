package org.saig.jump.python.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.saig.jump.lang.I18N;

import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.geom.EnvelopeUtil;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.cursortool.CursorTool;
import com.vividsolutions.jump.workbench.ui.cursortool.SelectTool;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;
import com.vividsolutions.jump.workbench.ui.renderer.FeatureSelectionRenderer;

/**
 * Herramienta que permite seleccionar un conjunto de elementos de las capas visibles. 
 * 
 * La idea es que se pueda llamar a esta herramienta desde python para obtener así 
 * elementos seleccionados por el usuario.
 * 
 * TODO está sin terminar
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com 
 */
public class PseudoSelectFeaturesTool extends SelectTool {
    
    /** Nombre asociado a la herramienta */
    public final static String NAME = 
        I18N.getString("xxx");
    
    /** Icono asociado a la herramienta */
    public final static Icon ICON = IconLoader.icon("Select.gif"); //$NON-NLS-1$
    
    static public Map<Layer, Collection<Feature>> features;
    
    static public List<Layerable> layers;
    
    /**
     * Constructor por defecto
     */
    public PseudoSelectFeaturesTool() {
        super(FeatureSelectionRenderer.CONTENT_ID);
        layers = Arrays.asList(JUMPWorkbench.getFrameInstance().getContext().getWorkbench()
                .getContext().getLayerNamePanel().getSelectedLayers());
    }
    
    /**
     * Constructor
     * @param layers a list with the layers in wich the selected features will
     * be searched for.
     */
    public PseudoSelectFeaturesTool(List<Layerable> layers) {
        super(FeatureSelectionRenderer.CONTENT_ID);
        PseudoSelectFeaturesTool.layers = layers;
    }
    
    /**
     * Devuelve el nombre asociado a la herramienta
     * 
     * @return String - Nombre asociado a la herramienta
     */
    public String getName(){
        return NAME;
    }

    /**
     * Devuelve el icono asociado a la herramienta
     * 
     * @return Icon - Icono asociado a la herramienta
     */
    public Icon getIcon(){
        return ICON;
    }
    
    public void activate(LayerViewPanel layerViewPanel) {
        super.activate(layerViewPanel);
        selection = layerViewPanel.getSelectionManager().getFeatureSelection();
    }
    
    
    @SuppressWarnings("unchecked")
    protected void gestureFinished() throws Exception {
		reportNothingToUndoYet();
        
        PlugInContext context = JUMPWorkbench.getFrameInstance().getContext().createPlugInContext();

        features = context.getLayerViewPanel().layersToFeaturesInFenceMap(
                getLayerNameFilter(),
                EnvelopeUtil.toGeometry(getBoxInModelCoordinates()));
	}
    
    public Map<Layer, Collection<Feature>> getSelectedFeatures(){
        return features;
    }
    
    public Collection<Feature> getSelectedFeatures(Layer layer){
        return features.get(layer);
    }
    
    
    
    /**
     * Obtiene el filtro de capas para la consulta
     *
     * @return List <Layerable>
     */
    protected List<Layerable> getLayerNameFilter(){   
        return layers;
    }
	
	/**
     * 
     *
     * @param workbenchContext
     * @param tool
     * @return MultiEnableCheck
	 */
	public static MultiEnableCheck createEnableCheck(
			final WorkbenchContext workbenchContext,CursorTool tool) {
		MultiEnableCheck solucion = new MultiEnableCheck(tool);
		EnableCheckFactory checkFactory = new EnableCheckFactory(
				workbenchContext);
		// al menos una capa debe tener elementos activos
		solucion.add(checkFactory.createTaskWindowMustBeActiveCheck());
		// solo una capa puede tener elementos seleccionados.
		solucion.add(checkFactory.createAtLeastNLayersMustExistCheck(1));
		solucion.add(checkFactory.createSelectedLayersMustNotBeRasterCheck());
		solucion.add(checkFactory.createSelectedLayerMustBeActiveCheck());
		return solucion;
	}
}

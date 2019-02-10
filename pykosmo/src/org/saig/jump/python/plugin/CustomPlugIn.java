package org.saig.jump.python.plugin;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.saig.core.model.data.Record;
import org.saig.core.model.data.dao.jdbc.PostgreSQLDataSource;

import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;

/**
 * PlugIn para hacer pruebas y así.
 * 
 * @author Francisco Abato Helguera - pacoabato@gmail.com
 */
public class CustomPlugIn extends AbstractPlugIn{
	
    private static final Logger LOGGER = Logger.getLogger(CustomPlugIn.class);
	
	private static final String NAME = "Custom PlugIn";
	private static final Icon ICON = IconLoader.icon("arrow_refresh.png");
	
	
	private Object context;
	private Object layerViewPanel;
	private Object layerNamePanel;
	private Object selectionManager;
	
	@Override
	public boolean execute(PlugInContext plugincontext) throws Exception {
		
	    String tablename = "fichas_de_campo";
	    String pkname = "id_ficha_campo";
	    PostgreSQLDataSource martaDataSource = new PostgreSQLDataSource(
                "localhost", 5432, "burgos_marta", "postgres", "postgres");
        martaDataSource.setTableName(tablename);
        martaDataSource.setName(tablename);
        martaDataSource.buildSchema();
        martaDataSource.setPkName(pkname);
        
        PostgreSQLDataSource rafaDataSource = new PostgreSQLDataSource(
                "localhost", 5432, "burgos_rafa", "postgres", "postgres");
        rafaDataSource.setTableName(tablename);
        rafaDataSource.setName(tablename);
        rafaDataSource.buildSchema();
        rafaDataSource.setPkName(pkname);
        
        
        int[] pks = {178, 182, 184, 186, 187, 190, 170, 173, 205, 206, 209, 216, 217, 224, 230, 231, 232, 233, 234, 235, 237, 481, 709, 710, 777, 80, 103, 110, 435, 671, 534, 541, 540, 543, 542, 503, 435, 671, 534, 541, 540, 543, 542, 503, 237, 186, 187, 184, 190, 182, 235, 234, 233, 232, 231, 230, 205, 206, 209, 777, 170, 175, 173, 710, 178, 177, 481, 709, 224, 216, 217, 80, 103, 110};
        
        
        
	    FeatureSchema schema = martaDataSource.getSchema();
	    
	    for(int pk:pks){
	        Record martaRecord = martaDataSource.getByPrimaryKey(pk); 
	        Record rafaRecord = rafaDataSource.getByPrimaryKey(pk); 
	        
    	    for (String attrName : schema.getAttributeNames()){
    	        if (!attrName.equals(pkname)){
    	            martaRecord.setAttribute(
    	                    attrName,
    	                    rafaRecord.getAttribute(attrName));
    	        }
    	    }
    	    
    	    martaDataSource.update(martaRecord);
	    }
	    
	    try{
	        martaDataSource.commit();
	    }catch(Exception ex){
	        LOGGER.error("", ex);
	        martaDataSource.rollback();
	    }
	    
//	    if(context != null){
//	        if(context.equals(JUMPWorkbench.getFrameInstance().getContext())){
//	            LOGGER.info("Contextos: IGUALES");
//            }else{
//                LOGGER.info("Contextos: DISTINTOS");
//            }
//	        
//	        if(layerViewPanel.equals(JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel())){
//	            LOGGER.info("LayerViewPanels: IGUALES");
//            }else{
//                LOGGER.info("LayerViewPanels: DISTINTOS");
//            }
//	        
//	        if(layerNamePanel.equals(JUMPWorkbench.getFrameInstance().getContext().getLayerNamePanel())){
//                LOGGER.info("LayerNamePanels: IGUALES");
//            }else{
//                LOGGER.info("LayerNamePanels: DISTINTOS");
//            }
//            
//	        if(selectionManager.equals(JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel().getSelectionManager())){
//                LOGGER.info("SelectionManagers: IGUALES");
//            }else{
//                LOGGER.info("SelectionManagers: DISTINTOS");
//            }
//	    }else{
//	        LOGGER.info("Capturando objetos por primera vez.");
//	    }
//	    
//	    context = JUMPWorkbench.getFrameInstance().getContext();
//	    layerViewPanel = JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel();
//	    layerNamePanel = JUMPWorkbench.getFrameInstance().getContext().getLayerNamePanel();
//	    selectionManager = JUMPWorkbench.getFrameInstance().getContext().getLayerViewPanel().getSelectionManager();
//	    
		return true;
	}

	@Override
	public EnableCheck getCheck() {
		return createEnableCheck(JUMPWorkbench.getFrameInstance().getContext());
	}

	public static EnableCheck createEnableCheck(WorkbenchContext context) {
        return new MultiEnableCheck();
	}

	@Override
	public Icon getIcon() {
		return ICON;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		context.getWorkbenchFrame().getToolBar().addPlugIn(this, context.getWorkbenchContext());
	}

	@Override
	public void finish(PlugInContext context) {
		context.getWorkbenchFrame().getToolBar().removePlugIn(this);
	}
}
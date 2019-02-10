package com.pacoabato.kosmo.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.saig.core.gui.swing.sldeditor.util.FormUtils;
import org.saig.jump.lang.I18N;
import org.saig.jump.widgets.util.JColorButton;
import org.saig.jump.widgets.util.NumberSpinner;

import com.vividsolutions.jump.util.Blackboard;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.OptionsPanel;
import com.vividsolutions.jump.workbench.ui.plugin.PersistentBlackboardPlugIn;

@SuppressWarnings("serial")
public class MinimapConfigPanel extends OptionsPanel {
	private static final String NAME = I18N.getString(MinimapConfigPanel.class, "minimap");  //$NON-NLS-1$
	
	private Blackboard blackboard;
	private NumberSpinner jsWidth;
	private NumberSpinner jsHeight;
	private JColorButton jcbInteriorColor;
	private JSlider jsInteriorAlpha;
	private JColorButton jcbExteriorColor;
    private JSlider jsExteriorAlpha;
    private JCheckBox jcbDisableLabels;
	
	private static final String BB_KEY_MINIMAP_PANEL_WIDTH = 
	    MinimapConfigPanel.class.getName() + " - MINIMAP_PANEL_WIDTH"; //$NON-NLS-1$
	private static final String BB_KEY_MINIMAP_PANEL_HEIGHT = 
	    MinimapConfigPanel.class.getName() + " - MINIMAP_PANEL_HEIGHT"; //$NON-NLS-1$
	
	private static final String BB_KEY_MINIMAP_INDICATOR_INTERIOR_COLOR = 
        MinimapConfigPanel.class.getName() + " - MINIMAP_INDICATOR_INTERIOR_COLOR"; //$NON-NLS-1$
    private static final String BB_KEY_MINIMAP_INDICATOR_INTERIOR_ALPHA = 
        MinimapConfigPanel.class.getName() + " - MINIMAP_INDICATOR_INTERIOR_ALPHA"; //$NON-NLS-1$
    
    private static final String BB_KEY_MINIMAP_INDICATOR_EXTERIOR_COLOR = 
        MinimapConfigPanel.class.getName() + " - MINIMAP_INDICATOR_EXTERIOR_COLOR"; //$NON-NLS-1$
    private static final String BB_KEY_MINIMAP_INDICATOR_EXTERIOR_ALPHA = 
        MinimapConfigPanel.class.getName() + " - MINIMAP_INDICATOR_EXTERIOR_ALPHA"; //$NON-NLS-1$
    
    private static final String BB_KEY_MINIMAP_DISABLE_LABELS = 
	    MinimapConfigPanel.class.getName() + " - MINIMAP_DISABLE_LABELS"; //$NON-NLS-1$
	
    
	
	private static final int DEFAULT_WIDTH = 200;
	private static final int DEFAULT_HEIGHT = 200;
	private static final Color DEFAULT_INTERIOR_COLOR = Color.RED;
	private static final float DEFAULT_INTERIOR_ALPHA = 1f;
	private static final Color DEFAULT_EXTERIOR_COLOR = Color.BLACK;
    private static final float DEFAULT_EXTERIOR_ALPHA = .6f;
    private static final boolean DEFAULT_DISABLE_LABELS = true;
    
	public MinimapConfigPanel(PlugInContext context) {
		super();
		
		blackboard = context.getWorkbenchContext().getBlackboard();
		
		initComponents();
		initGui();
	}
	
    private void initComponents() {
        jsWidth = new NumberSpinner(DEFAULT_WIDTH, 50, 1000, 10);
        jsHeight = new NumberSpinner(DEFAULT_HEIGHT, 50, 1000, 10);
        
        Dimension buttonDim = new Dimension(40, 20);
        
        jcbInteriorColor = new JColorButton();
        jcbInteriorColor.setMinimumSize(buttonDim);
        jcbInteriorColor.setPreferredSize(buttonDim);
        jcbInteriorColor.setMaximumSize(buttonDim);
        
        jsInteriorAlpha = new JSlider(0, 100, (int) (DEFAULT_INTERIOR_ALPHA * 100));
        jsInteriorAlpha.setMajorTickSpacing(10);
        jsInteriorAlpha.setMinorTickSpacing(5);
        jsInteriorAlpha.setPaintTicks(true);
        jsInteriorAlpha.setPaintLabels(true);
        
        jcbExteriorColor = new JColorButton();
        jcbExteriorColor.setMinimumSize(buttonDim);
        jcbExteriorColor.setPreferredSize(buttonDim);
        jcbExteriorColor.setMaximumSize(buttonDim);
        
        jsExteriorAlpha = new JSlider(0, 100, (int) (DEFAULT_EXTERIOR_ALPHA * 100));
        jsExteriorAlpha.setMajorTickSpacing(10);
        jsExteriorAlpha.setMinorTickSpacing(5);
        jsExteriorAlpha.setPaintTicks(true);
        jsExteriorAlpha.setPaintLabels(true);
        
        String disableLabels = I18N.getString(getClass(), "disable_labels"); //$NON-NLS-1$
        jcbDisableLabels = new JCheckBox(disableLabels);
    }

    private void initGui() {
		setLayout(new GridBagLayout());
		
		JPanel panelDimensions = getPanelDimensions();
		JPanel panelIndicator = getPanelIndicator();
		JPanel panelLabels = getPanelLabels();
		
		FormUtils.addRowInGBL(this, 0, 0, panelLabels);
		FormUtils.addRowInGBL(this, 1, 0, panelDimensions);
		FormUtils.addRowInGBL(this, 2, 0, panelIndicator);
		FormUtils.addFiller(this, 3, 0);
	}
    
    private JPanel getPanelLabels() {
        JPanel panelLabels = new JPanel(new GridBagLayout());
        FormUtils.addRowInGBL(panelLabels, 0, 0, jcbDisableLabels, true, false);
        FormUtils.addFiller(panelLabels, 1, 0);
        String labels = I18N.getString(getClass(), "labels"); //$NON-NLS-1$
        panelLabels.setBorder(BorderFactory.createTitledBorder(labels));
        
        return panelLabels;
    }
    
	private JPanel getPanelIndicator() {
	    String labelIndicator = I18N.getString(getClass(), "indicator"); //$NON-NLS-1$
	    String labelInterior = I18N.getString(getClass(), "interior"); //$NON-NLS-1$
	    String labelExterior = I18N.getString(getClass(), "exterior"); //$NON-NLS-1$
        String labelColor = I18N.getString(getClass(), "color"); //$NON-NLS-1$
        String labelAlpha= I18N.getString(getClass(), "opacity"); //$NON-NLS-1$
        
        // FIXME buscar otra forma de meter un poco de espacio
        JLabel spacer1 = new JLabel("   "); //$NON-NLS-1$ 
        JLabel spacer2 = new JLabel("   "); //$NON-NLS-1$
        
        JPanel panelInterior = new JPanel(new GridBagLayout());
        FormUtils.addRowInGBL(panelInterior, 0, 0, labelColor, jcbInteriorColor, false);
        FormUtils.addRowInGBL(panelInterior, 0, 2, spacer1, false, false);
        FormUtils.addRowInGBL(panelInterior, 0, 3, labelAlpha, jsInteriorAlpha, true);
        FormUtils.addFiller(panelInterior, 1, 0);
        panelInterior.setBorder(BorderFactory.createTitledBorder(labelInterior));
        
        JPanel panelExterior = new JPanel(new GridBagLayout());
        FormUtils.addRowInGBL(panelExterior, 0, 0, labelColor, jcbExteriorColor, false);
        FormUtils.addRowInGBL(panelExterior, 0, 2, spacer2, false, false);
        FormUtils.addRowInGBL(panelExterior, 0, 3, labelAlpha, jsExteriorAlpha, true);
        FormUtils.addFiller(panelExterior, 1, 0);
        panelExterior.setBorder(BorderFactory.createTitledBorder(labelExterior));
        
        JPanel panelIndicator = new JPanel(new GridBagLayout());
        FormUtils.addRowInGBL(panelIndicator, 0, 0, panelInterior, true, false);
        FormUtils.addRowInGBL(panelIndicator, 1, 0, panelExterior, true, false);
        FormUtils.addFiller(panelIndicator, 2, 0);
        panelIndicator.setBorder(BorderFactory.createTitledBorder(labelIndicator));
        
        return panelIndicator;
    }
    
    private JPanel getPanelDimensions() {
        String labelWidth = I18N.getString(getClass(), "width"); //$NON-NLS-1$
        String labelHeight = I18N.getString(getClass(), "height"); //$NON-NLS-1$
        String labelPixel = I18N.getString(getClass(), "pixel"); //$NON-NLS-1$
        String labelDimensions = I18N.getString(getClass(), "dimensions"); //$NON-NLS-1$
        
        JPanel panelDimension = new JPanel(new GridBagLayout());
        FormUtils.addRowInGBL(panelDimension, 0, 0, labelWidth, jsWidth, false);
        FormUtils.addRowInGBL(panelDimension, 0, 2, new JLabel(labelPixel));
        FormUtils.addRowInGBL(panelDimension, 1, 0, labelHeight, jsHeight, false);
        FormUtils.addRowInGBL(panelDimension, 1, 2, new JLabel(labelPixel));
        FormUtils.addFiller(panelDimension, 2, 0);
        panelDimension.setBorder(BorderFactory.createTitledBorder(labelDimensions));
        
        return panelDimension;
    }

    @Override
	public String getName() {
		return NAME;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public void init() {
		Blackboard pbb = PersistentBlackboardPlugIn.get(blackboard);
        int width = pbb.get(BB_KEY_MINIMAP_PANEL_WIDTH, DEFAULT_WIDTH);
		int height = pbb.get(BB_KEY_MINIMAP_PANEL_HEIGHT, DEFAULT_HEIGHT);
		Color interiorColor = (Color) pbb.get(BB_KEY_MINIMAP_INDICATOR_INTERIOR_COLOR, DEFAULT_INTERIOR_COLOR);
        double interiorAlpha = pbb.get(BB_KEY_MINIMAP_INDICATOR_INTERIOR_ALPHA, DEFAULT_INTERIOR_ALPHA);
        Color exteriorColor = (Color) pbb.get(BB_KEY_MINIMAP_INDICATOR_EXTERIOR_COLOR, DEFAULT_EXTERIOR_COLOR);
        double exteriorAlpha = pbb.get(BB_KEY_MINIMAP_INDICATOR_EXTERIOR_ALPHA, DEFAULT_EXTERIOR_ALPHA);
        boolean disableLabels = pbb.get(BB_KEY_MINIMAP_DISABLE_LABELS, DEFAULT_DISABLE_LABELS);
        
		jsWidth.setValue(width);
		jsHeight.setValue(height);
		jcbInteriorColor.setColor(interiorColor);
		jsInteriorAlpha.setValue((int) (interiorAlpha * 100));
		jcbExteriorColor.setColor(exteriorColor);
		jsExteriorAlpha.setValue((int) (exteriorAlpha * 100));
		jcbDisableLabels.setSelected(disableLabels);
	}

	@Override
	public String validateInput() {
		return null;
	}

	@Override
	public void okPressed() {
		int width = jsWidth.getIntValue();
		int height = jsHeight.getIntValue();
		Color interiorColor = jcbInteriorColor.getColor();
        int interiorAlpha = jsInteriorAlpha.getValue();
        Color exteriorColor = jcbExteriorColor.getColor();
        int exteriorAlpha = jsExteriorAlpha.getValue();
        boolean disableLabels = jcbDisableLabels.isSelected();
        
		Blackboard pbb = PersistentBlackboardPlugIn.get(blackboard);
		
		pbb.put(BB_KEY_MINIMAP_PANEL_WIDTH, width);
		pbb.put(BB_KEY_MINIMAP_PANEL_HEIGHT, height);
		pbb.put(BB_KEY_MINIMAP_INDICATOR_INTERIOR_COLOR, interiorColor);
		pbb.put(BB_KEY_MINIMAP_INDICATOR_INTERIOR_ALPHA, interiorAlpha/100f);
		pbb.put(BB_KEY_MINIMAP_INDICATOR_EXTERIOR_COLOR, exteriorColor);
        pbb.put(BB_KEY_MINIMAP_INDICATOR_EXTERIOR_ALPHA, exteriorAlpha/100f);
        pbb.put(BB_KEY_MINIMAP_DISABLE_LABELS, disableLabels);
	}
	
	public static int getMinimapPanelWidth() {
		Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
		int width = PersistentBlackboardPlugIn.get(bb).get(
		        BB_KEY_MINIMAP_PANEL_WIDTH,
		        DEFAULT_WIDTH);
		
		return width;
	}
	
	public static int getMinimapPanelHeight() {
		Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
		int height = PersistentBlackboardPlugIn.get(bb).get(
		        BB_KEY_MINIMAP_PANEL_HEIGHT,
		        DEFAULT_HEIGHT);
		
		return height;
	}
	
	public static Color getMinimapInteriorIndicatorColor() {
		Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
		Color color = (Color) PersistentBlackboardPlugIn.get(bb).get(
		        BB_KEY_MINIMAP_INDICATOR_INTERIOR_COLOR, 
		        DEFAULT_INTERIOR_COLOR);
		
		return color;
	}
	
	public static float getMinimapInteriorIndicatorAlpha() {
        Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
        double alpha = PersistentBlackboardPlugIn.get(bb).get(
                BB_KEY_MINIMAP_INDICATOR_INTERIOR_ALPHA,
                DEFAULT_INTERIOR_ALPHA);
        
        return (float)alpha;
    }
	
	public static Color getMinimapExteriorIndicatorColor() {
        Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
        Color color = (Color) PersistentBlackboardPlugIn.get(bb).get(
                BB_KEY_MINIMAP_INDICATOR_EXTERIOR_COLOR,
                DEFAULT_EXTERIOR_COLOR);
        
        return color;
    }
    
    public static float getMinimapExteriorIndicatorAlpha() {
        Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
        double alpha = PersistentBlackboardPlugIn.get(bb).get(
                BB_KEY_MINIMAP_INDICATOR_EXTERIOR_ALPHA,
                DEFAULT_EXTERIOR_ALPHA);
        
        return (float)alpha;
    }
    
    public static boolean getMinimapDisableLabels() {
        Blackboard bb = JUMPWorkbench.getFrameInstance().getContext().getBlackboard();
        boolean disableLabels = PersistentBlackboardPlugIn.get(bb).get(
                BB_KEY_MINIMAP_DISABLE_LABELS,
                DEFAULT_DISABLE_LABELS);
        
        return disableLabels;
    }
}

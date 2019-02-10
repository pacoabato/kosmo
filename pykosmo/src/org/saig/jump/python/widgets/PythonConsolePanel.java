/* 
 * Kosmo - Sistema Abierto de Información Geográfica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2010, SAIG S.L.
 *
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
 * Sistemas Abiertos de Información Geográfica, S.L.
 * Avnda. República Argentina, 28
 * Edificio Domocenter Planta 2ª Oficina 7
 * C.P.: 41930 - Bormujos (Sevilla)
 * España / Spain
 *
 * Teléfono / Phone Number
 * +34 954 788876
 * 
 * Correo electrónico / Email
 * info@saig.es
 *
 */
package org.saig.jump.python.widgets;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.saig.core.gui.swing.sldeditor.util.FormUtils;

/**
 * Panel que contiene los elementos para la consola python
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class PythonConsolePanel extends JPanel{
    private static final Logger LOGGER = Logger.getLogger(PythonConsolePanel.class);
    
    private JTextArea outputTextArea;
    private JTextArea inputTextArea;
    private JButton executeButton;
    
    private JScrollPane outputScrollPane;
    private JScrollPane inputScrollPane;
    
    PythonConsolePanel(){
        super(new GridBagLayout());
        
        initComponents();
    }

    private void initComponents(){
        outputTextArea = new JTextArea();
        inputTextArea = new JTextArea();
        executeButton = new JButton();
        
        outputTextArea.setEditable(false);
        outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        executeButton.setText("Ejecutar");
        executeButton.setToolTipText("Ctrl + Enter");
        
        setComponentsDimensions();
        
        FormUtils.addRowInGBL(this, 0, 0, outputScrollPane);
        FormUtils.addRowInGBL(this, 1, 0, inputScrollPane);
        FormUtils.addRowInGBL(this, 2, 0, executeButton);
    }
    
    private void setComponentsDimensions() {
        Dimension outputDim = new Dimension(600, 250);
        Dimension inputDim = new Dimension(600, 50);
        Dimension executeButtonDim = new Dimension(600, 35);
          
        outputScrollPane.setMinimumSize(outputDim);
        outputScrollPane.setMaximumSize(outputDim);
        outputScrollPane.setPreferredSize(outputDim);
          
        inputScrollPane.setMinimumSize(inputDim);
        inputScrollPane.setMaximumSize(inputDim);
        inputScrollPane.setPreferredSize(inputDim);
        
        executeButton.setMinimumSize(executeButtonDim);
        executeButton.setMaximumSize(executeButtonDim);
        executeButton.setPreferredSize(executeButtonDim);
    }

    public JTextArea getOutputTextArea() {
        return outputTextArea;
    }

    public JTextArea getInputTextArea() {
        return inputTextArea;
    }

    public JButton getExecuteButton() {
        return executeButton;
    }
}

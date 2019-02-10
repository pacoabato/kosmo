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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.saig.jump.python.util.PythonInterpreterManager;
import org.saig.jump.python.util.history.ConsoleHistoryProxy;

import com.vividsolutions.jump.util.StringUtil;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;

/**
 * Controlador de la consola de Python.
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class PythonConsoleController{
    
    private static final Logger LOGGER = Logger.getLogger(PythonConsoleController.class);
    
    private PythonConsoleFrame consoleFrame;
    
    public PythonConsoleController(PlugInContext context){
        
        consoleFrame = new PythonConsoleFrame(context);
        
        doListenersStuff();
        
        //muestra la consola
        consoleFrame.pack();
        context.getWorkbenchFrame().addInternalFrame(consoleFrame);
        context.getWorkbenchFrame().getDesktopPane().setLayer(consoleFrame, 100);
        GUIUtil.centreOnWindow(consoleFrame);
        
        requestFocusOnInputTextArea();
    }
    
    private void requestFocusOnInputTextArea() {
        consoleFrame.getConsolePanel().getInputTextArea().requestFocus();
    }

    /**
     * Build listeners and apply them to the console components.
     */
    private void doListenersStuff() {
        final PythonConsolePanel panel = consoleFrame.getConsolePanel();
        
        JTextArea outputTA = panel.getOutputTextArea();
        JTextArea inputTA = panel.getInputTextArea();
        JButton executeB = panel.getExecuteButton();
        
        
        inputTA.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(KeyEvent ke) {
                if(ke.getKeyChar() == '\n' && ke.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
                    doExecute();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent ke){
                if(ke.getKeyCode() == KeyEvent.VK_DOWN && ke.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
                    String nextCommand = ConsoleHistoryProxy.getConsoleHistoryController().nextCommand();
                    if(nextCommand == null){
                        writeInput("");
                    }else{
                        writeInput(nextCommand);
                    }
                }else if(ke.getKeyCode() == KeyEvent.VK_UP && ke.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
                    String prevCommand = ConsoleHistoryProxy.getConsoleHistoryController().prevCommand();
                    if(prevCommand == null){
                        writeInput("");
                    }else{
                        writeInput(prevCommand);
                    }
                }
            }
        });
        
        executeB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                doExecute();
            }
        });
    }
    
    /** Realiza las tareas para ejecutar lo que el usuario haya escrito:
     * - ejecuta las instrucciones escritas
     * - copia lo ejecutado y el resultado en output
     * - guarda en el histórico de comandos
     * - limpia el input*/
    private void doExecute(){
        //ejecutar
        PythonInterpreterManager manager = PythonInterpreterManager.getInstance();
        String[] result = manager.executeScript(readInput());
        //otras tareas
        appendInputToOutput();
        if(!StringUtil.isEmpty(result[0])){
            appendToOutput("Salida: " + result[0]);
        }
        
        if(!StringUtil.isEmpty(result[1])){
            appendToOutput("Error: " + result[1]);
        }
        
        ConsoleHistoryProxy.getConsoleHistoryController().addCommand(readInput());
        clearInput();
    }
    
    private void appendInputToOutput(){
        appendToOutput(readInput());
    }
    
    private void appendToOutput(String str){
        String output = readOutput();
        output += "\n\n";
        output += str;
        writeOutput(output);
    }
    
    private void clearInput(){
        writeInput("");
    }
    
    private void writeInput(String input){
        consoleFrame.getConsolePanel().getInputTextArea().setText(input);
    }

    private void writeOutput(String output){
        consoleFrame.getConsolePanel().getOutputTextArea().setText(output);
    }
    
    private String readInput(){
        return consoleFrame.getConsolePanel().getInputTextArea().getText();
    }
    
    private String readOutput(){
        return consoleFrame.getConsolePanel().getOutputTextArea().getText();
    }
    
    public void showConsole(){
        //TODO
    }
}

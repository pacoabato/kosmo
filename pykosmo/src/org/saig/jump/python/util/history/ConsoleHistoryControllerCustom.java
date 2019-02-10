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
package org.saig.jump.python.util.history;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejar el histórico de comandos ejecutados en la consola python. 
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class ConsoleHistoryControllerCustom implements IConsoleHistoryController{
    
    private static ConsoleHistoryControllerCustom INSTANCE;
    //TODO cargar el límite desde archivo de configuración (quizás tenga que quitarle el modificador final)
    private static final int MAX_NUMBER_OF_COMMANDS = 50;
    
    private List<String> history;
    private int next;
    
    private ConsoleHistoryControllerCustom(){
        clearHistory();
    }
    
    public static ConsoleHistoryControllerCustom getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConsoleHistoryControllerCustom();
        }
        
        return INSTANCE;
    }
    
    @Override
    public void addCommand(String command){
        //TODO tener en cuenta cuando se alcanza el máximo de comandos
        history.add(command);
        next = history.size();
    }
    
    @Override
    public String prevCommand(){
        String prevCommand = null;
        
        if(isVacio()){
            prevCommand = null;
        }else if(isPrimero()){
            prevCommand = history.get(0);
        }else{
            prevCommand = history.get(--next);
        }
        
        return prevCommand;
    }
    
    @Override
    public String nextCommand(){
        String nextCommand = null;
        
        if(isVacio() || isOutOfIndex()){
            nextCommand = null;
        }else if(isUltimo()){
            next++;
            nextCommand = null;
        }else{
            nextCommand = history.get(++next);
        }
        
        return nextCommand;
    }

    @Override
    public void clearHistory(){
        history = new ArrayList<String>(MAX_NUMBER_OF_COMMANDS);
        next = history.size();
    }
    
    @Override
    public void resetHistory(){
        next = history.size();
    }
    
    private boolean isVacio(){
        return history.isEmpty();
    }
    
    private boolean isPrimero(){
        return next == 0;
    }
    
    private boolean isOutOfIndex(){
        return next >= history.size();
    }
    
    private boolean isUltimo(){
        return next == history.size() - 1;
    }
}
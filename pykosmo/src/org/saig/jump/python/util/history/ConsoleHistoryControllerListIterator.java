/* 
 * Kosmo - Sistema Abierto de Informaci�n Geogr�fica
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
 * Sistemas Abiertos de Informaci�n Geogr�fica, S.L.
 * Avnda. Rep�blica Argentina, 28
 * Edificio Domocenter Planta 2� Oficina 7
 * C.P.: 41930 - Bormujos (Sevilla)
 * Espa�a / Spain
 *
 * Tel�fono / Phone Number
 * +34 954 788876
 * 
 * Correo electr�nico / Email
 * info@saig.es
 *
 */
package org.saig.jump.python.util.history;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Controla el hist�rico de comandos.
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class ConsoleHistoryControllerListIterator implements IConsoleHistoryController{
    
    private static final Logger LOGGER = Logger.getLogger(ConsoleHistoryControllerListIterator.class);
    
    private static ConsoleHistoryControllerListIterator INSTANCE;
    //TODO cargar el l�mite desde archivo de configuraci�n (quiz�s tenga que quitarle el modificador final)
    private static final int MAX_NUMBER_OF_COMMANDS = 50;
    

    private List<String> history;
    private ListIterator<String> iterator;
    
    private ConsoleHistoryControllerListIterator(){
        history = new ArrayList<String>(MAX_NUMBER_OF_COMMANDS);
        iterator = history.listIterator();
        resetHistory();
    }
    
    public static ConsoleHistoryControllerListIterator getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConsoleHistoryControllerListIterator();
        }
        
        return INSTANCE;
    }
    
    @Override
    public void addCommand(String command){
        //TODO tener en cuenta cuando se llegue al m�ximo de comandos
        history.add(command);
        iterator = history.listIterator();
        resetHistory();
    }

    @Override
    public void clearHistory(){
        history = new ArrayList<String>(MAX_NUMBER_OF_COMMANDS);
        iterator = history.listIterator();
    }

    @Override
    public String nextCommand(){
        String command = null;
        
        if(iterator.hasNext()){
            command = iterator.next();
        }
        
        return command; 
    }

    @Override
    public String prevCommand(){
        String command = null;
        
        if(iterator.hasPrevious()){
            command = iterator.previous();
        }
        
        return command;
    }

    @Override
    public void resetHistory(){
        //TODO buscar otra forma de hacerlo
        while(iterator.hasNext()){
            iterator.next();
        }
    }
}

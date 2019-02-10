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

/**
 * TODO Purpose of 
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class ConsoleHistoryProxy{
    private static IConsoleHistoryController controller;
    
    private static ConsoleHistoryProxy INSTANCE;
    
    private ConsoleHistoryProxy(){
        controller = ConsoleHistoryControllerCustom.getInstance();
//        controller = ConsoleHistoryControllerListIterator.getInstance();
    }
    
    private static ConsoleHistoryProxy getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConsoleHistoryProxy();
        }
        
        return INSTANCE;
    }
    
    public static IConsoleHistoryController getConsoleHistoryController(){
        return ConsoleHistoryProxy.getInstance().getController();
    }
    
    private IConsoleHistoryController getController(){
        return controller;
    }
}

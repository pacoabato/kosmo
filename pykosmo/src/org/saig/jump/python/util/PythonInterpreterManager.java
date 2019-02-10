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
package org.saig.jump.python.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import com.vividsolutions.jump.util.StringUtil;

/**
 * TODO Purpose of 
 * 
 * @author Francisco Abato Helguera - fabato@saig.es
 * @since 1.3
 */
public class PythonInterpreterManager{
    
    private static final Logger LOGGER = Logger.getLogger(PythonInterpreterManager.class);
    
    private static PythonInterpreterManager INSTANCE;
    PythonInterpreter interp;

    ByteArrayOutputStream output;
    ByteArrayOutputStream error;
    
    //TODO deber�a cerrar los stream en alg�n momento, pensar cu�ndo
    
    private PythonInterpreterManager(){
        interp = new PythonInterpreter();
        
        output = new ByteArrayOutputStream();
        error = new ByteArrayOutputStream();

        interp.setOut(output);
        interp.setErr(error);
    }
    
    public static PythonInterpreterManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PythonInterpreterManager();
        }
        
        return INSTANCE;
    }
 
    /**
     * @return String[0] es la salida est�ndar y String[1] es la salida de error tras la ejecuci�n.*/
    public String[] executeScript(File pyFile){
        
        String[] result = {null, null};
        
//      FileReader fr = null;
        
        BufferedReader br = null;
        
        try{
//          fr = new FileReader(pyFile);
//          br = new BufferedReader(fr);
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(pyFile),"UTF8"));
            
            //TODO �cerrar los InputStreamReader y FileInputStream al finalizar?
            
            String line;
            StringBuffer script = new StringBuffer();
            while((line = br.readLine()) != null) {
                int index = line.indexOf("#");
                if (index != -1){
                    line = line.substring(0, index);
                }
                
                if(StringUtils.isNotEmpty(line.trim())){
                    script.append(line);//no se puede hacer trim a line porque los espacios son importantes para python!
                    script.append("\n");
                }
            }
            
            result = executeScript(script.toString());
            
        }catch(UnsupportedEncodingException uee){
            //TODO
        }catch(FileNotFoundException fnfex){
            //TODO
        }catch(IOException ioex){
            //TODO
        }finally{
            if(br != null){
                try {
                    br.close();
                } catch (IOException ioex) {
                    LOGGER.error("Error al cerrar el BufferedReader.", ioex);
                }
            }
//          if(fr != null){
//              fr.close();
//          }
        }
        
        return result;
    }
    
    /**
     * @return String[0] es la salida est�ndar y String[1] es la salida de error tras la ejecuci�n.*/
    public String[] executeScript(String script){
        
        String[] result = {null, null};
        
        try{
            //      interp.exec("import pykosmo");
            interp.exec("from pykosmo import pyk");
            interp.exec("reload(pyk)");//TODO DESARROLLANDO: para que pille los cambios en la API, quitarlo en la versi�n final
            //TODO sacar el import a la creaci�n del manager (en producci�n no hace falta recargar el m�dulo)

      
      
//    //TODO cambiar execfile por open, compile y exec (creo que execfile est� deprecated en 3.0)
//    //si es que en la versi�n de jython sirve, porque creo que jython usa python 2.5
//    LOGGER.info(pyFile.getCanonicalPath());
//    String instr = "execfile('" + pyFile.getCanonicalPath() + "')";
//    interp.exec(instr);
//    
//    LOGGER.info("El script python " + pyFile.getName() + " termin� su ejecuci�n");
//            
      //esta forma (la que est� comentada) es m�s engorrosa y fea, pero devuelve
      //mensajes de error m�s descriptivos (con execfile en vez de la l�nea del script
      //donde falla devuelve un string con todo el script). Pero vamos, lo que hay que 
      //hacer es usar try
        
        
        
            interp.exec(script);
        }catch(Exception ex){
            LOGGER.error("", ex);
            result[1] = ex.toString();
        }finally{
            String standardOutput = output.toString();
            String errorOutput = error.toString();
            
//            LOGGER.info(standardOutput);
//            LOGGER.error(errorOutput);
            
            result[0] = standardOutput;
            
            if(!StringUtil.isEmpty(errorOutput)){
                result[1] = ( result[1] == null ? "" : result[1] ) +
                    "\n" + errorOutput;
            }
            
//            try{
//                output.flush();
//                error.flush();
                
                output.reset();
                error.reset();
//            }catch(IOException ioe){
//                LOGGER.error("", ioe);
//            }
         }

        return result;
    }
}

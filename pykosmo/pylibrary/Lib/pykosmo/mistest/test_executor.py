# This Python file uses the following encoding: utf-8

#Francisco Abato Helguera - pacoabato@gmail.com


#Estructura de los archivos de test:

# cada test debe tener una variable
# - inicializacion: cargar capas y elementos, conectar con BD, etc.
# - ejecucion del test
# - finalizacion: borrar elementos, desconectar de BD, eliminar capas, etc
# si durante la ejecucion se produce una excepcion hay que capturarla, finalizar y relanzar la excepcion
# para que test_executor la trate e informe de ella



#Para ejecutarlo desde la consola python de Kosmo:
#from pykosmo.mistest import test_executor
#test_executor.start()


import sys, os, traceback
from pykosmo import pyk
tests_path = r'D:\workspace\library\ext\Lib\pykosmo\mistest'

def buscaarchivospy(dirpath):
    '''string -> lista de string
    dirpath es la ruta completa de un directorio. Devuelve una lista cuyos elementos son 
    cadenas con la ruta completa de cada archivo con extension .py dentro del directorio
    (hace una busqueda recursiva en los subdirectorios).'''
    
    lista = []
    
    recursive(dirpath, lista)
    return lista
    
def recursive(dirpath, lista):
    for f in os.listdir(dirpath):
        fullfilepath = os.path.join(dirpath, f)
        
        if os.path.isdir(fullfilepath):
            recursive(fullfilepath, lista)
        elif  os.path.splitext(f)[1] == '.py':
            lista.append(fullfilepath)

def execute_test(file):
    try:
        execfile(file)
    except:
        #pykosmo.log('ERROR en: ' + file, logtype='ERROR')
        #pykosmo.log('en la linea: ' + sys.exc_info()[2].tb_lineno)
        #pykosmo.log(sys.exc_info()[1], logtype='ERROR')
        errormsg = '\n'.join(traceback.format_exception(*sys.exc_info())[-2:]).strip()
        errorsource = 'Error en: ' + file
        
        pyk.log(errorsource, pyk.LOG_TYPE_ERROR)
        pyk.log(errormsg, pyk.LOG_TYPE_ERROR)
        print errorsource
        print errormsg
    else:
        message = 'Ejecutado con exito: ' + file
        pyk.log(message, 'INFO')
        print message

def start():
    filesfound = buscaarchivospy(tests_path)
    filesfound.sort()
    #print str(filesfound)
    #pykosmo.log('\n'.join(filesfound))
    
    #print '\n'.join(filesfound)
    for file in filesfound:
        thename = os.path.basename(file)
        if thename != 'test_executor.py' and thename != '__init__.py':
            execute_test(file)
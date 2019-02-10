# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8

#Francisco Abato Helguera - pacoabato@gmail.com
#v 0.3.5  -  20090814
#v 0.3.6  -  20100302 - Añadida class GenericPlugInActivator
#v 0.3.7  -  20100507 - nuevos métodos genéricos de Kosmo (getLayerManater, etc.)
#v 0.3.8  -  20100510  - nuevos métodos geométricos, refactor métodos de PKEnvelope
#v 0.3.8.1 - 20100713  - get_area() -> area() ; get_length() -> length()
#                                - correcciones PKSchema
#                                - Layers.add_new_layer devuelve la capa recién creada
#                                - comentarios
#                                - PKFeature: __str__  y save (addWithNewKey en vez de add)
#v 0.3.8.2 - 20100721  - get_selection_manager y PKLayer.get_selected_features
#v 0.3.8.3 - 20100721  - KosmoUtils
#v 0.4      - 20100722  - Estructurado en módulos (todavía todo en un único archivo)
#v 0.4.1    - 20100729  - Tables.get_table convertido en estático
#                               - Adaptado Tables.load_datasource_table al nuevo core (DataFrame -> ViewTableFrame)
#                               - Modificado el acceso a tablas (tablerecorddatasource, iteradores, etc)
#v 0.4.2    - 20100730  - Cambiado PKRecord para que depende de DbDataSource en vez de PKTable
#              - 20100807 - Añadido about() y version()
#v 0.4.3    - 20100812  - Añadido PKLayer.__str__()
#v 0.4.4    - 20100812  - Dividido en módulos. Módulo principal pykosmo.py -> pyk.py

from com.vividsolutions.jump.workbench import JUMPWorkbench
from org.saig.jump.widgets.util import DialogFactory

from org.apache.log4j import Logger
from com.vividsolutions.jump.feature import BasicFeature
from org.saig.core.model.data import Record







from org.saig.jump.plugin.check import ActivateOnlySpecificPluginsEnableCheck

import os

import meta
import layers
import tables
import geomutils
#aquí se importan los demás paquetes de pykosmo, de forma que para usarlos hay que hacer pykosmo.paquete, e.g. pykosmo.Layers.get_all_layers()
#El usuario puede hacer "from pykosmo import *" para poder usar los paquetes diréctamente, e.g. Layers.get_all_layers()


#TODO quitar los reload cuando salga a producción (no será necesario andar recargando los módulos por si hay cambios, porque no los habrá)
reload(meta)
reload(layers)
reload(tables)
reload(geomutils)


LOG_TYPE_INFO = 'INFO'
LOG_TYPE_WARN = 'WARN'
LOG_TYPE_DEBUG = 'DEBUG'
LOG_TYPE_ERROR = 'ERROR'


# TODO métodos para cargar y guardar proyectos


def log(msg, logtype = LOG_TYPE_INFO):
    '''Los log estan redirigidos a donde lo esten en Kosmo.'''
    if logtype == LOG_TYPE_INFO:
        _kosmologger.info(msg)
    elif logtype == LOG_TYPE_WARN:
        _kosmologger.warn(msg)
    elif logtype == LOG_TYPE_DEBUG:
        _kosmologger.debug(msg)
    elif logtype == LOG_TYPE_ERROR:
        _kosmologger.error(msg)
    #TODO pasarle una excepción y que imprima la pila (no sé si se puede)


def get_task_manager():
    return JUMPWorkbench.getFrameInstance().getContext().getTaskManager()

def get_task(taskname):
    return get_task_manager().getTask(taskname)

def get_context():
    return JUMPWorkbench.getFrameInstance().getContext()

def get_layer_manager():
    '''Devuelve el objeto LayerManager de la ventana interna de Kosmo que esté activa en este momento.
    Si no hay ventana interna activa o ésta no implementa LayerManagerProxy devuelve None.'''
    return get_context().getLayerManager()

def get_layer_view_panel():
    '''Devuelve el objeto LayerViewPanel de la ventana interna de Kosmo que esté activa en este momento.
    Si no hay ventana interna activa o ésta no implementa LayerViewPanelProxy devuelve None.'''
    return get_context().getLayerViewPanel()

def get_layer_name_panel():
    '''Devuelve el objeto LayerNamePanel de la ventana interna de Kosmo que esté activa en este momento.
    Si no hay ventana interna activa o ésta no implementa LayerViewPanelProxy devuelve None.'''
    return get_context().getLayerNamePanel()

def get_selection_manager():
    '''Devuelve el objeto SelectionManager de la ventana interna de Kosmo que esté activa en este momento.
    Si no hay ventana interna activa o ésta no implementa LayerViewPanelProxy devuelve None.'''
    return get_layer_view_panel().getSelectionManager()

class GenericPlugInActivator:
    '''Clase de utilidades para activar/desactivar herramientas de Kosmo'''
    def activate_exclusive(plugins):
        '''plugins es una lista de cadenas o una cadena. Cada cadena es el nombre
        de un plugin de Kosmo. Activa los plugins indicados por sus nombres y desactiva el resto de los
        plugins de Kosmo'''
        
        activator = ActivateOnlySpecificPluginsEnableCheck.getInstance()
        activator.clearActivePlugins()
        GenericPlugInActivator.activate(plugins)
    
    activate_exclusive = staticmethod(activate_exclusive)
    
    def activate(plugins):
        '''plugins es una lista de cadenas o una cadena. Cada cadena es el nombre
        de un plugin de Kosmo. Activa los plugins indicados por sus nombres.'''
        activator = ActivateOnlySpecificPluginsEnableCheck.getInstance()
        
        if type(plugins) == type([]):
            for plugin in plugins:
                activator.activatePlugIn(plugin)
        elif type(plugins) == type(''):
            activator.activatePlugIn(plugins)
        
        JUMPWorkbench.removeGenericCheck(activator)
        JUMPWorkbench.addGenericCheck(activator)
        JUMPWorkbench.getFrameInstance().updateAllToolbars()
    
    activate = staticmethod(activate)
    
    def deactivate(plugins):
        '''plugins es una lista de cadenas o una cadena. Cada cadena es el nombre
        de un plugin de Kosmo. Desactiva los plugins indicados por sus nombres.'''
        activator = ActivateOnlySpecificPluginsEnableCheck.getInstance()
        
        if type(plugins) == type([]):
            for plugin in plugins:
                activator.deactivatePlugIn(plugin)
        elif type(plugins) == type(''):
            activator.deactivatePlugIn(plugins)
        JUMPWorkbench.getFrameInstance().updateAllToolbars()
    
    deactivate = staticmethod(deactivate)


class Dialogs:
    def show_info_dialog(message, title='Information', parent=JUMPWorkbench.getFrameInstance()):
        DialogFactory.showInformationDialog(parent, message, title)
    
    show_info_dialog = staticmethod(show_info_dialog)
    
    def show_input_dialog(message, title='Input', initialValue='', parent=JUMPWorkbench.getFrameInstance()):
        ''' -> string (None si se pulso Cancelar'''
        return DialogFactory.showInputDialog(parent, message, title, initialValue)
    
    show_input_dialog = staticmethod(show_input_dialog)
    
    def show_selection_dialog(message, choices, defaultValue, title='Selection', parent=JUMPWorkbench.getFrameInstance()):
        ''' choices is a list
        -> the element of choices selected by the user or None if clicked Cancel.'''
        return DialogFactory.showSelectionDialog(parent, message, title, choices, defaultValue)
    
    show_selection_dialog = staticmethod(show_selection_dialog)
    
    # TODO: error, warning, yesNo, yesNoCancel, yesNoCancelWarning ???


_kosmologger = Logger.getLogger('org.saig.jump.plugin.python.PythonInterpreterPlugIn')

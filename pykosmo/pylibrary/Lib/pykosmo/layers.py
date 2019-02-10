# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8

import pyk
from com.vividsolutions.jump.workbench import JUMPWorkbench
from com.vividsolutions.jump.workbench.datasource import LoadDatasetPlugIn
from com.vividsolutions.jump.workbench.ui.plugin import AddNewLayerPlugIn
from com.vividsolutions.jump.workbench.ui.task import TaskMonitorManager
from org.saig.jump.plugin.datasource import IndexedShapeFileDataSource
from com.vividsolutions.jump.io.datasource import DataSource
from com.vividsolutions.jump.io.datasource import DataSourceQuery
from com.vividsolutions.jump.task import DummyTaskMonitor
from com.vividsolutions.jump.workbench.model import StandardCategoryNames
from com.vividsolutions.jump.feature import FeatureSchema
from com.vividsolutions.jts.geom import Envelope
from com.vividsolutions.jump.feature import AttributeType
from com.vividsolutions.jump.feature import FeatureDataset
from com.vividsolutions.jump.workbench.model import LayerEventType

import os

#class Layers:
def get_layer(name):
    '''string -> pklayer
    El parametro es el nombre de una capa, devuelve el objeto PKLayer correspondiente.
    Si no hay una capa con ese nombre devuelve None.
    La capa se busca en la ventana interna activa en Kosmo en ese momento.'''
    if name:
        klayer = JUMPWorkbench.getLayer(str(name))
        if klayer: #TODO ¿qué hace si klayer es None? Poner un return explícito para ese caso
            return PKLayer(klayer)
    else:
        return None

#get_layer = staticmethod(get_layer)

def get_all_layers():
    ''' -> lista de PKLayers.
    Devuelve una lista con todas las capas cargadas en Kosmo. Si no hay capas
    devuelve una lista vacía (nunca devuelve None).
    La capa se busca en la ventana interna activa en Kosmo en ese momento.'''
    layerManager = pyk.get_layer_manager()
    if(layerManager is None):
        return []
    return [PKLayer(klayer) for klayer in
        layerManager.getAllLayers()]

#get_all_layers = staticmethod(get_all_layers)

def remove_layer(layername):
    '''Elimina la capa con el nombre igual al parámetro. Si no existe no hace nada.
    La capa se busca en la ventana interna activa en Kosmo en ese momento.'''
    klayer = JUMPWorkbench.getLayer(str(layername))
    if klayer:
        pyk.get_layer_manager().remove(klayer)
    else:
        pyk.log('No se encontró la capa: ' + str(layername), pyk.LOG_TYPE_ERROR)

#remove_layer = staticmethod(remove_layer)


#TODO si layername o categoryname tienen caracteres no ASCII en Kosmo no salen bien.
#TODO mirar KosmoDesktopUtils
def add_new_layer(geometrytype, layername='Nueva capa', categoryname=None,  schema=None):
    '''geometrytype es una de las constantes definidas en PKSchema:
    0 -> Desconocida
    1 -> Punto
    3 -> Linea
    5 -> Poligono
    Estas constantes están en PKSchema (GEOMETRY_TYPE_UNKNOWN, etc.)
    schema es un PKSchema
    Aunque se indique un schema, el tipo de geometria de la nueva capa sera el indicado
    en el parámetro geometrytype (y el schema que se pasa como parámetro quedará modificado).
    Devuelve un nuevo PKLayer con la capa (de kosmo) recién creada.'''
    context = pyk.get_context().createPlugInContext()
    projection = context.getTask().getProjection()
    if not categoryname:
        categoryname = LoadDatasetPlugIn.chooseCategory(context)
    
    fc = None
    if not schema:
        fc = AddNewLayerPlugIn.createBlankFeatureCollection(geometrytype)
    else:
        schema.set_geometry_type(geometrytype)
        fc = FeatureDataset(schema.kschema)
    
    newlayer = context.addLayer(categoryname, layername, fc)
    newlayer.setProjection(projection)
    newlayer.setFeatureCollectionModified(False)
    
    return PKLayer(newlayer)

#add_new_layer = staticmethod(add_new_layer)

def add_new_layer_gui():
    '''Muestra el dialogo para anadir una nueva capa''' 
    context = pyk.get_context().createPlugInContext()
    plugin = AddNewLayerPlugIn()
    plugin.execute(context)

#add_new_layer_gui = staticmethod(add_new_layer_gui)

def load_layer_gui():
    '''Muestra el dialogo para anadir una capa existente(shape, BD, imagen, etc.)'''
    context = pyk.get_context().createPlugInContext()
    plugin = LoadDatasetPlugIn()
    plugin.initialize(context)
    okpressed = plugin.execute(context)
    
    if okpressed:
        tmm = TaskMonitorManager()
        tmm.execute(plugin, context)    # ejecuta el metodo run del plugin

#load_layer_gui = staticmethod(load_layer_gui)

def load_shape_file(filepath, encoding='UTF-8'):
    name = os.path.basename(filepath) # el nombre del archivo, sin la ruta
    name = os.path.splitext(name)[0] # el nombre del archivo, sin la extension
    
    datasource = IndexedShapeFileDataSource()
    properties = {DataSource.FILE_KEY:filepath, DataSource.SELECTED_CHARSET_KEY:encoding}
    datasource.setProperties(properties)
    
    datasourcequery = DataSourceQuery(datasource, \
        filepath, \
        name)
    
    context = pyk.get_context().createPlugInContext()
    
    monitor = DummyTaskMonitor()
    
    category = StandardCategoryNames.WORKING
    if len(context.getLayerNamePanel().getSelectedCategories()) > 0:
        category = context.getLayerNamePanel().getSelectedCategories().iterator().next().toString()
        
    LoadDatasetPlugIn.loadDataSourceQueryToLayer( \
        monitor, 
        context,
        datasourcequery,
        category)

#load_shape_file = staticmethod(load_shape_file)

def get_selected_layers():
    ''' -> lista de PKlayer
    Devuelve las capas seleccionadas en la vista activa o una lista vacía.'''
    layerNamePanel = get_layer_name_panel()
    if layerNamePanel is not None:
        klayers = layerNamePanel.getSelectedLayers()
        pklayers = [PKLayer(klayer) for klayer in klayers]
        return  pklayers
    else:
        return []

#get_selected_layers = staticmethod(get_selected_layers)

#TODO load_dbf_layer, load_postgis_layer, etc ???

#class Categories:
    #  TODO  addCategory, removeCategory


class PKSchema:
    GEOMETRY_TYPE_UNKNOWN = 0
    GEOMETRY_TYPE_POINT = 1
    GEOMETRY_TYPE_MULTILINESTRING = 2
    GEOMETRY_TYPE_LINE = 3
    GEOMETRY_TYPE_MULTIPOLYGON = 4
    GEOMETRY_TYPE_POLYGON = 5
    GEOMETRY_TYPE_MULTIPOINT = 8
    GEOMETRY_TYPE_ARC = 9
    GEOMETRY_TYPE_CIRCLE = 10
    GEOMETRY_TYPE_ELLIPSE = 11
    GEOMETRY_TYPE_MULTIPLE = 15
    
    ATTRIBUTE_TYPE_STRING = 'STRING'
    ATTRIBUTE_TYPE_CHAR = 'CHAR'
    ATTRIBUTE_TYPE_VARCHAR = 'VARCHAR'
    ATTRIBUTE_TYPE_LONGVARCHAR = 'LONGVARCHAR'
    ATTRIBUTE_TYPE_BIT = 'BIT'
    ATTRIBUTE_TYPE_BOOLEAN = 'BOOLEAN'
    ATTRIBUTE_TYPE_TINYINT = 'TINYINT'
    ATTRIBUTE_TYPE_SMALLINT = 'SMALLINT'
    ATTRIBUTE_TYPE_INTEGER = 'INTEGER'
    ATTRIBUTE_TYPE_LONG = 'LONG'
    ATTRIBUTE_TYPE_BIGINT = 'BIGINT'
    ATTRIBUTE_TYPE_DECIMAL = 'DECIMAL'
    ATTRIBUTE_TYPE_NUMERIC = 'NUMERIC'
    ATTRIBUTE_TYPE_BIGDECIMAL = 'BIGDECIMAL'
    ATTRIBUTE_TYPE_FLOAT = 'FLOAT'
    ATTRIBUTE_TYPE_DOUBLE = 'DOUBLE'
    ATTRIBUTE_TYPE_REAL = 'REAL'
    ATTRIBUTE_TYPE_DATE = 'DATE'
    ATTRIBUTE_TYPE_TIME = 'TIME'
    ATTRIBUTE_TYPE_TIMESTAMP = 'TIMESTAMP'
    ATTRIBUTE_TYPE_GEOMETRY = 'GEOMETRY'
    ATTRIBUTE_TYPE_OBJECT = 'OBJECT'
    
    def __init__(self, kschema=None):
        '''kschema es de tipo com.vividsolutions.jump.feature.FeatureSchema'''
        if kschema:
            self.kschema = kschema
        else:
            self.kschema = FeatureSchema()
    
    def get_geometry_type(self, pkfeature):
        '''PKFeature -> int
        Devuelve el tipo de la geometria como una de las constantes de PKSchema.'''
        return self.kschema.getGeometryType()
    
    def set_geometry_type(self, geometrytype):
        '''geometrytype es una de las constantes de PKSchema'''
        self.kschema.setGeometryType(geometrytype)
    
    def add_attribute(self, attrname, attrtype, primarykey=False):
        '''string, string [, boolean] -> None
        attrtype es una de las constantes de PKSchema'''
        self.kschema.addAttribute(attrname, AttributeType.toAttributeType(attrtype), primarykey)
    
    def get_attribute_type(self, attrname):
        return self.kschema.getAttributeType(attrname)
    
    def get_attributes_names(self):
        '''-> list(string)'''
        return self.kschema.getAttributeNames()
    
    def has_attribute(self, attrname):
        '''string -> boolean'''
        return self.kschema.getAttribute(attrname) != None
    
    def clone(self):
        return PKSchema(self.kschema.clone())
    
    def __eq__(self, other, ordermatters=False):
        return type(self) == type(other) and self.kschema.equals(other.kschema, ordermatters)
    
    def __len__(self):
        return self.kschema.getAttributeCount()
    #TODO getPrimaryKeyName, etc

class PKLayer:
    def __init__(self, klayer):
        '''klayer es de tipo com.vividsolutions.jump.workbench.model.Layer'''
        if klayer is None:    # TODO comprobar que es de tipo com.vividsolutions.jump.workbench.model.Layer
            raise TypeExeption('No se pudo crear un layer, el tipo del argumento no era com.vividsolutions.jump.workbench.model.Layer')
        self.klayer = klayer
    
    def get_by_primary_key(self, pk):
        return self.klayer.getUltimateFeatureCollectionWrapper().getByPrimaryKey(pk)
    
    def get_by_attributes(self, attrnames, attrvalues=None):
        '''list(string), list(object) -> list(PKFeature)
        o tambien: map(string:object) -> list(PKFeature)'''
        
        if type(attrnames) == type({}):
            names = attrnames.keys()
            values = attrnames.values()
            kfeatures = self.klayer.getUltimateFeatureCollectionWrapper().getByAttribute(names, values)
        else:
            kfeatures = self.klayer.getUltimateFeatureCollectionWrapper().getByAttribute(attrnames, attrvalues)
        pkfeatures = [PKFeature(self.klayer, kfeature) for kfeature in kfeatures]
        return pkfeatures
    
    def get_name(self):
        return self.klayer.getName()
    
    def get_by_envelope(self, bbox):
        '''bbox es un PKEnvelope o una tupla (xmin, ymin, xmax, ymax) -> lista de PKFeature
        cuyos envelopes intersectan con bbox'''
        kenvelope = None
        if type(bbox) == type(()):
            kenvelope = Envelope(bbox[0], bbox[2], bbox[1], bbox[3])  #  Envelope(xmin, xmax, ymin, ymax)
        else:
            kenvelope = bbox.kenvelope
        kfeatures = self.klayer.getUltimateFeatureCollectionWrapper().query(kenvelope)
        pkfeatures = [PKFeature(self.klayer, kfeature) for kfeature in kfeatures]
        return pkfeatures
    
    def get_features(self):
        ''' -> lista con todas las pkfeatures. Puede estar vacia pero nunca devuelve None'''
        #pkfeatures = []
        #for kfeature in self.klayer.getUltimateFeatureCollectionWrapper().getFeatures():
        #    pkfeatures.append(PKFeature(self.klayer, kfeature))
        #return pkfeatures
        return [PKFeature(self.klayer, kfeature) for kfeature in 
            self.klayer.getUltimateFeatureCollectionWrapper().getFeatures()]
    
    def get_selected_features(self):
        '''Devuelve una lista con los PKFeature seleccionados de la capa.'''
        #return get_selection_manager().getFeaturesWithSelectedItems(self.klayer)
        return [PKFeature(self.klayer, kfeature) for kfeature in
            pyk.get_selection_manager().getFeaturesWithSelectedItems(self.klayer)]
    
    def get_schema(self):
        return PKSchema(self.klayer.getFeatureSchema())
    
    def commit(self):
        self.klayer.getUltimateFeatureCollectionWrapper().commit()
        self.refresh()
    
    def rollback(self):
        self.klayer.getUltimateFeatureCollectionWrapper().rollBack()
    
    def refresh(self):
        '''Refresca la capa a nivel de vista.'''
        self.klayer.fireAppearanceChanged()
        self.klayer.fireLayerChanged(LayerEventType.COMMITED)
    
    def set_visible(self, visible):
        self.klayer.setVisible(visible)
    
    def set_editable(self, editable):
        self.klayer.setEditable(editable)
    
    def is_visible(self):
        return self.klayer.isVisible()
    
    def is_editable(self):
        return self.klayer.isEditable()
    
    def is_raster(self):
        return self.klayer.isRaster()
    
    def __eq__(self, other):
        return type(self) == type(other) and self.klayer.equals(other.klayer)
        #  la comprobacion de tipo tambien comprueba que other no sea None (self nunca lo es)
    
    def __len__(self):
        return self.klayer.getUltimateFeatureCollectionWrapper().size()
    
    def __str__(self):
        return 'PKLayer: ' + str(self.get_name())
# TODO: implementar __iter__,  __contains__, y quizas otros

class PKFeature:    
    _STATUS_NOT_REMOVED = 0
    _STATUS_TO_BE_REMOVED = 1
    _STATUS_REMOVED = 2
    
    def __init__(self, layer, kfeature=None):
        '''kfeature es de tipo com.vividsolutions.jump.feature.Feature
        layer es la capa en la que esta feature. Es de tipo Layer de Kosmo o PKLayer.
        Si kfeature es None se crea un nuevo feature'''
        if layer is None:    # comprobar que feature es de tipo com.vividsolutions.jump.feature.Feature, layer del correspondiente
            raise TypeException('No se pudo crear un feature, el tipo del argumento no era com.vividsolutions.jump.feature.Feature')
        
        if isinstance(layer, PKLayer):
            self.klayer = layer.klayer
        else:  #  supone que es de tipo Layer de Kosmo
            self.klayer = layer
            # TODO comprobar que el tipo es Layer y en caso de no ser ninguno de los dos lanzar excepcion
            
        #if str(type(layer)) == "<type 'com.vividsolutions.jump.workbench.model.Layer'>": # TODO comprobar el tipo de forma más elegante
        #    self.klayer = layer
        #else:    #  supone que es de tipo PKLayer
        #    self.klayer = layer.klayer
        # TODO comprobar que el tipo es PKLayer y en caso de no ser ninguno de los dos lanzar excepcion
        
        if kfeature:
            self.kfeature = kfeature
            self.isnewfeature = False #indica que la feature ya se añadió a la capa
        else:
            self.kfeature = BasicFeature(self.klayer.getFeatureSchema())
            self.isnewfeature = True #indica que la feature aún no se ha añadido a la capa
            
        self.status = PKFeature._STATUS_NOT_REMOVED
    
    def get_attribute(self, attrname):
        '''string -> object
        Devuelve el valor del atributo attrname de feature'''
        #if self.has_attribute(attrname):
        return self.kfeature.getAttribute(attrname)
    
    def set_attribute(self, attrname, attrvalue):
        '''Devuelve self para poder hacer llamadas encadenadas:
        unrecord.set_attribute('name', 'Bruce').set_attribute('apellido', 'Wayne')'''
        if (#self.has_attribute(attrname) and
            self.status not in (PKFeature._STATUS_TO_BE_REMOVED, PKFeature._STATUS_REMOVED)):
            
            self.kfeature.setAttribute(attrname, attrvalue)
            return self

    def has_attribute(self, attrname):
        return self.kfeature.getFeatureSchema().hasAttribute(attrname)
    
    def get_schema(self):
        return PKSchema(self.kfeature.getSchema())
    
    def get_geometry(self):
        return self.kfeature.getGeometry()
    
    def set_geometry(self, geometry):
        if (self.kfeature is not None and 
            self.status not in (PKFeature._STATUS_TO_BE_REMOVED, PKFeature._STATUS_REMOVED)):
            
            self.kfeature.setGeometry(geometry)
    
    def save(self, docommit=True):
        '''Guarda la feature con los cambios que pudiese tener. Puede lanzar excepciones.
        Es importante tener en cuenta que se guardan todos los features modificados, nuevos y/o 
        borrados de la capa, no solo el que llama a save.
        Es decir, si docommit es True se guardarán en el origen de datos todas las features que
        hayan hecho save(False) con anterioridad.
        Si docommit es False, se actualiza el estado de la feature pero no se hace commit en el origen 
        de datos; ademas de ser mucho mas eficiente es util por ejemplo cuando hay reglas topologicas, en cuyo caso el commit debe
        ser simultaneo para todas las feature (pero el estado de las feature debe actualizarse individualmente
        de todos modos):
        feat1.set_attribute('attr1', 'value1')
        feat1.save(False)    #actualiza el estado de feat1 pero no hace commit
        feat2.set_attribute('attr2', 'value2')
        feat2.save(False)    #idem
        feat3.remove()
        feat3.save(False)    #idem
        layer.commit()
        
        OJITO con Shapefile: si se van a borrar elementos hay que usar save(False) y hacer
        commit al final:
        featurestoremove = layer.get_features()
        for feature in featurestoremove:
            feature.remove()
            feature.save(False)
        layer.commit()
        '''
        if self.status == PKFeature._STATUS_REMOVED:
            raise Exception('La feature que ha intentado guardar habia sido eliminada previamente')
        
        try:
            if self.status == PKFeature._STATUS_TO_BE_REMOVED:
                self.klayer.getUltimateFeatureCollectionWrapper().remove(self.kfeature)
                self.status = PKFeature._STATUS_REMOVED
            elif self.isnewfeature:
                #TODO si ya tiene una PK quizás habría que usar add() en vez de addWithNewKey()
                self.klayer.getUltimateFeatureCollectionWrapper().addWithNewKey(self.kfeature)
                self.isnewfeature = False
            else:
                self.klayer.getUltimateFeatureCollectionWrapper().update(self.kfeature)
            
            if docommit:
                self.klayer.getUltimateFeatureCollectionWrapper().commit()
                PKLayer(self.klayer).refresh()
        except Exception:
            self.klayer.getUltimateFeatureCollectionWrapper().rollBack()
            raise
    
    def remove(self):
        '''Elimina la feature. Puede lanzar excepciones.'''
        if self.status not in (PKFeature._STATUS_TO_BE_REMOVED, PKFeature._STATUS_REMOVED): 
            self.status = PKFeature._STATUS_TO_BE_REMOVED
        #TODO el remove() debería eliminar diréctamente (sin tener que hacer luego save()). Se puede usar un parámetro
        #para indicar que no se borre de verdad (como en save(docommit))
        #Creo que el remove() debería hacer el featurecollection.remove(feat) pero el commit habría que hacerlo en el save
    
    def get_layer(self):
        return PKLayer(self.klayer)
    
    def envelope(self):
        '''-> PKEnvelope (nullenvelope si la geometria esta vacia)'''
        return PKEnvelope(self.get_geometry().getEnvelopeInternal())
    
    def boundingbox(self):
        '''Devuelve una tupla con (xmin, ymin, xmax, ymax)'''
        pkenvelope = self.envelope()
        if pkenvelope.is_null():
            return (0, 0, 0, 0)
        else:
            return (pkenvelope.bottom(), #min_x
                pkenvelope.left(), #min_y
                pkenvelope.top(), #max_x
                pkenvelope.right()) #max_y
    
    def area(self):
        return self.kfeature.getGeometry().getArea()
    
    def length(self):
        return self.kfeature.getGeometry().getLength()
    
    def distance(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().distance(other.get_geometry())
    
    def intersects(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
#        if type(self) != type(other) or self.get_geometry() == None or  \
#            other.get_geometry() == None:
#            return None
        return self.get_geometry().intersects(other.get_geometry())
    
    def intersection(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().intersection(other.get_geometry())
    
    def contains(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().contains(other.get_geometry())
    
    def covered_by(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().coveredBy(other.get_geometry())
    
    def covers(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().covers(other.get_geometry())
    
    def crosses(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().crosses(other.get_geometry())
    
    def difference(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().difference(other.get_geometry())
    
    def disjoint(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().disjoint(other.get_geometry())
    
    def centroid(self, other):
        '''Si other no es un PKFeature o alguna de las dos geometrías es 
        None se producirá una excepción'''
        return self.get_geometry().getCentroid(other.get_geometry())
    
    def coordinates(self):
        '''-> una lista de objetos PKCoordinate o vacía.'''
        coordinates = self.get_geometry().getCoordinates()
        return [PKCoordinate(coordinate) for coordinate in coordinates]
    
    #TODO getGeometryN, etc.
    
    
    def equals(self, other):
        '''Si other no es un PKFeature devuelve false'''
        return type(self) == type(other) and self.kfeature.equals(other.kfeature)
        #si other es None la comprobación de tipo devolverá false
        
    def __getitem__(self, key):
        return self.get_attribute(key)

    def __setitem__(self, key, value):
        return self.set_attribute(key, value)

    def __eq__(self, other):
        return self.equals(other)
    
    def __str__(self):
        return str(self.kfeature.toString())
    
    #TODO ¿implementar __contains__ y otros?

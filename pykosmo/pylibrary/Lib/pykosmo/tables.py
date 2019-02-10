# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8

from pykosmo.layers import PKSchema #porque PKRecord también usa PKSchema


#class Tables:

# TODO: cargar tablas .mdb

def load_datasource_table(datasource):
    '''TableRecordDataSource -> None
    Carga en Kosmo una tabla con el origen de datos datasource.'''
    #from org.saig.core.model.data import TableFactory
    from org.saig.core.model.data.widgets import ViewTableFrame
    from org.saig.core.model.data import Table
    
    #table = TableFactory.getRecordCollection(datasource)
    table = Table(datasource)
    
    dataManager = get_context().getDataManager()
    pluginContext = JUMPWorkbench.getFrameInstance().getContext().createPlugInContext()
    viewTableFrame = ViewTableFrame(table, pluginContext)
    dataManager.addTable(viewTableFrame)

#load_datasource_table = staticmethod(load_datasource_table)

def load_dbf_table(filepath, charset='UTF-8'):
    '''Carga en Kosmo una tabla con origen en un archivo dbf'''
    datasource = Tables.build_dbf_datasource(filepath, charset)
    Tables.load_datasource_table(datasource)

#load_dbf_table = staticmethod(load_dbf_table)

def build_dbf_datasource(filepath, charset='UTF-8'):
    ''' -> org.saig.core.model.data.dao.dbf.DBFRecordDataSource'''
    from java.nio.charset import Charset
    from org.saig.core.model.data.dao.dbf import DBFRecordDataSource
    from org.saig.core.model.data import TableFactory
    import os
    
    selectedCharset = Charset.forName(charset)
    datasource = DBFRecordDataSource(filepath, None, selectedCharset)
    name = os.path.basename(filepath) # el nombre del archivo, sin la ruta
    name = os.path.splitext(name)[0] # el nombre del archivo, sin la extension
    datasource.setName(name)
    return datasource
    #TODO adaptar este a DbDataSource como se ha hecho con postgresql, etc)

#build_dbf_datasource = staticmethod(build_dbf_datasource)

def load_postgresql_table(host, dbname, tablename, pkname, username, password, port=5432):
    '''Carga en Kosmo una tabla con origen en una BD PostgreSQL.'''
    datasource = Tables.build_postgresql_datasource(host, dbname, tablename, pkname, username, password, port)
    Tables.load_datasource_table(datasource)

#load_postgresql_table = staticmethod(load_postgresql_table)

def build_postgresql_datasource(host, dbname, tablename, pkname, username, password, port=5432):
    ''' -> DbDataSource (con un org.saig.core.model.data.dao.jdbc.PostgreSQLDataSource)'''
    from org.saig.core.model.data.dao.jdbc import PostgreSQLDataSource
    datasource = PostgreSQLDataSource(host, port, dbname, username, password)
    datasource = DbDataSource(datasource)
    datasource.setup(tablename, pkname)
    return datasource

#build_postgresql_datasource = staticmethod(build_postgresql_datasource)

def load_mysql_table(host, dbname, tablename, pkname, username, password, port=3306):
    '''Carga en Kosmo una tabla con origen en una BD MySQL.'''
    datasource = Tables.build_mysql_table(host, dbname, tablename, pkname, username, password, port)
    Tables.load_datasource_table(datasource)

#load_mysql_table = staticmethod(load_mysql_table)

def build_mysql_datasource(host, dbname, tablename, pkname, username, password, port=3306):
    ''' -> DbDataSource (con un org.saig.core.model.data.dao.jdbc.MySQLDataSource)'''
    from org.saig.core.model.data.dao.jdbc import MySQLDataSource
    datasource = MySQLDataSource(host, port, dbname, username, password)
    datasource = DbDataSource(datasource)
    datasource.setup(tablename, pkname)
    return datasource

#build_mysql_datasource = staticmethod(build_mysql_datasource)

def load_oracle_table(host, dbname, tablename, pkname, username, password, port=1521):
    '''Carga en Kosmo una tabla con origen en una BD Oracle.'''
    datasource = Tables.build_oracle_datasource(host, dbname, tablename, pkname, username, password, port)
    Tables.load_datasource_table(datasource)
    
#load_oracle_table = staticmethod(load_oracle_table)

def build_oracle_datasource(host, dbname, tablename, pkname, username, password, port=1521):
    ''' -> DbDataSource (con un org.saig.core.model.data.dao.jdbc.OracleDataSource)'''
    from org.saig.core.model.data.dao.jdbc import OracleDataSource
    datasource = OracleDataSource(host, port, dbname, username, password)
    datasource = DbDataSource(datasource)
    datasource.setup(tablename, pkname)
    return datasource

#build_oracle_datasource = staticmethod(build_oracle_datasource)

def get_table(tablename):
    '''string -> PKTable
    Devuelve la tabla cargada en Kosmo de nombre tablename o None si no no hay una tabla
    cargada con ese nombre'''
    ktable = JUMPWorkbench.getTable(tablename)
    if ktable:
        return PKTable(ktable)

#get_table = staticmethod(get_table)

#  TODO removeTable




class PKTable:
    '''Clase que da acceso a las tablas cargadas en Kosmo.'''
    def __init__(self, ktable):
        '''ktable es de tipo org.saig.core.model.data.Table'''
        if ktable is None:    # TODO comprobar que es de tipo org.saig.core.model.data.Table
            raise TypeExeption('No se pudo crear una tabla, el tipo del argumento no era org.saig.core.model.data.Table')
        self.ktable = ktable
    
    def get_by_primary_key(self, pks):
        '''pks es el nombre de la pk o una lista de nombres de las primary keys. Siempre devuelve una lista (que puede ser vacía pero
        nunca None)'''
        if type(pks) == type(''):
            return [PKRecord(self.ktable.getByPrimaryKey(pks))]
        elif type(pks) == type([]):
            krecords = self.ktable.getByPrimaryKeys(pks)
            return [PKRecord(krecord) for krecord in krecords]
        return []
    
    def get_by_attributes(self, attrnames, attrvalues=None, orderfield=None, ascending=True):
        '''list(string), list(object) -> list(PKRecord)
        o tambien: map(string:object) -> list(PKRecord)'''
        if type(attrnames) == type({}):
            names = attrnames.keys()
            values = attrnames.values()
            krecords = self.ktable.getByAttribute(names, values, orderfield, None, ascending)
        else:
            krecords = self.ktable.getByAttribute(attrnames, attrvalues, orderfield, None, ascending)
        pkrecords = [PKRecord(self.ktable, krecord) for krecord in krecords]
        return pkrecords
    
    def get_name(self):
        return self.ktable.getName()
    
    def get_records(self, fieldordered=None, ascending=True):
        return [PKRecord(self.ktable, krecord) for krecord in 
            self.ktable.getRecords(fieldordered, None, ascending)]
    
    def get_record(self, index):
        '''index es un entero'''
        return PKRecord(self.ktable, self.ktable.getRecord(index))
    
    def get_schema(self):
        return PKSchema(self.ktable.getSchema())
    
    def commit(self):
        self.ktable.commit()
        self.refresh()
    
    def rollback(self):
        self.ktable.rollback()
    
    def refresh(self):
        '''Refresca la tabla'''
        self.ktable.fireTableChanged()
    
    def is_empty(self):
        return self.ktable.isEmpty()
    
    def __len__(self):
        return self.ktable.size()
    
    def __eq__(self, other):
        return type(self) == type(other) and self.ktable.equals(other.ktable)
        #  la comprobacion de tipo tambien comprueba que other no sea None (self nunca lo es)
#end class PKTable

class DbDataSource:
    '''Acceso a tablas de bases de datos (wrapper de TableRecordDataSource).'''
    def __init__(self, trds):
        '''TableRecordDataSource -> DbDataSource'''
        self.trds = trds
    
    def get_schema(self):
        return self.trds.getSchema()
    
    #TODO get_by_attributes, etc.
    
    def get_by_attribute(self, attrname, value):
        '''string, object -> PKRecord o None'''
        records = self.trds.getByAttribute([attrname], [value])
        if len(records) == 0:
            return None
        
        return PKRecord(self, records.pop())
    
    def get_by_attributes(self, attrnames, values):
        '''List(string), List(object) -> List(PKRecord)
        Puede devolver una lista vacía, pero nunca None'''
        records = self.trds.getByAttribute(attrnames, values)
        pkrecords = []
        while len(records) > 0:
            pkrecords.append(PKRecord(self, records.pop()))
        return pkrecords
    
    def add(self, pkrecord):
        self.trds.add(pkrecord.krecord)
    
    def update(self, pkrecord):
        self.trds.update(pkrecord.krecord)
    
    def commit(self):
        self.trds.commit()
    
    def rollback(self):
        self.trds.rollback()
    
    def setup(self, tablename, pkname):
        '''Configura el datasource pasado como parámetro con los valores indicados.'''
        self.trds.setTableName(tablename)
        self.trds.setName(tablename)
        self.trds.buildSchema()
        self.trds.setPkName(pkname)
    
    def get_iterator(self, where=None, orderby=None):
        return DbDataSourceIterator(self, where, orderby)
    
    def __iter__(self):
        return DbDataSourceIterator(self)
#end class DbDataSource

class DbDataSourceIterator:
    '''Mucho ojo cuando se use este iterador. Al crear un DbDataSourceIterator se abre una conexión con la 
    base de datos por lo que al terminar hay que cerrar dicha conexión (ejecutando close()). Cuando
    el iterador llega al final y no tiene más elementos que devolver cierra automáticamente la conexión
    pero si cabe la posibilidad de que no se llegue hasta el final conviene ejecutar close() explícitamente.'''
    def __init__(self, datasource, where=None, orderby=None):
        '''DbDataSource[, string, string] -> DbDataSourceIterator'''
        self.datasource = datasource
        if where is None and orderby is None:
            self.it = datasource.trds.getIterator()
        elif where is not None and orderby is None:
            self.it = datasource.trds.getIterator(where)
        else:
            self.it = datasource.trds.getIterator(where, orderby)
    
    def __iter__(self):
        return self
    
    def next(self):
        if self.it.hasNext():
            krecord = self.it.next()
            return PKRecord(self.datasource, krecord)
        else:
            self.close()
            raise StopIteration
    
    def close(self):
        self.it.close()

class PKRecord:
    _STATUS_NOT_REMOVED = 0
    _STATUS_TO_BE_REMOVED = 1
    _STATUS_REMOVED = 2
    
    def __init__(self, dbdatasource, krecord=None):
        '''krecord es de tipo org.saig.core.model.data.Record o None (en cuyo caso se crea un nuevo registro).'''
        if dbdatasource is None: # TODO comprobar que es de tipo org.saig.core.model.data.Record
            raise TypeException('No se pudo crear el registro, el tipo del argumento no era dbDataSource')
        
        if krecord:
            self.krecord = krecord
            self.isnewfeature = False
        else:
            self.krecord = Record(dbdatasource.get_schema())
            self.isnewfeature = True
        
        self.dbdatasource = dbdatasource
        self.status = PKRecord._STATUS_NOT_REMOVED
    
    def get_schema(self):
        return PKSchema(self.krecord.getSchema())
    
    def get_attribute(self, attrname):
        '''string -> object'''
        #if self.has_attribute(attrname):
        return self.krecord.getAttribute(attrname)
    
    def set_attribute(self, attrname, attrvalue):
        '''Devuelve self para poder hacer llamadas encadenadas:
        unrecord.set_attribute('name', 'Jose Luis').set_attribute('apellido', 'Moreno')'''
        self.set_attributes({attrname:attrvalue})
        return self
        #TODO ¿si no tiene ese atributo debería dar una excepción?
        #FIXME falla con atributos BigInteger (int8 en postgresql), el pete lo da en el commit (revisar también PKFeature)
    
    def set_attributes(self, attrs):
        '''Devuelve self para poder hacer llamadas encadenadas.'''
        if(#self.has_attribute(attrname) and
            self.status not in (PKRecord._STATUS_TO_BE_REMOVED, PKRecord._STATUS_REMOVED)):
            
            for (attrname, attrvalue) in attrs.iteritems():
                self.krecord.setAttribute(attrname, attrvalue)
        return self
    
    def has_attribute(self, attrname):
        return self.get_schema().has_attribute(attrname)
    
    def save(self, docommit=True):
        '''Guarda el registro con los cambios que haya sufrido. Puede lanzar excepciones.
        Se guardan todos los registros de la tabla que hayan sido modificados en la tabla, 
        no solo el que hace save.
        Si docommit es False, se actualiza el estado del registros pero no se hace commit 
        en el origen de datos; es mucho mas eficiente hacer cambios en muchos registros con
        docommit False y al final hacer un commit en la tabla.'''
        if self.status == PKRecord._STATUS_REMOVED:
            raise Exception('El registro que ha intentado guardar había sido eliminado previamente.')
        
        try:
            if self.status == PKRecord._STATUS_TO_BE_REMOVED:
                self.dbdatasource.remove(self) #(self.krecord)
                self.status = PKRecord._STATUS_REMOVED
            elif self.isnewfeature:
                self.dbdatasource.add(self)
                self.isnewfeature = False
            else:
                self.dbdatasource.update(self)
            
            if docommit:
                self.dbdatasource.commit()
                #PKTable(self.ktable).refresh()
        
        except Exception:
            self.dbdatasource.rollback()
            raise
    
    def remove(self):
        '''Elimina el record. Los cambios no se harán efectivos en el origen de datos
        hasta hacer save (o commit en la tabla)'''
        if self.status not in (PKRecord._STATUS_TO_BE_REMOVED, PKRecord._STATUS_REMOVED):
            self.status = PKRecord._STATUS_TO_BE_REMOVED
    
    def get_dbdatasource(self):
        return self.dbdatasource
    
    def __getitem__(self, key):
        return self.get_attribute(key)
    
    def __setitem__(self, key, value):
        return self.set_attribute(key, value)
    
    def __eq__(self, other):
        return type(self) == type(other) and self.krecord.equals(other.krecord)
        #  la comprobacion de tipo tambien comprueba que other no sea None (self nunca lo es)
    
    #TODO getPrimaryKey
#end class PKRecord

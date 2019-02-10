# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8

from com.vividsolutions.jts.io import WKTReader
from com.vividsolutions.jts.io import WKTWriter
from com.vividsolutions.jts.geom import Envelope

def wkt_to_geometry(wkt):
    '''string -> JTS geometry'''
    return WKTReader().read(wkt)

def geometry_to_wkt(geometry):
    '''JTS geometry -> string'''
    return WKTWriter().write(geometry)



#class PKCoordinate:
    #TODO


class PKEnvelope:
    def __init__(self, kenvelope=None):
        '''kenvelope es un envelope de JTS'''
        if kenvelope:
            self.kenvelope = kenvelope
        else:
            self.kenvelope = Envelope()
    
    def bottom(self):
        return self.kenvelope.getMinY()
    
    def top(self):
        return self.kenvelope.getMaxY()
    
    def left(self):
        return self.kenvelope.getMinX()
    
    def right(self):
        return self.kenvelope.getMaxX()
    
    def is_null_envelope(self):
        return self.kenvelope.isNull()
    
    def set_to_null(self):
        '''Hace que este envelope se corresponda con el de una
        geometria vacia'''
        self.kenvelope.setToNull()
    
    def width(self):
        return self.kenvelope.getWidth()
    
    def height(self):
        return self.kenvelope.getHeight()
        
    def expand_by(self, xfactor, yfactor=None):
        '''Aumenta este envelope en las direcciones x e y la cantidad indicada.
        Si yfactor no se indica, se usara xfactor en las dos direcciones.
        Siempre devuelve None'''
        if yfactor == None:
            yfactor = xfactor
        self.kenvelope.expandBy(xfactor, yfactor)
    
    def expand_to_include_point(self, x, y):
        '''Aumenta este envelope para que incluya el punto x, y.
        Siempre devuelve None'''
        self.kenvelope.expandToInclude(x, y)
    
    def expand_to_include_envelope(self, pkenvelope):
        '''Aumenta este envelope para incluir pkenvelope (que es un PKEnvelope).
        Siempre devuelve None'''
        self.kenvelope.expandToInclude(pkenvelope.kenvelope)
    
    def contains_point(self, x, y):
        return self.kenvelope.contains(x, y)
    
    def contains_envelope(self, pkenvelope):
        '''Devuelve True si pkenvelope esta completamente dentro de este envelope'''
        return self.kenvelope.contains(pkenvelope.kenvelope)
    
    def intersection(self, pkenvelope):
        return PKEnvelope(self.kenvelope.intersection(pkenvelope.kenvelope))
    
    def clone(self):
        '''Devuelve un PKEnvelope identido (pero sin relacion alguna) a este'''
        return PKEnvelope(self.kenvelope.clone())

    def __getitem__(self, key):
        if key == 'bottom' or key == 'ymin' or key == 'miny' or key == 1:
            return self.bottom() #min_y
        elif key == 'top' or key == 'ymax' or key == 'maxy' or key == 3:
            return self.top() #max_y
        elif key == 'left' or key == 'xmin' or key == 'minx' or key == 0:
            return self.left() #min_x
        elif key == 'right' or key == 'xmax' or key == 'maxx' or key == 2:
            return self.right() #max_x

    def __str__(self):
        '''Devuelve una cadena como "PKEnvelope[xmin, ymin, xmax, ymax]"'''
        return 'PKEnvelope[' \
                + self.left() + ', ' \
                + self.bottom() + ', ' \
                + self.right() + ', ' \
                + self.top() + ']'

    def __eq__(self, other):
        return type(self) == type(other) and self.kenvelope.equals(other.kenvelope)
        #  la comprobacion de tipo tambien comprueba que other no sea None (self nunca lo es)

#end class PKEnvelope
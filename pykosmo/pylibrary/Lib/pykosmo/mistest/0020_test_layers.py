# This Python file uses the following encoding: utf-8

#Francisco Abato Helguera - pacoabato@gmail.com

#Test para el módulo layers.py



def initialize():
    pass

def finalize():
    pass



try:
    initialize()
    
    pyk.layers.add_new_layer(pyk.layers.PKSchema.GEOMETRY_TYPE_POLYGON, 'polígonos', 'categoría')
    
    esquema = pyk.layers.PKSchema()
    prim_key = 'prim_key'
    attr_name = 'attr_name'
    esquema.set_geometry_type(pyk.layers.PKSchema.GEOMETRY_TYPE_POINT)
    esquema.add_attribute(prim_key, pyk.layers.PKSchema.ATTRIBUTE_TYPE_STRING, True)
    esquema.add_attribute(attr_name, pyk.layers.PKSchema.ATTRIBUTE_TYPE_INTEGER, False)
    
    pyk.layers.add_new_layer(pyk.layers.PKSchema.GEOMETRY_TYPE_POINT, 'puntos', 'categoría', esquema)
    
    
    pyk.layers.add_new_layer_gui()
    
    pyk.layers.load_layer_gui()
    
    
finally:
    finalize()

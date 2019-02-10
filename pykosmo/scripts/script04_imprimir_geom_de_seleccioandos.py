# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8

'''Este script imprime en el log la geometría (en WKT) de los features seleccionados en la capa seleccionada.'''


tuberias_layer = pykosmo.Layers.get_layer('Tuberías')

if tuberias_layer is not None:
    features_list = tuberias_layer.get_selected_features()
    geoms_wkt = [pykosmo.geometry_to_wkt(feat.get_geometry()) for feat in features_list]
    for geom in geoms_wkt:
        pykosmo.log(geom)
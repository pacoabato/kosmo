

import sys, os, traceback, zipfile

import re
path = r'D:\workspace\library'

pattern = r'log4jetty'

class Cont:
    def __init__(self):
        self.cont = 0
    
    def suma(self):
        self.cont = self.cont + 1
    
    def __str__(self):
        return str(self.cont)

cont = Cont()


def buscaarchivos(dirpath):
    '''string -> lista de string
    dirpath es la ruta completa de un directorio. Devuelve una lista cuyos elementos son 
    cadenas con la ruta completa de cada archivo dentro del directorio
    (hace una busqueda recursiva en los subdirectorios).'''
    
    lista = []
    
    recursive(dirpath, lista)
    return lista
    
def recursive(dirpath, lista):
    for f in os.listdir(dirpath):
        
        if f != '.svn':
            fullfilepath = os.path.join(dirpath, f)
            
            if os.path.isdir(fullfilepath):
                recursive(fullfilepath, lista)
            else:
                lista.append(fullfilepath)

def comparapatron(text, filename, cont, secondfile = ''):
    if text is not None and text != '' and text.find(pattern) != -1:
        cont.suma()
        print 'Encontrado en: ' + filename
        print 'Subarchivo: ' + secondfile
        print 'Text was: ' + text

def buscaenzip(file, cont, origen):
    z = zipfile.ZipFile(file, "r")
    for filename in z.namelist():
        if zipfile.is_zipfile(filename):
            buscaenzip(filename, cont, origen + file)
        else:
            text = z.read(filename)
            comparapatron(text, origen, cont, filename)



#START execution


filesfound = buscaarchivos(path)
for file in filesfound:

#path = r'D:\workspace\library\cacadelavaca.txt'
#for file in (path, ):
    if zipfile.is_zipfile(file):
        buscaenzip(file, cont, file)
    else:
        f = open(file)
        try:
            text = f.read()
            comparapatron(text, file, cont)
        finally:
            f.close()

print 'Archivos procesados: ' + str(cont)


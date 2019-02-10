# This Python file uses the following encoding: utf-8

#Francisco Abato Helguera - pacoabato@gmail.com

#Test para el módulo pyk.py



def initialize():
    pass

def finalize():
    pass



try:
    initialize()
    
    context = pyk.get_context()

    if context is None:
        raise Exception('pyk.get_context() devolvió None.')
    
    if pyk.get_task_manager() is None:
        raise Exception('pyk.get_task_manager() devolvió None.')
    
    tasks = pyk.JUMPWorkbench.getFrameInstance().getContext().getTaskManager().getTasks()
    for task in tasks:
        if pyk.get_task(task.getName()) is None:
            raise Exception('pyk.get_task(taskName) devolvió None.')
    
    if pyk.get_layer_manager() is None:
        raise Exception('pyk.get_layer_manager() devolvió None.')
    
    if pyk.get_layer_view_panel() is None:
        raise Exception('pyk.get_layer_view_panel() devolvió None.')
    
    if pyk.get_layer_name_panel() is None:
        raise Exception('pyk.get_layer_name_panel() devolvió None.')
    
    if pyk.get_selection_manager() is None:
        raise Exception('pyk.get_selection_manager() devolvió None.')
    
    
    
finally:
    finalize()

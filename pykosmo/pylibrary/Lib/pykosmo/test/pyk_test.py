# -*- coding: utf-8 -*-
# This Python file uses the following encoding: utf-8


import unittest
reload(unittest)


def execute():
    print 'caquisima'
    thetest = TestPyk()
    print thetest
    suite = unittest.TestLoader().loadTestsFromTestCase(thetest)
    unittest.TextTestRunner(verbosity=2).run(suite)
    

if __name__ == '__main__':
    print 'merde'
    unittest.main()

class TestPyk(unittest.TestCase):
    def test_get_context(self):
        #context = pyk.get_context()
        #print str(context)
        #self.assertNotEquals(context, None)
        print 'cacota'
        self.assertEquals('a', 'a')

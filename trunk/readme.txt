=============================================================
            PriDE 2.5.0, 2014-09-07
            (c) The PriDE team and arvato IT services
=============================================================

See license.txt for license conditions!
See doc/PriDE-History.html for release notes!


Building PriDE
--------------
PriDE comes with an Ant script which allows to build the core
library by performing "ant jar". Make shure to prepare the
environment correctly by adapting the properties in build.xml
and optionally running the shrc.bat file when working a DOS
shell.

If you like to compile PriDE from within your favourite IDE,
just create your own project and add the sub-directories 'src',
'test', and 'examples' as source folders. The folder 'plugin'
contains the source code of the PriDE Eclipse Plugin and can
only be loaded by Eclipse 2.0 or higher in a plugin project.
The folder 'ant' containes the source code for PriDE's Ant task
and requires ant.jar from an Ant distribution 1.6.x or higher.

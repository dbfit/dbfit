name             'dbfit_test'
maintainer       'Jake Benilov'
maintainer_email 'benilov@gmail.com'
license          'GNU General Public License, version 2'
description      'Creates the test databases/set grants for running DbFit tests'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version          '0.1.0'
depends          'mysql'
depends          'postgresql'
depends          'database'
depends          'maven'
depends          'gradle'
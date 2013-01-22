# DbFit

DbFit is a set of fixtures which enables FIT/FitNesse tests to execute directly against a database.

## Contributing

### Setting up the test VM (on Mac OS or Linux)

1.  You first need to [install VirtualBox](https://www.virtualbox.org/wiki/Downloads). I have been using version 4.1.18.

2.  Install ruby `bundler`:

        sudo gem install bundler

3.  Run every subsequent command from the `test_vm` folder:

        cd test_vm

4.  Install the necessary ruby gems (including `vagrant`):

        bundle install

5.  Install the `vagrant` recipes:

        bundle exec librarian-chef install

6.  Provision and start the vagrant VM:

        bundle exec vagrant up

## License

DbFit is released under the [GNU General Public License, version 2](http://www.gnu.org/licenses/gpl-2.0.txt).

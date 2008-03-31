
# Set here the version of the .war file and owsproxyserver archive to generate
WAR_VERSION := YYYYMMDD

# The home of your Eclipse workspace
ECLIPSE_HOME := /path/to/workspace

# We use the webapp generated from Eclipse. Put here the path that Eclipse uses. It is relative to the workspace.
# The path should look like: .metadata/.plugins/org.eclipse.wst.server.core/tmp{NUMBER}/wtpwebapps/owsproxyserver, where {NUMBER} should be 0, 1, ...
# The number can be found by running the project from Eclipse, and look at the process parameters Eclipse used.
# There should be a -Dcatalina.base=... value showing the "tmp{NUMBER}" used.
WEBAPP_HOME := .metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/owsproxyserver

# This is the .war file used for the tests. It shouldn't need to be changed
TEST_WAR := war/$(WAR_VERSION)/owsproxyserver.war


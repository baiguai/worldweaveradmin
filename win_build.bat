# BUILD SCRIPT
################################################################################
#
#
# Purpose:
# Builds the WorldWeaver Admin tool.
#
################################################################################

javac -cp .:Release/lib/* -d ./Compiled ./Code/*.java
cd Compiled
jar -cfm ../Release/WorldWeaverAdmin.jar manifest.txt *.class

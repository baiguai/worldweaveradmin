# MACRO SCRIPT
################################################################################
#
#
# Purpose:
# Runs the specified macro. Accepts a single parameter - the macro file name.
#
################################################################################

cd Release
java -jar WorldWeaverAdmin.jar -m $1

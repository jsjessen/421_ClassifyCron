NAME := ClassifyCron
LIBS := .:libs/mongo-java-driver-2.13.3.jar:libs/weka.jar:libs/commons-math3-3.5.jar 
SRCS := src/*.java

all:
	javac -classpath $(LIBS) -d . $(SRCS)

run:
	java -classpath $(LIBS) com.ireach.$(NAME)

q: | all run

# Use this line to execute:
# java -classpath .:libs/mongo-java-driver-2.13.3.jar:libs/weka.jar:libs/commons-math3-3.5.jar com.ireach.ClassifyCron

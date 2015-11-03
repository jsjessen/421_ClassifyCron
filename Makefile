NAME := ClassifyCron
LIBS := .:libs/mongo-java-driver-2.13.3.jar:libs/weka.jar
SRCS := src/*.java

all:
	javac -classpath $(LIBS) $(SRCS)

run:
	java -classpath $(LIBS) ireach.Main #$(SRCS:.java=.class)

q: | all run

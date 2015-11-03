NAME := ClassifyCron
LIBS := .:libs/mongo-java-driver-2.13.3.jar:libs/weka.jar
SRCS := src/*.java

all:
	javac -classpath $(LIBS) -d . $(SRCS)

run:
	java -classpath $(LIBS) com.ireach.$(NAME)

q: | all run

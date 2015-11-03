NAME := ClassifyCron
LIBS := .:libs/mongo-java-driver-2.13.3.jar:libs/weka.jar

all:
	javac -cp $(LIBS) $(NAME).java

run:
	java -cp $(LIBS) $(NAME)

q: | all run

CC=gcc
LFLAGS=-lpulse -lpulse-simple -lpthread

wavepipe: parec.c parec.h main.c
	${CC} $^ -o $@ ${LFLAGS}

clean:
	rm -rf *.o *.gch wavepipe

.PHONY: clean

#include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>

#include "parec.h"

uint8_t rdy;
pthread_mutex_t samples_buf_access;
pthread_t pa_thread;
int sockfd;

int16_t samples_buffer[NSAMP];
char cmd[10];

void *pa_main() {
    pain_init();
    while (1) {
        if (!rdy) {
            pthread_mutex_lock(&samples_buf_access);
            pain_read(samples_buffer, NSAMP);
            rdy = 1;
            pthread_mutex_unlock(&samples_buf_access);
        }

    }

    return 0;
}

int main() {

    // setup socket
    int nrecv;
    int nsend;
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    struct sockaddr_in serv_addr, client_addr;
    socklen_t addr_size = sizeof(client_addr);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(9134);
    serv_addr.sin_addr.s_addr = inet_addr("0.0.0.0"); // use public addr

    if (bind(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr))) {
        fprintf(stderr, "bind failed...\n");
        return -1;
    }

    while (strcmp(cmd, "OPEN")) {
        bzero(cmd, 10);
        nrecv = recvfrom(sockfd, cmd, 10, 0, (struct sockaddr *)&client_addr, &addr_size);
        /* printf("%d\n", nrecv); */
        printf("recv: %s\n", cmd);
    }

    rdy = 0; // init ready to false
    pthread_mutex_init(&samples_buf_access, 0); // init access mutex
    pthread_create(&pa_thread, NULL, pa_main, NULL);

    while (1) {
        if (rdy) {
            // BEGIN critical section
            pthread_mutex_lock(&samples_buf_access);
            // send bytes on socket
            nsend = sendto(sockfd, samples_buffer, sizeof(samples_buffer), 0, (struct sockaddr *)&client_addr, addr_size);
            rdy = 0;
            pthread_mutex_unlock(&samples_buf_access);
            // END critical section
        }
    }



finish:
    pain_close();
    close(sockfd);
    return 0;
}

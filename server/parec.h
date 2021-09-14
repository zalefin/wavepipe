#include <pulse/simple.h>

#define NSAMP 64

#define RATE 22000

static pa_simple *s = NULL;
static const pa_sample_spec ss = {
    .format = PA_SAMPLE_S16LE, // signed 16 bit little endian
    .rate = RATE,
    .channels = 1
};

int pain_init();
void pain_close();
int pain_read();

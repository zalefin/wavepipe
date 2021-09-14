#include <stdio.h>
#include <pulse/simple.h>
#include <pulse/error.h>

#include "parec.h"

int pain_init() {
    int err;
    if (!(s = pa_simple_new(
                    NULL,
                    "Wavepipe",
                    PA_STREAM_RECORD,
                    NULL,
                    "record",
                    &ss,
                    NULL, NULL, &err
                    )))
    {
        return 1;
    }
    return 0;
}

void pain_close() {
    if (s) {
        pa_simple_free(s);
    }
}

int pain_read(int16_t *samples_buf, unsigned n_samp) {
    int n_read;
    int err;
    n_read = pa_simple_read(s, samples_buf, n_samp * sizeof(int16_t), &err);
    if (err) {
        return -1;
    }
    return n_read;
}


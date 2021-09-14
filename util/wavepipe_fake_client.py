import socket
import sys

RATE = 8000
BUFSIZE = 64

target_addr = ("127.0.0.1", 9134)
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# send open cmd
s.sendto(b"OPEN", 0, target_addr)

try:
    while True:
        dat, addr = s.recvfrom(BUFSIZE * 2)
        # print("wrote bytes", file=sys.stderr)
        sys.stdout.buffer.write(dat)
except KeyboardInterrupt:
    s.sendto(b"CLOSE", 0, target_addr)

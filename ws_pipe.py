import websocket
import thread
import sys
import time

do_close = False
msg_count = 0

def on_message(ws, message):
    global do_close
    global msg_count
    msg_count -= 1
    sys.stdout.write(message)
    if do_close and msg_count == 0:
        ws.close()

def on_error(ws, error):
    print error

def on_close(ws):
    pass

def on_open(ws):
    def run(*args):
        global do_close
        global msg_count
        line = sys.stdin.readline()
        while line:
            msg_count += 1
            ws.send(line)
            line = sys.stdin.readline()
        do_close = True
    thread.start_new_thread(run, ())


if __name__ == "__main__":
    #websocket.enableTrace(True)
    if len(sys.argv) != 2:
        print "Usage: python ws_pipe.py ws://server/address"
        exit(-1)
    ws = websocket.WebSocketApp(sys.argv[1],
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    ws.on_open = on_open
    ws.run_forever()

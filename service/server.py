import http.server
import socketserver
import os
from pathlib import Path


PORT = os.environ.get('PORT', 8080)

class CustomRequestHandler(http.server.SimpleHTTPRequestHandler):

    def do_GET(self):
        path = self.path
        do_file = Path("/opt/app/settings/domaintancemode")
        if do_file.is_file():
               # file exists
               self.send_response(503, 'Service Unavalible')
               self.send_header('Content-Type', 'text/html; charset=utf-8')
               self.end_headers()
               #self.wfile.write(bytes("","utf-8"))
        else:
               if '/ping' in path:
                    self.send_response(200, 'OK')
                    self.send_header('Content-Type', 'text/html; charset=utf-8')
                    self.end_headers()
                    #self.wfile.write(bytes("","utf-8"))
               else:
                    if '/version' in path:
			with open('./index.html', 'rb') as f:
			    self.send_response(200)
			    self.send_header('Content-Type', 'text/html; charset=utf-8')
			    self.end_headers()
			    self.wfile.write(f.read())


httpd = socketserver.TCPServer(("", int(PORT)), CustomRequestHandler)
print("Python web server listening on port {}...".format(PORT))
httpd.serve_forever()

# docker-helloworld-http

A Python HTTP server which responds to any GET request with an HTTP 200.

## Usage

Start the server, listening for traffic on port 8080 of localhost:

```sh
docker build --tag cts:1.0 .
docker run -it -p 8080:8080 -e PORT=8080 --name cts cts:1.0
docker ps | grep cts
docker exec -it 66e87c202c19 bash
```

```sh
$curl -i -H GET  'http://localhost:8080/ping'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 17:22:44 GMT
Content-Type: text/html; charset=utf-8

```

```sh
$curl -i -H GET  'http://localhost:8080/version'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 17:23:05 GMT
Content-Type: text/html; charset=utf-8

It works!

Revision 1

```

docker exec -it <mycontainer> bash
docker exec -it <mycontainer> touch /opt/app/do_maintance_mode

```
$curl -i -H GET  'http://10.244.0.50:8080/ping'
HTTP/1.0 503 Service Unavalible
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 21:15:08 GMT
Content-Type: text/html; charset=utf-8
```
docker exec -it <mycontainer> rm /opt/app/do_maintance_mode
```
$curl -i -H GET  'http://10.244.0.50:8080/ping'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 21:16:03 GMT
Content-Type: text/html; charset=utf-8
```

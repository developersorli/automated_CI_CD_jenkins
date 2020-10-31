# docker-helloworld-http

A Python HTTP server which responds to any GET request with an HTTP 200.

## Usage

Start the server, listening for traffic on port 8080 of localhost:

```sh
docker build --tag fun7:1.0 .
docker run -v /home/programer/Documents/devops/settings/:/opt/app/settings/ -it -p 8080:8080 -e PORT=8080 --name bb fun7:1.0
docker ps | grep fun7
docker exec -it 66e87c202c19 bash
```

```sh
curl -i -H GET  'http://localhost:8080/ping'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 17:22:44 GMT
Content-Type: text/html; charset=utf-8

```

```sh
curl -i -H GET  'http://localhost:8080/version'
HTTP/1.0 200 OK
Server: SimpleHTTP/0.6 Python/3.8.5
Date: Sat, 31 Oct 2020 17:23:05 GMT
Content-Type: text/html; charset=utf-8

Hello World!

```

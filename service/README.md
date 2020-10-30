# docker-helloworld-http

A Python HTTP server which responds to any GET request with an HTTP 200.

## Usage

Start the server, listening for traffic on port 8080 of localhost:

```sh
docker build --tag fun7:1.0 .
docker run -v /home/programer/Documents/devops/settings/:/opt/app/settings/ -it -p 8080:8080 -e PORT=8080 --name bb fun7:1.0
```

```sh
curl -i -H GET  'http://localhost:8080/ping'
```

```sh
curl -i -H GET  'http://localhost:8080/version'

```

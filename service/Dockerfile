FROM python:3.6.3-alpine3.6

ENV PORT 8080

WORKDIR /opt/app/
COPY . /opt/app/
ENTRYPOINT ["python", "./server.py"]
EXPOSE ${PORT}


# Import a base image
FROM alpine

ENV PORT 8080

# Install Python and the required dependencies
RUN apk add --update --no-cache build-base python3-dev python3 libffi-dev libressl-dev bash git gettext curl \
 && curl -O https://bootstrap.pypa.io/get-pip.py \
 && python3 get-pip.py \
 && pip install --upgrade six awscli awsebcli

# Open the shell
RUN sh
# Install the required python modules
RUN pip3 install httpserver

# Create a work directory to copy the files into (to prevent accidental overrides in the root directory)
WORKDIR /opt/app/

COPY . /opt/app/
ADD index.html /opt/app/
EXPOSE ${PORT}
#ENTRYPOINT ["python", "./server.py"]
CMD ["python3", "server.py"]


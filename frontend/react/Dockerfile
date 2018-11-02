FROM node:latest
# Create app directory
RUN mkdir -p  /usr/src/app

# Set working DIR
WORKDIR /usr/src/app

COPY package.json ./

COPY package.json /usr/src/app/package.json

RUN npm install

RUN npm install react-scripts@1.1.0 -g

COPY . .

EXPOSE 3000

CMD [ "npm", "start" ]

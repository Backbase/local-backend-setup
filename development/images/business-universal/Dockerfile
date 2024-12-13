FROM node:20 AS builder

ARG APP_VERSION
RUN --mount=type=secret,id=jfrog,dst=./.netrc curl -v --netrc-file .netrc -s "https://repo.backbase.com/artifactory/backbase-releases/com/backbase/apps/business-banking-universal-app/business-banking-universal-app-${APP_VERSION}.zip" -o business-banking-universal-app-package.zip
RUN unzip business-banking-universal-app-package.zip -d /tmp/business-banking-universal-app-package

WORKDIR /tmp/business-banking-universal-app-package

RUN npm i -g nx
RUN --mount=type=secret,id=npm,dst=./.npmrc npm ci --ignore-scripts --legacy-peer-deps
RUN nx build business-universal

FROM repo.backbase.com/backbase-docker-releases/web-base:2.2.2

COPY --from=builder /tmp/business-banking-universal-app-package/dist/apps/business-universal /statics

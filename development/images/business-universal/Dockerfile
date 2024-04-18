FROM node:16 AS builder

RUN --mount=type=secret,id=repo,dst=./.netrc curl -v --netrc-file .netrc -s "https://repo.backbase.com/artifactory/backbase-releases/com/backbase/apps/business-banking-universal-app/business-banking-universal-app-2024.03.1-LTS.zip" -o business-banking-universal-app-package.zip
RUN unzip business-banking-universal-app-package.zip -d /tmp/business-banking-universal-app-package

WORKDIR /tmp/business-banking-universal-app-package

RUN npm i -g nx replace-in-file
RUN --mount=type=secret,id=npm,dst=./.npmrc npm ci --ignore-scripts --legacy-peer-deps

RUN replace-in-file "defaultCountry: 'NL'" "defaultCountry: 'GB'" "apps/business-universal/**/*.ts"
RUN nx build business-universal

FROM repo.backbase.com/backbase-docker-releases/web-base:2.0.10

COPY --from=builder /tmp/business-banking-universal-app-package/dist/apps/business-universal /statics

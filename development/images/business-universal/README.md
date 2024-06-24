# Business Essentials Web

This image is not available in any Backbase public repository.

You have the following options:

## Pulling from Harbor internal (VPN connection required)

After connecting to Backbase VPN you can run:
```shell
docker login harbor.backbase.eu
docker pull --platform=linux/amd64 harbor.backbase.eu/development/business-universal:2024.03-LTS
```

## Building the image locally

You can build it by using the following commands:

```shell
# If not there yet, create a .npmrc file using your credentials.
curl -s -u"<USERNAME>:<PASSWORD/TOKEN>" https://repo.backbase.com/api/npm/npm-backbase/auth/backbase > ~/.npmrc

# If not there yet, create a .netrc file using your credentials.
echo "machine repo.backbase.com login <USERNAME> password <PASSWORD/TOKEN>" > ~/.netrc

# Build the image using your existing credentials as a build secret.
docker build --platform=linux/amd64 --secret id=npm,src=$(echo $HOME)/.npmrc --secret id=repo,src=$(echo $HOME)/.netrc -t harbor.backbase.eu/development/business-universal:2024.03-LTS .
```

> Mounting the secret as `.netrc` is required to download the app from the Backbase JFrog repository, and `.npmrc` file is necessary to fetch the NPM dependencies on Backbase private registry.
# Rancher Cloudflare #

[![Docker Pulls](https://img.shields.io/docker/pulls/scalified/rancher-cloudflare.svg)](https://hub.docker.com/r/scalified/rancher-cloudflare)
[![](https://images.microbadger.com/badges/image/scalified/rancher-cloudflare.svg)](https://microbadger.com/images/scalified/rancher-cloudflare)
[![](https://images.microbadger.com/badges/version/scalified/rancher-cloudflare.svg)](https://microbadger.com/images/scalified/rancher-cloudflare)

## Description

The project automatically detects ingress records within [Rancher](https://rancher.com/) and applies changes on [Cloudflare](https://www.cloudflare.com/) DNS

## Dockerhub

**`docker pull scalified/rancher-cloudflare:<version>`**

## Version

| Version    | Rancher API Version | Cloudflare API Version |
|------------|---------------------|------------------------|
| **latest** |         v3          |            v4          |

### How-To

#### Usage

The following environment variables must be provided:

| Environment Variable | Example                                                | Description                                                                                             |
|----------------------|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| RANCHER_URL          | https://rancher.example.com                            | Rancher Server URL                                                                                      |
| RANCHER_ACCESS_KEY   | token-dukhk                                            | The token’s username. See [API Keys](https://rancher.com/docs/rancher/v2.x/en/user-settings/api-keys/)  |
| RANCHER_SECRET_KEY   | f6sdd2gt2t5gs779fpg9km6fl4d46nkfcnknqzjwszwb6jm4qfh48y | The token’s password. See [API Keys](https://rancher.com/docs/rancher/v2.x/en/user-settings/api-keys/)  |
| IP_ADDRESS           | 50.20.120.200                                          | IP Address to be written as A record content                                                            |
| CLOUDFLARE_ZONE_ID   | 4ff62716278c165a1592992bde7ff64d                       | Cloudflare Zone Id. See [Api Documentation](https://api.cloudflare.com/#getting-started-resource-ids)   |
| CLOUDFLARE_API_KEY   | 794ce33565fec7fc069e0cdfb9c43a0dd1221                  | Cloudflare API Key See [Api Documentation](https://api.cloudflare.com/#getting-started-resource-ids)    |
| CLOUDFLARE_EMAIL     | mail@example.com                                       | Cloudflare account email                                                                                |

#### Assembling

1. Build the Project:  
   `./gradlew clean build`
2. Build Docker image:  
   `docker build . -t <tag> --build-arg JAR_FILE=rancher-cloudflare/rancher-cloudflare.jar`

## Scalified Links

* [Scalified](https://www.scalified.com)
* [Scalified Official Facebook Page](https://www.facebook.com/scalified)
* <a href="mailto:info@scalified.com?subject=[Rancher Cloudflare]: Proposals And Suggestions">Scalified Support</a>
* [**SweetCV - Online Resume Builder**](https://sweetcv.com)
